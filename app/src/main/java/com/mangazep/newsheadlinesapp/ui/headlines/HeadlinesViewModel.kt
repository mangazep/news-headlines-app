package com.mangazep.newsheadlinesapp.ui.headlines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mangazep.newsheadlinesapp.data.model.Article
import com.mangazep.newsheadlinesapp.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HeadlinesViewModel @Inject constructor(repository: NewsRepository) : ViewModel() {

    //flow data news paging
    val headlinesPagingData: Flow<PagingData<Article>> = repository
        .getNewsHeadlines()
        .cachedIn(viewModelScope)

    // LiveData untuk UI States
    private val _uiState = MutableLiveData<HeadlinesUiState>()
    val uiState: LiveData<HeadlinesUiState> = _uiState

    // LiveData untuk selected article (untuk navigation)
    private val _navigateToDetail = MutableLiveData<Article?>()
    val navigateToDetail: LiveData<Article?> = _navigateToDetail

    // snack bar message
    private val _snackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = _snackbarMessage

    init {
        _uiState.value = HeadlinesUiState.Loading
    }

    fun onLoadStateUpdate(
        isLoading: Boolean,
        errorMessage: String? = null,
        isEmpty: Boolean = false
    ) {
        _uiState.value = when {
            isLoading -> HeadlinesUiState.Loading
            errorMessage != null -> HeadlinesUiState.Error(errorMessage)
            isEmpty -> {
                _snackbarMessage.value = "Data News is empty"
                HeadlinesUiState.Empty
            }

            else -> HeadlinesUiState.Success
        }
    }

    fun onArticleClicked(article: Article) {
        _navigateToDetail.value = article
    }

    fun onNavigatedToDetail() {
        _navigateToDetail.value = null
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

sealed class HeadlinesUiState {
    object Loading : HeadlinesUiState()
    object Success : HeadlinesUiState()
    object Empty : HeadlinesUiState()
    data class Error(val message: String) : HeadlinesUiState()
}