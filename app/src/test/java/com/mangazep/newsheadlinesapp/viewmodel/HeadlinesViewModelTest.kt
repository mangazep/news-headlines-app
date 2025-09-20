package com.mangazep.newsheadlinesapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mangazep.newsheadlinesapp.data.repository.NewsRepository
import com.mangazep.newsheadlinesapp.ui.headlines.HeadlinesUiState
import com.mangazep.newsheadlinesapp.ui.headlines.HeadlinesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HeadlinesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: NewsRepository

    @Mock
    lateinit var uiStateObserver: Observer<HeadlinesUiState>

    private lateinit var viewModel: HeadlinesViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = HeadlinesViewModel(repository)
    }

    @Test
    fun `initial state should be loading`() {
        viewModel.uiState.observeForever(uiStateObserver)

        verify(uiStateObserver).onChanged(HeadlinesUiState.Loading)
    }

    @Test
    fun `onLoadStateUpdate with error should update state to error`() {
        viewModel.uiState.observeForever(uiStateObserver)
        val errorMessage = "Network error"

        viewModel.onLoadStateUpdate(isLoading = false, errorMessage = errorMessage)

        verify(uiStateObserver).onChanged(HeadlinesUiState.Error(errorMessage))
    }

}