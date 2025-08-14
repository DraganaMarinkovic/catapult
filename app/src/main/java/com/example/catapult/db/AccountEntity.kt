package com.example.catapult.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val email: String,
    val firstName: String,
    val lastName: String,
    val nickname: String,
    val createdAt: Long = System.currentTimeMillis()
)
