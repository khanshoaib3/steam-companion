package com.github.khanshoaib3.steamcompanion.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data class AppDetail(val appId: Int?) : Route {
        override val name = "AppDetail"
        override val icon = null
        override val selectedIcon = null
    }

    @Serializable
    data object Bookmark : Route {
        override val name = "Bookmarks"
        override val icon = Icons.Outlined.BookmarkBorder
        override val selectedIcon = Icons.Filled.Bookmark
    }

    @Serializable
    data object Tracked : Route {
        override val name = "Tracked"
        override val icon = Icons.Outlined.AccessAlarm
        override val selectedIcon = Icons.Filled.AccessAlarm
    }

    @Serializable
    data object Settings : Route {
        override val name = "Settings"
        override val icon = Icons.Outlined.Settings
        override val selectedIcon = Icons.Filled.Settings
    }

    @Serializable
    data object AboutNFeedback : Route {
        override val name = "About and Feedback"
        override val icon = Icons.Outlined.Feedback
        override val selectedIcon = Icons.Filled.Feedback
    }

    val name: String
    val icon: ImageVector?
    val selectedIcon: ImageVector?
}

@Serializable
sealed interface TopLevelRoute : Route {
    @Serializable
    data object Home : TopLevelRoute {
        override val name = "Home"
        override val icon = Icons.Outlined.Home
        override val selectedIcon = Icons.Filled.Home
    }

    @Serializable
    data object Search : TopLevelRoute {
        override val name = "Search"
        override val icon = Icons.Outlined.Search
        override val selectedIcon = Icons.Filled.Search
    }

    @Serializable
    data object Dummy : TopLevelRoute {
        override val name = "TopLevel"
        override val icon = null
        override val selectedIcon = null
    }
}

val NAV_TOP_LEVEL_ROUTES = listOf(TopLevelRoute.Home, TopLevelRoute.Search)
val NAV_OTHER_ROUTES = listOf(Route.Bookmark, Route.Tracked, Route.Settings, Route.AboutNFeedback)
