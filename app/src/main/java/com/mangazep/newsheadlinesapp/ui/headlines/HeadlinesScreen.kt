package com.mangazep.newsheadlinesapp.ui.headlines

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.mangazep.newsheadlinesapp.data.model.Article
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mangazep.newsheadlinesapp.ui.components.ArticleItem
import com.mangazep.newsheadlinesapp.ui.components.ErrorMessage
import com.mangazep.newsheadlinesapp.ui.components.LoadingIndicator
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeadlinesScreen(
    navController: NavController,
    viewModel: HeadlinesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(initial = HeadlinesUiState.Loading)
    val isRefreshing by viewModel.isRefreshing.observeAsState(initial = false)
    val navigateToDetail by viewModel.navigateToDetail.observeAsState()

    val pagingItems = viewModel.headlinesPagingData.collectAsLazyPagingItems()
    
    LaunchedEffect(navigateToDetail) {
        navigateToDetail?.let { article ->
            val encodedTitle = URLEncoder.encode(
                article.title ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val encodedDescription = URLEncoder.encode(
                article.description ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val encodedImageUrl = URLEncoder.encode(
                article.urlToImage ?: "",
                StandardCharsets.UTF_8.toString()
            )
            navController.navigate("detail/$encodedTitle/$encodedDescription/$encodedImageUrl")
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
                viewModel.onLoadStateUpdate(isLoading = false)
                viewModel.onRefreshComplete()
            }
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
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                viewModel.onRefresh()
                pagingItems.refresh()
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            HeadlinesContent(
                uiState = uiState,
                pagingItems = pagingItems,
                onArticleClick = { article ->
                    viewModel.onArticleClicked(article)
                },
                onRetryClick = {
                    pagingItems.retry()
                }
            )
        }
    }
}

@Composable
fun HeadlinesContent(
    uiState: HeadlinesUiState,
    pagingItems: LazyPagingItems<Article>,
    onArticleClick: (Article) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is HeadlinesUiState.Loading -> {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HeadlinesUiState.Error -> {
                ErrorMessage(
                    message = uiState.message,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HeadlinesUiState.Success -> {
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
                                            ?: "Error loading more items",
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