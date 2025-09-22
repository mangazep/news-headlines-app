package com.mangazep.newsheadlinesapp.pagging

import androidx.paging.PagingSource
import com.mangazep.newsheadlinesapp.data.api.NewsApiService
import com.mangazep.newsheadlinesapp.data.model.Article
import com.mangazep.newsheadlinesapp.data.model.NewsResponse
import com.mangazep.newsheadlinesapp.data.model.Source
import com.mangazep.newsheadlinesapp.data.paging.NewsPagingSource
import com.mangazep.newsheadlinesapp.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class NewsPagingSourceTest {

    @Mock
    lateinit var apiService: NewsApiService

    private lateinit var pagingSource: NewsPagingSource

    private val sampleArticles = listOf(
        Article(
            title = "Test Article",
            description = "Test Description with detailed content for testing",
            url = "https://test.com/article",
            urlToImage = "https://test.com/image.jpg",
            publishedAt = "2024-01-01T10:30:00Z",
            author = "John Doe",
            source = Source(id = "test-news", name = "Test News Network")
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        pagingSource = NewsPagingSource(apiService)
    }

    @Test
    fun `test api call success`() = runTest {
        // Given
        val mockResponse = NewsResponse(
            status = "ok",
            totalResults = 2,
            articles = sampleArticles
        )

        `when`(apiService.getTopHeadlines(apiKey = Constants.API_KEY)).thenReturn(mockResponse)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(sampleArticles, pageResult.data)
        assertEquals(null, pageResult.prevKey)
        assertEquals(2, pageResult.nextKey)
    }

    @Test
    fun `test api call data is empty`() = runTest {
        // Given
        val mockResponse = NewsResponse(
            status = "ok",
            totalResults = 0,
            articles = emptyList()
        )

        `when`(apiService.getTopHeadlines(apiKey = Constants.API_KEY)).thenReturn(mockResponse)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(emptyList<Article>(), pageResult.data)
        assertEquals(null, pageResult.nextKey)
    }
}
