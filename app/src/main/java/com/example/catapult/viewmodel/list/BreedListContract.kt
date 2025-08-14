package com.example.catapult.viewmodel.list

import com.example.catapult.data.model.BreedApiModel

data class BreedListState(
    val isLoading: Boolean = false,
    val breeds: List<BreedApiModel> = emptyList(),
    val filteredBreeds: List<BreedApiModel> = emptyList(),
    val query: String = "",
    val error: String? = null
)

sealed class BreedListEvent {
    object LoadBreeds : BreedListEvent()
    data class Search(val query: String) : BreedListEvent()
    data class ItemClicked(val breedId: String) : BreedListEvent()
}

sealed class BreedListSideEffect {
    data class NavigateToDetails(val breedId: String) : BreedListSideEffect()
}