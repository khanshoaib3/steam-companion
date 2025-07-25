package com.github.khanshoaib3.steamcompanion.ui.navigation.components

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue.Expanded
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.github.khanshoaib3.steamcompanion.ui.utils.Route
import kotlinx.coroutines.launch

class SteamCompanionNavSuiteScope(
    val isWideScreen: Boolean,
    val isShowingNavRail: Boolean,
    val railState: WideNavigationRailState,
    val modifier: Modifier = Modifier
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavWrapper(
    currentTopLevelRoute: Route,
    navigateTo: (Route) -> Unit,
    content: @Composable (SteamCompanionNavSuiteScope.() -> Unit),
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass

    val showNavRail = when {
        adaptiveInfo.windowPosture.isTabletop -> false
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
            -> true

        else -> false
    }
    val isWideScreen = when {
        !showNavRail -> false
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)
                && !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_EXPANDED_LOWER_BOUND)
            -> true
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
                && !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)
            -> true

        else -> false
    }
    val railState = rememberWideNavigationRailState()

    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    BackHandler(enabled = railState.targetValue == Expanded) {
        coroutineScope.launch {
            railState.collapse()
        }
    }

    val density = LocalDensity.current
    var leftInsetInDp = 0.dp
    with(density) {
        leftInsetInDp =
            WindowInsets.systemBars.getLeft(density, LocalLayoutDirection.current).toDp()
    }
    Scaffold(
        topBar = {
            if (isWideScreen) {
                CommonTopAppBar(
                    showMenuButton = false,
                    onMenuButtonClick = {},
                    navigateBackCallback = {},
                    scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                    forRoute = currentTopLevelRoute,
                )
            }
        },
        bottomBar = {
            if (!showNavRail) {
                NavBar(
                    currentTopLevelRoute = currentTopLevelRoute,
                    navigateTo = navigateTo
                )
            }
        },
        modifier = Modifier.padding(start = if (showNavRail) leftInsetInDp + 96.dp else 0.dp)
    ) { innerPaddings ->
        SteamCompanionNavSuiteScope(
            isWideScreen,
            isShowingNavRail = showNavRail,
            railState,
            modifier = Modifier.padding(innerPaddings)
        ).content()
    }

    NavRail(
        currentTopLevelRoute = currentTopLevelRoute,
        railState = railState,
        navigateTo = {
            coroutineScope.launch {
                navigateTo(it)
                if (railState.targetValue == Expanded) {
                    railState.collapse()
                }
            }
        },
        onRailButtonClicked = {
            coroutineScope.launch {
                railState.toggle()
                if (railState.targetValue == Expanded) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                }
            }
        },
        showNavRail = showNavRail
    )
}
