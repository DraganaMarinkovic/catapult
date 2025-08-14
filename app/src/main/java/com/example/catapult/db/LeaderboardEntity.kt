package com.example.catapult.db

import android.R
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nickname: String,
    val score: Float,
    val createdAt: Long,
)