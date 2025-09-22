package com.mangazep.newsheadlinesapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.mangazep.newsheadlinesapp.data.model.Article
import com.mangazep.newsheadlinesapp.data.model.Source
import com.mangazep.newsheadlinesapp.data.repository.NewsRepository
import com.mangazep.newsheadlinesapp.ui.headlines.HeadlinesViewModel
import com.mangazep.newsheadlinesapp.ui.headlines.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HeadlinesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var repository: NewsRepository

    @Mock
    lateinit var uiStateObserver: Observer<UiState>

    @Mock
    lateinit var navigateToDetailObserver: Observer<Article?>

    @Mock
    lateinit var snackbarMessageObserver: Observer<String?>

    private lateinit var viewModel: HeadlinesViewModel

    // Sample test data
    private val sampleArticle = Article(
        title = "Test Article",
        description = "Test Description with detailed content for testing",
        url = "https://test.com/article",
        urlToImage = "https://test.com/image.jpg",
        publishedAt = "2024-01-01T10:30:00Z",
        author = "John Doe",
        source = Source(id = "test-news", name = "Test News Network")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)

        // Mock repository to return empty paging data by default
        `when`(repository.getNewsHeadlines()).thenReturn(
            flowOf(PagingData.from(emptyList<Article>()))
        )

        viewModel = HeadlinesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loading`() {
        // Given
        viewModel.uiState.observeForever(uiStateObserver)

        // Then
        verify(uiStateObserver).onChanged(UiState.Loading)
    }

    @Test
    fun `on success`() {
        // Given
        viewModel.uiState.observeForever(uiStateObserver)

        // When
        viewModel.onLoadStateUpdate(isLoading = false)

        // Then
        verify(uiStateObserver).onChanged(UiState.Success)
    }

    @Test
    fun `on error`() {
        // Given
        viewModel.uiState.observeForever(uiStateObserver)
        val errorMessage = "Network error"

        // When
        viewModel.onLoadStateUpdate(isLoading = false, errorMessage = errorMessage)

        // Then
        verify(uiStateObserver).onChanged(UiState.Error(errorMessage))
    }

    @Test
    fun `show snackbar data empty`() {
        // Given
        viewModel.uiState.observeForever(uiStateObserver)
        viewModel.snackbarMessage.observeForever(snackbarMessageObserver)

        // When
        viewModel.onLoadStateUpdate(isLoading = false, isEmpty = true)

        // Then
        verify(uiStateObserver).onChanged(UiState.Empty)
        // message snackbar data news is empty
        verify(snackbarMessageObserver).onChanged("Data News is empty")
    }

    @Test
    fun `articleClicked navigation to detail`() {
        // Given
        viewModel.navigateToDetail.observeForever(navigateToDetailObserver)

        // When
        viewModel.onArticleClicked(sampleArticle)

        // Then
        verify(navigateToDetailObserver).onChanged(sampleArticle)
    }
}
