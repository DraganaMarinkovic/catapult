package com.example.catapult.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.repository.LeaderboardEntry
import com.example.catapult.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repo: LeaderboardRepository
) : ViewModel() {

    private val _entries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val entries: StateFlow<List<LeaderboardEntry>> = _entries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            loadLeaderboard()
        }
    }

    private suspend fun loadLeaderboard() {
        _isLoading.value = true
        runCatching {
            repo.fetchLeaderboard()
        }.onSuccess { list ->
            _entries.value = list
            _error.value = null
        }.onFailure { thr ->
            _error.value = thr.message
        }
        _isLoading.value = false
    }
}
