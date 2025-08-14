package com.example.catapult.data.model

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class BreedApiModel(
    val id: String,
    val name: String,
    val temperament: String,
    val description: String,
    val origin: String? = null,
    @SerialName("life_span")
    val lifeSpan: String? = null,
    val weight: Weight? = null,
    val image: ImageData? = null,
    @SerialName("wikipedia_url")
    val wikipediaUrl: String? = null,
    @SerialName("alt_names")
    val altNames: String? = null,
    val rare: Int,
    val intelligence: Int,
    val shedding_level: Int,
    val affection_level: Int,
    val energy_level: Int,
    val dog_friendly: Int
    )

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Weight(
    val metric: String,
    val imperial: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ImageData(
    val id: String,
    val url: String,
    val width: Int? = null,
    val height: Int? = null
)