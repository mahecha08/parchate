package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

data class HomeFilterState(
    val search: String = "",
    val categoria: String = "Todos",
    val ciudad: String = "",
    val soloGratis: Boolean = false,
    val modalidad: String = "Todas"
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val events: List<Evento> = emptyList(),
    val favoriteEventIds: List<String> = emptyList(),
    val filteredEvents: List<Evento> = emptyList(),
    val errorMessage: String? = null,
    val filters: HomeFilterState = HomeFilterState()
)

class HomeViewModel(
    private val repository: EventRepository = EventRepository(),

) : ViewModel() {
    private val currentUserId = Firebase.auth.currentUser?.uid ?: ""
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeEvents()
        observeFavorites()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            repository.observeActiveEvents().collect { result ->
                result
                    .onSuccess { events ->
                        _uiState.update { current ->
                            val updated = current.copy(
                                isLoading = false,
                                events = events,
                                errorMessage = null
                            )
                            updated.copy(filteredEvents = applyFilters(events, updated.filters))
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Error cargando eventos"
                            )
                        }
                    }
            }
        }
    }

    private fun observeFavorites() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            repository.observeFavoriteEventIds(currentUserId).collect { result ->
                result
                    .onSuccess { ids ->
                        _uiState.update {
                            it.copy(favoriteEventIds = ids)
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(errorMessage = error.message ?: "Error cargando favoritos")
                        }
                    }
            }
        }
    }

    fun toggleFavorite(eventId: String) {
        if (currentUserId.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Usuario no autenticado")
            }
            return
        }

        viewModelScope.launch {
            val isFavorite = _uiState.value.favoriteEventIds.contains(eventId)

            val result = if (isFavorite) {
                repository.removeFavorite(eventId)
            } else {
                repository.addFavorite(eventId)
            }

            result.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo actualizar favoritos")
                }
            }
        }
    }


    fun updateFilters(transform: (HomeFilterState) -> HomeFilterState) {
        _uiState.update { current ->
            val newFilters = transform(current.filters)
            current.copy(
                filters = newFilters,
                filteredEvents = applyFilters(current.events, newFilters)
            )
        }
    }

    private fun applyFilters(events: List<Evento>, filters: HomeFilterState): List<Evento> {
        return events.filter { event ->
            val matchesSearch = event.matchesSearch(filters.search)
            val matchesCategory = filters.categoria == "Todos" || event.categoria.equals(filters.categoria, ignoreCase = true)
            val matchesCity = filters.ciudad.isBlank() || event.ciudad.contains(filters.ciudad.trim(), ignoreCase = true)
            val matchesFree = !filters.soloGratis || event.gratis
            val matchesModalidad = filters.modalidad == "Todas" || event.modalidad.equals(filters.modalidad, ignoreCase = true)

            matchesSearch && matchesCategory && matchesCity && matchesFree && matchesModalidad
        }
    }
}
