package com.example.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

//News view models that generate news responses of type <Article> based on their inputs and
// news query type
class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1

    val localNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var localNewsPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    val spotlightNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var spotlightNewsPage = 1

    val topTenNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var topTenNewsPage = 1


    init {
        getTopTenNews(10)
    }

    fun getBreakingNews(countryCode: String, q: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, q, breakingNewsPage)
        breakingNews.postValue(breakingNewsHandler(response))
    }

    fun getLocalNews(q: String) = viewModelScope.launch {
        localNews.postValue(Resource.Loading())
        val response = newsRepository.getLocalNews(q, localNewsPage)
        localNews.postValue(localNewsHandler(response))
    }

    private fun getTopTenNews(limit: Int) = viewModelScope.launch {
        topTenNews.postValue(Resource.Loading())
        val response = newsRepository.topTenNews(limit, topTenNewsPage)
        topTenNews.postValue(topTenNewsHandler(response))
    }

    fun getSearchedNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(searchNewsHandler(response))
    }

    fun getSpotlightNews(spotlight: String) = viewModelScope.launch {
        spotlightNews.postValue(Resource.Loading())
        val response = newsRepository.getSpotlightNews(spotlight, spotlightNewsPage)
        spotlightNews.postValue(spotlightNewsHandler(response))
    }

    private fun breakingNewsHandler(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun localNewsHandler(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun topTenNewsHandler(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun searchNewsHandler(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun spotlightNewsHandler(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}