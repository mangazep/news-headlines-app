package com.mangazep.newsheadlinesapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mangazep.newsheadlinesapp.ui.headlines.HeadlinesScreen

@Composable
fun NewsNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "headlines"
    ) {
        composable("headlines") {
            HeadlinesScreen(navController = navController)
        }
    }
}