package com.mangazep.newsheadlinesapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mangazep.newsheadlinesapp.data.model.Article
import com.mangazep.newsheadlinesapp.data.model.Source
import com.mangazep.newsheadlinesapp.ui.detail.DetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var articleObserver: Observer<Article?>

    @Mock
    lateinit var actionMessageObserver: Observer<String?>

    private lateinit var viewModel: DetailViewModel

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
        MockitoAnnotations.openMocks(this)
        viewModel = DetailViewModel()
    }

    @Test
    fun `setArticleData should update article LiveData`() {
        // Given
        viewModel.article.observeForever(articleObserver)

        // When
        viewModel.setArticleData(sampleArticle)

        // Then
        verify(articleObserver).onChanged(sampleArticle)
    }

    @Test
    fun `setArticleData with null should update article LiveData to null`() {
        // Given
        viewModel.article.observeForever(articleObserver)

        // When
        viewModel.setArticleData(null)

        // Then
        verify(articleObserver).onChanged(null)
    }

    @Test
    fun `clearActionMessage should clear action message`() {
        // Given
        viewModel.actionMessage.observeForever(actionMessageObserver)

        // When
        viewModel.clearActionMessage()

        // Then
        verify(actionMessageObserver).onChanged(null)
    }

    @Test
    fun `initial state should have null values for LiveData`() {
        // Given
        assertNull(viewModel.article.value)
        assertNull(viewModel.actionMessage.value)
    }
}
