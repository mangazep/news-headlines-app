package com.mangazep.newsheadlinesapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mangazep.newsheadlinesapp.data.api.NewsApiService
import com.mangazep.newsheadlinesapp.data.model.Article
import com.mangazep.newsheadlinesapp.data.model.NewsResponse
import com.mangazep.newsheadlinesapp.data.repository.NewsRepository
import com.mangazep.newsheadlinesapp.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class NewsRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: NewsApiService

    private lateinit var repository: NewsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = NewsRepository(apiService)
    }

    @Test
    fun `get data using flow paging data`() = runTest {
        // Given
        val mockResponse = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                Article(
                    title = "Test Article",
                    description = "Test Description",
                    url = "https://test.com",
                    urlToImage = null,
                    publishedAt = "2024-01-01",
                    author = "Test Author",
                    source = null
                )
            )
        )

        `when`(
            apiService.getTopHeadlines(
                country = Constants.COUNTRY_ID,
                apiKey = Constants.API_KEY,
                page = 1,
                pageSize = 20
            )
        ).thenReturn(mockResponse)

        // When
        val result = repository.getNewsHeadlines()

        // Then
        assert(result != null)
    }
}