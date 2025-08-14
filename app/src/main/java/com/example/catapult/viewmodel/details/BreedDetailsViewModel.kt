// app/src/main/java/com/example/catapult/viewmodel/details/BreedDetailsViewModel.kt
package com.example.catapult.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.db.BreedWithImages
import com.example.catapult.db.toImageData
import com.example.catapult.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedDetailsViewModel @Inject constructor(
    private val repo: BreedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val breedId: String = checkNotNull(savedStateHandle["breedId"])

    private val _state = MutableStateFlow(BreedDetailsState(isLoading = true))
    val state: StateFlow<BreedDetailsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getBreedWithImages(breedId)
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { relation: BreedWithImages? ->
                    if (relation == null) {
                        _state.update {
                            it.copy(isLoading = false, error = "Breed not found")
                        }
                    } else {
                        val apiModel = relation.toApiModel()

                        val uiImages = relation.images
                            .map { it.toImageData() }
                            .take(5)

                        _state.update {
                            it.copy(
                                isLoading = false,
                                breed = apiModel,
                                images = uiImages,
                                error = null
                            )
                        }
                    }
                }
        }
    }
}
