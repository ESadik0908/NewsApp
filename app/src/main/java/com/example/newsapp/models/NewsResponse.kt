package com.example.newsapp.models

data class NewsResponse(
    val articles: MutableList<Article>,
    val totalArticles: Int
)