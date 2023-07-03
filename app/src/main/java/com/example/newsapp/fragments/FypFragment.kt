package com.example.newsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_fyp.*

class FypFragment : Fragment(R.layout.fragment_fyp) {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private lateinit var topicsList: MutableList<String>

    lateinit var mainHandler: Handler

    //Refresh the news every 30 seconds in case the topics have been changed
    private val updateNews = object : Runnable {
        override fun run() {
            getTopics()
            mainHandler.postDelayed(this, 30000)
        }
    }

    val TAG = "FypFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecycler()
        init()
        mainHandler = Handler(Looper.getMainLooper())

        //If an article is clicked then go to the Article Activity and pass the url
        newsAdapter.setOnItemClickListener {
            val intent = Intent(context, ArticleActivity::class.java)
            startActivity(intent.putExtra("data", it.url))
        }

        //Display breaking news
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hidePB()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalArticles / QUERY_PAGE_SIZE + 2
                        lastPage = viewModel.breakingNewsPage == totalPages
                        if (lastPage) {
                            rv_fyp.setPadding(0, 0, 0, 0)
                        }
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

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateNews)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateNews)
    }

    private fun hidePB() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showPB() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var lastPage = false


    private fun setupRecycler() {
        newsAdapter = NewsAdapter()
        rv_fyp.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    //initialise the db and authentication
    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
            .child(firebaseAuth.currentUser?.uid.toString()).child("topics")
        topicsList = mutableListOf()
    }

    //get the topics from the database and search for news based on the results
    private fun getTopics() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                topicsList.clear()
                for (topicsSnapshot in snapshot.children) {
                    val topic: String = topicsSnapshot.value.toString()
                    topicsList.add(topic)
                }
                viewModel.getBreakingNews(
                    "gb",
                    topicsList.joinToString(separator = " OR ")
                )
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}
