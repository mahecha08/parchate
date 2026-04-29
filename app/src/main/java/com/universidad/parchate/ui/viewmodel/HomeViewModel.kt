package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.EventRepository
import com.universidad.parchate.data.repository.UserRepository
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
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeEvents()
        observeFavorites()
    }

    private fun observeFavorites() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, _ ->
                val favoriteIds = snapshot?.data?.get("favoriteEventIds")
                    ?.let { it as? List<*> }
                    ?.filterIsInstance<String>()
                    .orEmpty()

                _uiState.update { state ->
                    state.copy(favoriteEventIds = favoriteIds.toSet())
                }
            }
    }

    fun toggleFavorite(eventId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        if (userId.isBlank()) return

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
                            current.copy(
                                isLoading = false,
                                events = events,
                                filteredEvents = applyFilters(events, current.filters),
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update { current ->
                            current.copy(
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

    fun clearFilters() {
        _uiState.update { current ->
            val emptyFilters = HomeFilterState()
            current.copy(
                filters = emptyFilters,
                filteredEvents = applyFilters(current.events, emptyFilters)
            )
        }
    }

    private fun applyFilters(events: List<Evento>, filters: HomeFilterState): List<Evento> {
        val filtered = events.filter { event ->
            val matchesSearch = matchesSearch(event, filters.search)
            val matchesCategory = filters.categoria.equals("Todos", ignoreCase = true) ||
                event.categoria.equals(filters.categoria.trim(), ignoreCase = true)
            val matchesCity = filters.ciudad.isBlank() ||
                event.ciudad.contains(filters.ciudad.trim(), ignoreCase = true) ||
                event.direccion.contains(filters.ciudad.trim(), ignoreCase = true) ||
                event.pais.contains(filters.ciudad.trim(), ignoreCase = true)
            val matchesPrice = !filters.soloGratis || event.gratis || (event.precio ?: 0.0) == 0.0
            val normalizedModality = event.modalidad.ifBlank { "presencial" }
            val matchesModality = filters.modalidad.equals("Todas", ignoreCase = true) ||
                normalizedModality.equals(filters.modalidad.trim(), ignoreCase = true)

            matchesSearch && matchesCategory && matchesCity && matchesPrice && matchesModality
        }

        return if (filters.search.isBlank()) {
            filtered
        } else {
            filtered.sortedWith(
                compareByDescending<Evento> { searchScore(it, filters.search) }
                    .thenBy { it.fecha }
                    .thenBy { it.hora }
            )
        }
    }

    private fun matchesSearch(event: Evento, search: String): Boolean {
        if (search.isBlank()) return true

        val haystack = buildSearchableText(event)
        val queryTokens = search
            .trim()
            .lowercase()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        return queryTokens.all { token -> haystack.contains(token) }
    }

    private fun searchScore(event: Evento, search: String): Int {
        if (search.isBlank()) return 0

        val query = search.trim().lowercase()
        val tokens = query.split(Regex("\\s+")).filter { it.isNotBlank() }
        var score = 0

        if (event.nombreVisible.lowercase().contains(query)) score += 12
        if (event.categoria.lowercase().contains(query)) score += 8
        if (event.ubicacion.lowercase().contains(query) || event.direccion.lowercase().contains(query)) score += 7
        if (event.ciudad.lowercase().contains(query) || event.pais.lowercase().contains(query)) score += 5
        if (event.descripcion.lowercase().contains(query)) score += 4
        if (event.organizadorNombre.lowercase().contains(query)) score += 4
        if (event.etiquetas.any { it.lowercase().contains(query) }) score += 6

        val haystack = buildSearchableText(event)
        score += tokens.count { token -> haystack.contains(token) }

        return score
    }

    private fun buildSearchableText(event: Evento): String {
        val priceTokens = if (event.gratis || (event.precio ?: 0.0) == 0.0) {
            "gratis free sin costo"
        } else {
            "pago ${event.precio ?: ""}"
        }

        return listOf(
            event.nombreVisible,
            event.descripcion,
            event.categoria,
            event.ubicacion,
            event.ciudad,
            event.pais,
            event.direccion,
            event.modalidad.ifBlank { "presencial" },
            event.organizadorNombre,
            event.contactoOrganizador,
            event.etiquetas.joinToString(" "),
            priceTokens
        ).joinToString(" ").lowercase()
    }
}
