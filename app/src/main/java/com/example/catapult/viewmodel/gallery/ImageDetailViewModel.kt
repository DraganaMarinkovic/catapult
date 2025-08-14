package com.example.catapult.viewmodel.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.data.model.ImageData
import com.example.catapult.db.toImageData
import com.example.catapult.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val repo: BreedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val imageId: String = checkNotNull(savedStateHandle["imageId"])

    val image: StateFlow<ImageData?> =
        repo.getImageById(imageId)
            .map { it?.toImageData() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
}
