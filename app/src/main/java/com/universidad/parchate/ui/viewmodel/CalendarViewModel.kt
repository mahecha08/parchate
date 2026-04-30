package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalendarUiState(
    val isLoading: Boolean = true,
    val favoriteEvents: List<Evento> = emptyList(),
    val myEvents: List<Evento> = emptyList(),
    val errorMessage: String? = null
) {
    val allEvents: List<Evento>
        get() = (favoriteEvents + myEvents)
            .distinctBy { it.id }
            .sortedWith(
                compareBy<Evento> { it.fecha }
                    .thenBy { it.hora }
            )
}

class CalendarViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUserId = Firebase.auth.currentUser?.uid ?: ""

    init {
        loadCalendarEvents()
    }

    private fun loadCalendarEvents() {
        if (currentUserId.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Usuario no autenticado"
                )
            }
            return
        }

        loadMyEvents()
        loadFavoriteEvents()
    }

    private fun loadMyEvents() {
        viewModelScope.launch {
            repository.observeMyEvents(currentUserId).collect { result ->
                result
                    .onSuccess { events ->
                        _uiState.update {
                            it.copy(
                                myEvents = events,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Error cargando tus eventos"
                            )
                        }
                    }
            }
        }
    }

    private fun loadFavoriteEvents() {
        viewModelScope.launch {
            repository.observeFavoriteEventIds(currentUserId).collect { result ->
                result
                    .onSuccess { favoriteIds ->
                        val favoriteEvents = favoriteIds.mapNotNull { eventId ->
                            repository.getEventById(eventId).getOrNull()
                        }

                        _uiState.update {
                            it.copy(
                                favoriteEvents = favoriteEvents,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Error cargando favoritos"
                            )
                        }
                    }
            }
        }
    }
}
