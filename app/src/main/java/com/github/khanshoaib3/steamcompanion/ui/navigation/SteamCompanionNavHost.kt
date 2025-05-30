package com.github.khanshoaib3.steamcompanion.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.github.khanshoaib3.steamcompanion.R
import com.github.khanshoaib3.steamcompanion.ui.screen.bookmark.BookmarkScreenRoot
import com.github.khanshoaib3.steamcompanion.ui.screen.detail.AppDetailsScreen
import com.github.khanshoaib3.steamcompanion.ui.screen.home.HomeScreenRoot
import com.github.khanshoaib3.steamcompanion.ui.screen.search.SearchScreenRoot

@Composable
fun SteamCompanionNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navSuiteType: NavigationSuiteType,
    currentDestination: NavDestination?,
    onMenuButtonClick: () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Home,
    ) {
        composable<Route.Home> {
            HomeScreenRoot(
                onMenuButtonClick = onMenuButtonClick,
                navSuiteType = navSuiteType,
                currentDestination = currentDestination
            )
        }
        composable<Route.Search> {
            SearchScreenRoot(
                currentDestination = currentDestination,
                navSuiteType = navSuiteType,
                onMenuButtonClick = onMenuButtonClick
            )
        }
        composable<Route.AppDetail> {
            AppDetailsScreen(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                appId = it.toRoute<Route.AppDetail>().appId,
                showTopBar = true,
                onUpButtonClick = { /* TODO (proposal) Navigate to home screen */ }
            )
        }
        composable<Route.Bookmark> {
            BookmarkScreenRoot(
                onMenuButtonClick = onMenuButtonClick,
                currentDestination = currentDestination,
                navSuiteType = navSuiteType
            )
        }
    }
}
