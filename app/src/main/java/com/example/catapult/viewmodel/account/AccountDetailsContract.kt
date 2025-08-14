package com.example.catapult.viewmodel.account

import com.example.catapult.db.AccountEntity
import com.example.catapult.db.LeaderboardEntity

data class AccountDetailsUiState(
    val account: AccountEntity? = null,
    val localHistory: List<LeaderboardEntity> = emptyList(),
    val bestLocalScore: Float? = null,
    val bestGlobalPosition: Int? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed class AccountDetailsSideEffect {
    object LoggedOut : AccountDetailsSideEffect()
}

sealed class AccountDetailsEvent {
    object LogOutEvent : AccountDetailsEvent()
}