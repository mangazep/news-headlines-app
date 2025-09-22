package com.mangazep.newsheadlinesapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mangazep.newsheadlinesapp.data.api.NewsApiService
import com.mangazep.newsheadlinesapp.data.model.Article
import com.mangazep.newsheadlinesapp.data.paging.NewsPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val apiService: NewsApiService
) {
    fun getNewsHeadlines(): Flow<PagingData<Article>> {
        //pagging 3
        return Pager(
            config = PagingConfig(
                pageSize = NewsPagingSource.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NewsPagingSource(apiService) }
        ).flow
    }

    //todo: using room to handle offline mode & caching
}