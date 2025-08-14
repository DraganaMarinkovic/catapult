package com.example.catapult.viewmodel.account

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.db.AccountEntity
import com.example.catapult.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.*
import com.example.catapult.viewmodel.list.BreedListEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repo: AccountRepository
) : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var nickname by mutableStateOf("")
    var email by mutableStateOf("")

    var errorMsg by mutableStateOf<String?>(null)

    private val eventChannel = Channel<SignUpEvent>(Channel.UNLIMITED)
    private val events: Flow<SignUpEvent> = eventChannel.receiveAsFlow()

    private val USERNAME_REGEX = "^[a-z0-9_]+$".toRegex()

    init {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is SignUpEvent.Submit -> submit(event.onSuccess)
                }
            }
        }
    }

    fun sendEvent(event: SignUpEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotBlank()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isUsernameValid(nick: String): Boolean {
        return nick.isNotBlank() && USERNAME_REGEX.matches(nick)
    }

    fun submit(onSuccess: () -> Unit) {
        if (nickname.isBlank() || email.isBlank()) {
            errorMsg = "Nickname and email are required!"
            return
        }
        if (!isUsernameValid(nickname)) {
            errorMsg = "Nickname may only contain lowercase letters, digits, and _"
            return
        }
        if (!isEmailValid(email)) {
            errorMsg = "Please enter a valid email address"
            return
        }
        viewModelScope.launch {
            repo.createAccount(
                AccountEntity(
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    nickname = nickname
                )
            )
            onSuccess()
        }
    }
}
