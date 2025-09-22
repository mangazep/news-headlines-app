package com.mangazep.newsheadlinesapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mangazep.newsheadlinesapp.data.api.NewsApiService
import com.mangazep.newsheadlinesapp.data.model.Article
import retrofit2.HttpException
import java.io.IOException

class NewsPagingSource (private val apiService: NewsApiService) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: START_PAGE_INDEX

        return try {
            val response = apiService.getTopHeadlines(
                apiKey = "hardcode_apikey",
                page = page,
                pageSize = params.loadSize
            )

            val articles = response.articles
            val nextKey = if (articles.isEmpty()) {
                null
            } else {
                page + (params.loadSize / NETWORK_PAGE_SIZE)
            }

            LoadResult.Page(
                data = articles,
                prevKey = if (page == START_PAGE_INDEX) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        const val START_PAGE_INDEX = 1
        const val NETWORK_PAGE_SIZE = 20
    }
}