package com.example.catapult.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.db.AccountEntity
import com.example.catapult.data.model.LeaderboardApiModel
import com.example.catapult.repository.AccountDetailsRepository
import com.example.catapult.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val repo: AccountDetailsRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountDetailsUiState())
    val uiState: StateFlow<AccountDetailsUiState> = _uiState.asStateFlow()

    private val _effects = Channel<AccountDetailsSideEffect>(Channel.UNLIMITED)
    val effects = _effects.receiveAsFlow()

    private val eventChannel = Channel<AccountDetailsEvent>(Channel.UNLIMITED)
    private val events: Flow<AccountDetailsEvent> = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is AccountDetailsEvent.LogOutEvent -> logout()
                }
            }
        }

        viewModelScope.launch {
            repo.getAccount()
                .filterNotNull()
                .take(1)
                .collect { acct ->
                    loadAllForNickname(acct)
                }
        }
    }

    private suspend fun loadAllForNickname(account: AccountEntity) = withContext(Dispatchers.IO) {
        _uiState.update { it.copy(isLoading = true, account = account, errorMessage = null) }
        val nick = account.nickname

        viewModelScope.launch {
            repo.getAllLocalQuizResults(nick)
                .collect { historyList ->
                    _uiState.update {
                        it.copy(localHistory = historyList)
                    }
                }
        }

        val bestLocal = try {
            repo.getBestLocalScore(nick)
        } catch (t: Throwable) {
            null
        }
        _uiState.update {
            it.copy(bestLocalScore = bestLocal)
        }

        val globalList: List<LeaderboardApiModel> = try {
            repo.getGlobalLeaderboard()
        } catch (t: Throwable) {
            emptyList()
        }

        val position: Int? = globalList.indexOfFirst { dto ->
            dto.nickname.equals(nick, ignoreCase = true)
        }.let { idx ->
            if (idx >= 0) idx + 1 else null
        }

        _uiState.update {
            it.copy(
                bestGlobalPosition = position,
                isLoading = false
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            accountRepository.logout()
            _effects.send(AccountDetailsSideEffect.LoggedOut)
        }
    }

    fun sendEvent(event: AccountDetailsEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}
