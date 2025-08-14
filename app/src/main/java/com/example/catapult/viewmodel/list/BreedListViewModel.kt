package com.example.catapult.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val repo: BreedRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BreedListState())
    val state: StateFlow<BreedListState> = _state.asStateFlow()

    private val eventChannel = Channel<BreedListEvent>(Channel.UNLIMITED)
    private val events: Flow<BreedListEvent> = eventChannel.receiveAsFlow()

    private val effectChannel = Channel<BreedListSideEffect>(Channel.UNLIMITED)
    val sideEffects: Flow<BreedListSideEffect> = effectChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is BreedListEvent.LoadBreeds -> loadBreeds()
                    is BreedListEvent.Search -> filterBreeds(event.query)
                    is BreedListEvent.ItemClicked -> handleItemClicked(event.breedId)
                }
            }
        }
        sendEvent(BreedListEvent.LoadBreeds)
    }

    fun sendEvent(event: BreedListEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun loadBreeds() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        repo.getBreeds()
            .map { list ->
                list.map { it.toApiModel() }
            }
            .onEach { uiList ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        breeds = uiList,
                        filteredBreeds = uiList
                    )
                }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.toString()) }
            }
            .collect()
    }

    private fun filterBreeds(query: String) {
        _state.update { s ->
            val filtered = if (query.isBlank()) {
                s.breeds
            } else {
                s.breeds.filter {
                    it.name.startsWith(query, ignoreCase = true)
                            || it.name.contains(query, ignoreCase = true)
                }
            }
            s.copy(query = query, filteredBreeds = filtered)
        }
    }

    private fun handleItemClicked(breedId: String) {
        viewModelScope.launch {
            effectChannel.send(BreedListSideEffect.NavigateToDetails(breedId))
        }
    }
}





