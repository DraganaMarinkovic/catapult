package com.example.catapult.data.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LeaderboardApiModel(
    val category: Int,
    val nickname: String,
    val result: Float,
    val createdAt: Long
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PostResultRequest(
    val nickname: String,
    val result: Float,
    val category: Int = 1
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PostResultResponse(
    val result: LeaderboardApiModel,
    val ranking: Int
)
