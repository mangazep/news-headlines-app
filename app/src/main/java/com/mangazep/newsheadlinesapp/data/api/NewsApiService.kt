package com.mangazep.newsheadlinesapp.data.api

import com.mangazep.newsheadlinesapp.data.model.NewsResponse
import com.mangazep.newsheadlinesapp.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = Constants.COUNTRY_ID,
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponse
}