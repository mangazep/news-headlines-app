package com.mangazep.newsheadlinesapp.ui.headlines

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mangazep.newsheadlinesapp.data.model.Article
import com.mangazep.newsheadlinesapp.ui.components.ArticleItem
import com.mangazep.newsheadlinesapp.ui.components.ErrorMessage
import com.mangazep.newsheadlinesapp.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeadlinesScreen(
    navController: NavController,
    viewModel: HeadlinesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(initial = UiState.Loading)
    val navigateToDetail by viewModel.navigateToDetail.observeAsState()
    val pagingItems = viewModel.headlinesPagingData.collectAsLazyPagingItems()
    val snackbarMessage by viewModel.snackbarMessage.observeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    //on click to detail
    LaunchedEffect(navigateToDetail) {
        navigateToDetail?.let { article ->
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("article", article)
            navController.navigate("detail")
            viewModel.onNavigatedToDetail()
        }
    }

    LaunchedEffect(pagingItems.loadState) {
        when (val refresh = pagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                viewModel.onLoadStateUpdate(isLoading = true)
            }

            is LoadState.Error -> {
                viewModel.onLoadStateUpdate(
                    isLoading = false,
                    errorMessage = refresh.error.localizedMessage
                )
            }

            is LoadState.NotLoading -> {
                val isEmpty = pagingItems.itemCount == 0
                viewModel.onLoadStateUpdate(
                    isLoading = false,
                    isEmpty = isEmpty
                )
            }
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Headlines") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LoadData(
            uiState = uiState,
            pagingItems = pagingItems,
            onArticleClick = { article -> viewModel.onArticleClicked(article) },
            onRetryClick = { pagingItems.retry() },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun LoadData(
    uiState: UiState,
    pagingItems: LazyPagingItems<Article>,
    onArticleClick: (Article) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("TEST", "load data")
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is UiState.Loading -> {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Empty -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "News Not Available",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Try refreshing ",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetryClick) {
                        Text("Refresh")
                    }
                }
            }

            is UiState.Error -> {
                ErrorMessage(
                    message = uiState.message,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pagingItems.itemCount) { index ->
                        val article = pagingItems[index]
                        article?.let {
                            ArticleItem(article = it, onClick = { onArticleClick(it) })
                        }
                    }

                    pagingItems.apply {
                        when {
                            loadState.append is LoadState.Loading -> {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    )
                                }
                            }

                            loadState.append is LoadState.Error -> {
                                item {
                                    ErrorMessage(
                                        message = (loadState.append as LoadState.Error).error.localizedMessage
                                            ?: "Error loading",
                                        onRetryClick = { retry() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}