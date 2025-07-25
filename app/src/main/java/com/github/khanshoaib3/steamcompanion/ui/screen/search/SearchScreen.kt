package com.github.khanshoaib3.steamcompanion.ui.screen.search

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.khanshoaib3.steamcompanion.R
import com.github.khanshoaib3.steamcompanion.ui.navigation.components.CommonTopAppBar
import com.github.khanshoaib3.steamcompanion.ui.screen.search.components.SearchResultRow
import com.github.khanshoaib3.steamcompanion.ui.theme.SteamCompanionTheme
import com.github.khanshoaib3.steamcompanion.ui.utils.Route
import com.github.khanshoaib3.steamcompanion.ui.utils.Side
import com.github.khanshoaib3.steamcompanion.ui.utils.TopLevelRoute
import com.github.khanshoaib3.steamcompanion.ui.utils.plus
import com.github.khanshoaib3.steamcompanion.ui.utils.removePaddings
import com.github.khanshoaib3.steamcompanion.utils.TopLevelBackStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenRoot(
    topLevelBackStack: TopLevelBackStack<Route>,
    isWideScreen: Boolean,
    isShowingNavRail: Boolean,
    onMenuButtonClick: () -> Unit,
    addAppDetailPane: (Int) -> Unit,
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val searchDataState by searchViewModel.searchDataStateFlow.collectAsState()

    val localView = LocalView.current

    val density: Density = LocalDensity.current
    val imageWidth: Dp
    val imageHeight: Dp
    with(density) {
        imageWidth = 150.toDp()
        imageHeight = 225.toDp()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val onSearch: (String) -> Unit = {
        focusManager.clearFocus()
        scope.launch(Dispatchers.IO) {
            searchViewModel.runSearchQuery(it)
            localView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    val onSearchQueryChange: (String) -> Unit = {
        searchViewModel.updateSearchQuery(it)
    }

    if (isWideScreen) {
        SearchScreen(
            onSearch = onSearch,
            searchResults = searchDataState.searchResults,
            searchQuery = searchDataState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onGameClick = addAppDetailPane,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            modifier = modifier
        )
    } else {
        SearchScreenWithScaffold(
            onSearch = onSearch,
            searchResults = searchDataState.searchResults,
            searchQuery = searchDataState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onGameClick = addAppDetailPane,
            showMenuButton = !isShowingNavRail,
            onMenuButtonClick = onMenuButtonClick,
            navigateBackCallback = { topLevelBackStack.removeLast() },
            topAppBarScrollBehavior = scrollBehavior,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenWithScaffold(
    onSearch: (String) -> Unit,
    searchResults: List<AppSearchResultDisplay>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onGameClick: (Int) -> Unit,
    showMenuButton: Boolean,
    onMenuButtonClick: () -> Unit,
    navigateBackCallback: () -> Unit,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    imageWidth: Dp,
    imageHeight: Dp,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CommonTopAppBar(
                showMenuButton = showMenuButton,
                onMenuButtonClick = onMenuButtonClick,
                scrollBehavior = topAppBarScrollBehavior,
                navigateBackCallback = navigateBackCallback,
                forRoute = TopLevelRoute.Search,
                windowInsets = WindowInsets()
            )
        },
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) {
        SearchScreen(
            onSearch = onSearch,
            searchResults = searchResults,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onGameClick = onGameClick,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            modifier = modifier.padding(it.removePaddings(Side.End + Side.Start + Side.Bottom))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onSearch: (String) -> Unit,
    searchResults: List<AppSearchResultDisplay>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onGameClick: (Int) -> Unit,
    imageWidth: Dp,
    imageHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Card(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = { onSearchQueryChange(it) },
                onSearch = { onSearch(it) },
                placeholder = { Text("Search..") },
                expanded = false,
                onExpandedChange = {},
                leadingIcon = {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            if (searchQuery.isEmpty()) Icons.Default.Search else Icons.Default.Clear,
                            contentDescription = if (searchQuery.isEmpty()) "Enter search query" else "Clear input text"
                        )
                    }
                },
                trailingIcon = {
                    if (!searchQuery.isEmpty())
                        IconButton(onClick = { onSearch(searchQuery) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send search query"
                            )
                        }
                }
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(searchResults) {
                    SearchResultRow(
                        it,
                        onClick = onGameClick,
                        imageWidth = imageWidth,
                        imageHeight = imageHeight
                    )
                }
            }
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchScreenWithScaffoldPreview() {
    val density: Density = LocalDensity.current
    val imageWidth: Dp
    val imageHeight: Dp
    with(density) {
        imageWidth = 150.toDp()
        imageHeight = 225.toDp()
    }

    SteamCompanionTheme {
        SearchScreenWithScaffold(
            onSearch = {},
            searchResults = listOf(),
            searchQuery = "Ello",
            onSearchQueryChange = {},
            onGameClick = {},
            showMenuButton = true,
            onMenuButtonClick = {},
            navigateBackCallback = {},
            topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            imageWidth = imageWidth,
            imageHeight = imageHeight
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchScreenPreview() {
    val density: Density = LocalDensity.current
    val imageWidth: Dp
    val imageHeight: Dp
    with(density) {
        imageWidth = 150.toDp()
        imageHeight = 225.toDp()
    }

    SteamCompanionTheme {
        SearchScreen(
            onSearch = {},
            searchResults = listOf(
                AppSearchResultDisplay(
                    appId = 12342,
                    name = "Max Payne 2: The Fall of Max Payne",
                    iconUrl = "bru"
                ),
                AppSearchResultDisplay(
                    appId = 12342,
                    name = "Max Payne 2: The Fall of Max Payne",
                    iconUrl = "bru"
                ),
            ),
            searchQuery = "Ello",
            onSearchQueryChange = {},
            onGameClick = {},
            imageWidth = imageWidth,
            imageHeight = imageHeight
        )
    }
}