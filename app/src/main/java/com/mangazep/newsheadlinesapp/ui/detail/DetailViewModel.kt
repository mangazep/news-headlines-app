package com.mangazep.newsheadlinesapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mangazep.newsheadlinesapp.data.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {
    private val _article = MutableLiveData<Article?>()
    val article: LiveData<Article?> = _article

    private val _actionMessage = MutableLiveData<String?>()
    val actionMessage: LiveData<String?> = _actionMessage

    fun setArticleData(article: Article?) {
        _article.value = article
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}