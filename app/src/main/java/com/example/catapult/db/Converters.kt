package com.example.catapult.db

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Converters {
    private val json = Json { encodeDefaults = true }

    @TypeConverter
    @JvmStatic
    fun fromStringList(list: List<String>): String =
        json.encodeToString(list)

    @TypeConverter
    @JvmStatic
    fun toStringList(data: String): List<String> =
        try {
            json.decodeFromString(data)
        } catch (_: Exception) {
            emptyList()
        }
}
