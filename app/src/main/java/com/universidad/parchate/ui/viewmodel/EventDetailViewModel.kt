package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val event: Evento? = null,
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val error: String? = null
)

class EventDetailViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    private val currentUserId = Firebase.auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getEventById(eventId)
                .onSuccess { evento ->
                    _uiState.update { it.copy(event = evento, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
        if (currentUserId.isNotBlank()) observeFavorites(eventId)
    }

    private fun observeFavorites(eventId: String) {
        viewModelScope.launch {
            repository.observeFavoriteEventIds(currentUserId).collect { result ->
                result.onSuccess { ids ->
                    _uiState.update { it.copy(isFavorite = eventId in ids) }
                }
            }
        }
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            if (_uiState.value.isFavorite) repository.removeFavorite(eventId)
            else repository.addFavorite(eventId)
        }
    }
}
