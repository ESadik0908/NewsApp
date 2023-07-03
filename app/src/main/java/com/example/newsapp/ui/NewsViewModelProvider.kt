package com.example.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.repository.NewsRepository

//View model Factory
class NewsViewModelProvider(
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository) as T
    }
}