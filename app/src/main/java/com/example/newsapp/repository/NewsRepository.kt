package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDatabase

//Repository using retrofit so call news api queries
class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, q: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, q,  pageNumber)

    suspend fun getLocalNews(keywords: String, pageNumber: Int) =
        RetrofitInstance.api.localNews(keywords, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun topTenNews(limit: Int, pageNumber: Int)=
        RetrofitInstance.api.topTenNews(limit, pageNumber)

    suspend fun getSpotlightNews(spotlight: String, pageNumber: Int)=
        RetrofitInstance.api.spotLightNews(spotlight, pageNumber)
}