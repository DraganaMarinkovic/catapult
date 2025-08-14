package com.example.catapult.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.catapult.db.LeaderboardEntity
import com.example.catapult.navigation.Screen
import com.example.catapult.ui.list.BreedListBottomNav
import com.example.catapult.viewmodel.account.AccountDetailsEvent
import com.example.catapult.viewmodel.account.AccountDetailsSideEffect
import com.example.catapult.viewmodel.account.AccountDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountDetailsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onNavBarClick: (String) -> Unit,
    onLogout: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { eff ->
            when (eff) {
                AccountDetailsSideEffect.LoggedOut -> {
                    onLogout()
                }
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
                TopAppBar(title = { Text("Account Details") })
            }
        },
        bottomBar = { BreedListBottomNav(onNavBarClick, Screen.Account.route) }

    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        state.account?.let { acct ->
                            AccountInfoCard(account = acct)
                        }
                    }
                    item {
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text(
                            "Your Best Local Score:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = state.bestLocalScore?.let { "%.2f".format(it) } ?: "—",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text(
                            "Your Best Global Position:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = state.bestGlobalPosition?.let { "#$it" } ?: "Not on Leaderboard",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Button(
                            onClick = { viewModel.sendEvent(AccountDetailsEvent.LogOutEvent) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Logout")
                        }
                        Divider(Modifier.padding(vertical = 8.dp))

                    }
                    item {
                        Text(
                            "Your Quiz History:",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    if (state.localHistory.isEmpty()) {
                        item {
                            Text("No local quiz results yet.", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        items(state.localHistory.reversed()) { qr ->
                            QuizHistoryRow(qr)
                        }
                    }
                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountInfoCard(account: com.example.catapult.db.AccountEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nickname: ${account.nickname}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(4.dp))
            Text("Email: ${account.email}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(4.dp))
            Text("First Name: ${account.firstName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(4.dp))
            Text("Last Name: ${account.lastName}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun QuizHistoryRow(qr: LeaderboardEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val ts = java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date(qr.createdAt))

            Column {
                Text("Score: ${"%.2f".format(qr.score)}", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(2.dp))
                Text("When: $ts", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
