package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.EventRepository
import com.universidad.parchate.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val filteredEvents: List<Evento> = emptyList(),
    val errorMessage: String? = null,
    val filters: HomeFilterState = HomeFilterState(),
    val favoriteEventIds: Set<String> = emptySet()
)

class HomeViewModel(
    private val eventRepository: EventRepository = EventRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeEvents()
        observeFavorites()
    }

    private fun observeFavorites() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.data?.get("favoriteEventIds")
                    ?.let { it as? List<*> }
                    ?.filterIsInstance<String>()
                    ?.let { favoriteIds ->
                        _uiState.update { state ->
                            state.copy(favoriteEventIds = favoriteIds.toSet())
                        }
                    }
            }
    }

    fun toggleFavorite(eventId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val isFavorite = eventId in _uiState.value.favoriteEventIds
            if (isFavorite) {
                userRepository.removeFavorite(eventId)
                _uiState.update { state ->
                    state.copy(favoriteEventIds = state.favoriteEventIds - eventId)
                }
            } else {
                userRepository.addFavorite(eventId)
                _uiState.update { state ->
                    state.copy(favoriteEventIds = state.favoriteEventIds + eventId)
                }
            }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            eventRepository.observeActiveEvents().collect { result ->
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
