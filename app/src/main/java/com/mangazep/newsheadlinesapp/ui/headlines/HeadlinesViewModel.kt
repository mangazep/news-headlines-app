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

    // LiveData untuk refresh state
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    // LiveData untuk selected article (untuk navigation)
    private val _navigateToDetail = MutableLiveData<Article?>()
    val navigateToDetail: LiveData<Article?> = _navigateToDetail

    init {
        _uiState.value = HeadlinesUiState.Loading
    }

    fun onLoadStateUpdate(isLoading: Boolean, errorMessage: String? = null) {
        _uiState.value = when {
            isLoading -> HeadlinesUiState.Loading
            errorMessage != null -> HeadlinesUiState.Error(errorMessage)
            else -> HeadlinesUiState.Success
        }
    }

    fun onArticleClicked(article: Article) {
        _navigateToDetail.value = article
    }

    fun onNavigatedToDetail() {
        _navigateToDetail.value = null
    }

    fun onRefresh() {
        _isRefreshing.value = true
    }

    fun onRefreshComplete() {
        _isRefreshing.value = false
    }
}

sealed class HeadlinesUiState {
    object Loading : HeadlinesUiState()
    object Success : HeadlinesUiState()
    data class Error(val message: String) : HeadlinesUiState()
}