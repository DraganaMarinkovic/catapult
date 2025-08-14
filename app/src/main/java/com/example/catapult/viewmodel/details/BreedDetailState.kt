package com.example.catapult.viewmodel.details

import com.example.catapult.data.model.BreedApiModel
import com.example.catapult.data.model.ImageData

data class BreedDetailsState(
    val isLoading: Boolean = false,
    val breed: BreedApiModel? = null,
    val images: List<ImageData> = emptyList(),
    val error: String? = null
)
