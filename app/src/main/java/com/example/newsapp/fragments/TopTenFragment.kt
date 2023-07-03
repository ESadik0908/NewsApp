package com.example.newsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_top_ten.*


class TopTenFragment : Fragment(R.layout.fragment_top_ten) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "TopTenFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecycler()

        newsAdapter.setOnItemClickListener{
            val intent = Intent(context, ArticleActivity::class.java)
            startActivity(intent.putExtra("data", it.url))
        }

        viewModel.topTenNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    hidePB()
                    response.data?.let { newsResponse -> newsAdapter.differ.submitList(newsResponse.articles) }
                }
                is Resource.Error ->{
                    hidePB()
                    response.message?.let { message ->
                        Log.e(TAG, "An Error occurred: $message")
                    }
                }
                is Resource.Loading ->{
                    showPB()
                }
            }
        })
    }

    private fun hidePB(){
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showPB(){
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecycler(){
        newsAdapter = NewsAdapter()
        rv_top_ten.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}