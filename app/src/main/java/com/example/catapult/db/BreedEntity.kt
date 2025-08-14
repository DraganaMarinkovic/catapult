// app/src/main/java/com/example/catapult/db/BreedEntity.kt
package com.example.catapult.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val id: String,
    val name: String,
    val temperament: String,
    val description: String,
    val origin: String?,
    val lifeSpan: String?,
    val weightMetric: String?,
    val weightImperial: String?,
    val wikipediaUrl: String?,
    val altNames: String?,
    val rare: Int,
    val intelligence: Int,
    val sheddingLevel: Int,
    val affectionLevel: Int,
    val energyLevel: Int,
    val dogFriendly: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
