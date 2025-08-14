package com.example.catapult.ui.list

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.catapult.ui.components.SearchBar
import com.example.catapult.viewmodel.list.BreedListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.catapult.navigation.Screen
import com.example.catapult.ui.components.CatapultLogo
import com.example.catapult.ui.components.LoadingScreen
import com.example.catapult.ui.components.ErrorScreen
import com.example.catapult.viewmodel.list.BreedListEvent
import com.example.catapult.viewmodel.list.BreedListSideEffect
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedListScreen(
    viewModel: BreedListViewModel = hiltViewModel(),
    onBreedClick: (String) -> Unit,
    onNavBarClick: (String) -> Unit,
    ) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { effect ->
            when (effect) {
                is BreedListSideEffect.NavigateToDetails -> {
                    onBreedClick(effect.breedId)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                CatapultLogo()
            }
        },
        bottomBar = { BreedListBottomNav(onNavBarClick, Screen.List.route) },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(8.dp))
            SearchBar(
                query = state.query,
                onQueryChange = { viewModel.sendEvent(BreedListEvent.Search(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            when {
                state.isLoading -> LoadingScreen(Modifier.fillMaxSize())
                state.error != null -> ErrorScreen(state.error!!, Modifier.fillMaxSize())
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 4.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.filteredBreeds) { breed ->
                            BreedListItem(breed, onClick = {
                                viewModel.sendEvent(
                                    BreedListEvent.ItemClicked(
                                        breed.id
                                    )
                                )
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BreedListBottomNav(onNavBarClick: (String) -> Unit, currentRoute: String) {
    val items = listOf(
        Screen.List to Icons.Default.Home,
        Screen.Quiz to Icons.Default.Help,
        Screen.Leaderboard to Icons.Default.Leaderboard,
        Screen.Account to Icons.Default.Person
    )

    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            items.forEach { (screen, icon) ->
                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = screen.route) },
                    label = {
                        when (screen) {
                            Screen.List -> Text("Home")
                            Screen.Quiz -> Text("Quiz")
                            Screen.Leaderboard -> Text("Leaderboard")
                            Screen.Account -> Text("Account")
                            else -> Text("")
                        }
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        onNavBarClick(screen.route)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.onSurface,
                        selectedIconColor = MaterialTheme.colorScheme.surface,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
