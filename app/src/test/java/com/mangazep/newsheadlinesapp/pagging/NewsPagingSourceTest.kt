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
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

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
        ),
        Article(
            title = "Test Article 1",
            description = "Test Description with detailed content for testing 1",
            url = "https://test.com/article1",
            urlToImage = "https://test.com/image1.jpg",
            publishedAt = "2024-01-01T10:30:00Z",
            author = "John Doe1",
            source = Source(id = "test-news1", name = "Test News Network1")
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        pagingSource = NewsPagingSource(apiService)
    }

    @Test
    fun `load returns success result when API call succeeds`() = runTest {
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
    fun `load returns success result with empty list when no articles`() = runTest {
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

    @Test
    fun `load returns error result when IOException occurs`() = runTest {
        // Given
        doAnswer {
            throw IOException("Network error")
        }.`when`(apiService).getTopHeadlines(
            apiKey = Constants.API_KEY,
            page = 1,
            pageSize = 20
        )

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val errorResult = result as PagingSource.LoadResult.Error
        assertTrue(errorResult.throwable is IOException)
        assertEquals("Network error", errorResult.throwable.message)
    }

    @Test
    fun `load returns error result when HttpException occurs`() = runTest {
        // Given
        val exception = HttpException(Response.error<Any>(404, mock()))
        `when`(apiService.getTopHeadlines(apiKey = Constants.API_KEY)).thenThrow(exception)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val errorResult = result as PagingSource.LoadResult.Error
        assertEquals(exception, errorResult.throwable)
    }
}
