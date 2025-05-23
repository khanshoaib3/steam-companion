package com.github.khanshoaib3.steamcompanion.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.khanshoaib3.steamcompanion.ui.navigation.SteamCompanionNavHost
import com.github.khanshoaib3.steamcompanion.ui.navigation.SteamCompanionNavigationActions
import com.github.khanshoaib3.steamcompanion.ui.navigation.SteamCompanionNavigationWrapper
import com.github.khanshoaib3.steamcompanion.ui.theme.SteamCompanionTheme
import kotlinx.coroutines.launch

@Composable
fun SteamCompanionApp() {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        SteamCompanionNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val scope = rememberCoroutineScope()

    Surface {
        SteamCompanionNavigationWrapper(
            currentDestination = currentDestination,
            navigateToTopLevelDestination = { navigationActions.navigateToTopLevelRoute(it) },
        ) {
            SteamCompanionNavHost(
                navController = navController,
                navSuiteType = navSuiteType,
                currentDestination = currentDestination,
                onMenuButtonClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun SteamCompanionAppPreview() {
    SteamCompanionTheme {
        SteamCompanionApp()
    }
}