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

    //flow data news paging3
    val headlinesPagingData: Flow<PagingData<Article>> = repository
        .getNewsHeadlines()
        .cachedIn(viewModelScope)

    // state loading, error success
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    // navigate to detail screen
    private val _navigateToDetail = MutableLiveData<Article?>()
    val navigateToDetail: LiveData<Article?> = _navigateToDetail

    // snack bar message
    private val _snackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = _snackbarMessage

    init {
        _uiState.value = UiState.Loading
    }

    fun onLoadStateUpdate(
        isLoading: Boolean,
        errorMessage: String? = null,
        isEmpty: Boolean = false
    ) {
        _uiState.value = when {
            isLoading -> UiState.Loading
            errorMessage != null -> UiState.Error(errorMessage)
            isEmpty -> {
                _snackbarMessage.value = "Data News is empty"
                UiState.Empty
            }

            else -> UiState.Success
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

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    object Empty : UiState()
    data class Error(val message: String) : UiState()
}