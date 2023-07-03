package com.example.newsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_spotlight.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpotlightFragment : Fragment(R.layout.fragment_spotlight) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    val TAG = "SpotlightFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecycler()
        init()
        getSpotlightDataFromFirebase()
        var job: Job? = null

        //load new data if the spotlight is changed
        spotlight_autoCompleteTextView.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        updateSpotlight(editable.toString())
                        viewModel.getSpotlightNews(editable.toString())
                    }
                }
            }
        }

        //load the article if clicked
        newsAdapter.setOnItemClickListener {
            val intent = Intent(context, ArticleActivity::class.java)
            startActivity(intent.putExtra("data", it.url))
        }

        //populate recyclerview
        viewModel.spotlightNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hidePB()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hidePB()
                    response.message?.let { message ->
                        Log.e(TAG, "An Error occurred: $message")
                    }
                }
                is Resource.Loading -> {
                    showPB()
                }
            }
        })
    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
            .child(firebaseAuth.currentUser?.uid.toString())
    }

    //get users spotlight from database
    private fun getSpotlightDataFromFirebase() {
        databaseReference.child("spotlight").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val spotlight: String = snapshot.value.toString()
                    spotlight_autoCompleteTextView.setText(spotlight)
                    setupDropdown()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //change spotlight to new spotlight
    private fun updateSpotlight(newSpotlight: String) {
        hashMapOf<String, Any>(
            "spotlight" to newSpotlight
        )
    }

    private fun hidePB() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showPB() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecycler() {
        newsAdapter = NewsAdapter()
        rv_spotlight.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setupDropdown() {
        val spotlights = resources.getStringArray(R.array.spotlights)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_layout, spotlights)
        spotlight_autoCompleteTextView.setAdapter(arrayAdapter)
    }

}

