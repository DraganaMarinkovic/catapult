package com.example.catapult.viewmodel.account

sealed class SignUpEvent {
    data class Submit(val onSuccess: () -> Unit) : SignUpEvent()
}