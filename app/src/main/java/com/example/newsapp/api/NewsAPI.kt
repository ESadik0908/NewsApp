package com.example.newsapp.api

import com.example.newsapp.models.NewsResponse
import com.example.newsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//This is where default searches for the news API are set up, each get calls the api link with
// different parameters based on the type of news we want
interface NewsAPI {

    @GET("api/v4/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "gb",
        @Query("q")
        q: String,
        @Query("page")
        offset: Int = 1,
        @Query("lang")
        language: String = ("en"),
        @Query("token")
        apiKey: String = API_KEY,
        @Query("sortby")
        sortby: String = ("publishedAt")
    ): Response<NewsResponse>

    @GET("api/v4/top-headlines")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        offset: Int = 1,
        @Query("lang")
        language: String = ("en"),
        @Query("token")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("api/v4/top-headlines")
    suspend fun localNews(
        @Query("q")
        searchQuery: String?,
        @Query("page")
        offset: Int = 1,
        @Query("lang")
        language: String = ("en"),
        @Query("token")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("api/v4/top-headlines")
    suspend fun topTenNews(
        @Query("max")
        limit: Int = 10,
        @Query("page")
        offset: Int = 1,
        @Query("lang")
        language: String = ("en"),
        @Query("sort")
        sort: String = ("popularity"),
        @Query("token")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("api/v4/top-headlines")
    suspend fun spotLightNews(
        @Query("q")
        searchQuery: String?,
        @Query("page")
        offset: Int = 1,
        @Query("lang")
        language: String = ("en"),
        @Query("country")
        countryCode: String = "gb",
        @Query("token")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

}