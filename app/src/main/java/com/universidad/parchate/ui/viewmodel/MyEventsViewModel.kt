package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyEventsUiState(
    val isLoading: Boolean = true,
    val events: List<Evento> = emptyList(),
    val errorMessage: String? = null
)

class MyEventsViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUserId = Firebase.auth.currentUser?.uid ?: ""

    init {
        loadMyEvents()
    }

    private fun loadMyEvents() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            repository.observeMyEvents(currentUserId).collect { result ->
                result.onSuccess { list ->
                    _uiState.update { it.copy(events = list, isLoading = false) }
                }.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
                }
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
        }
    }
}