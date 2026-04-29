package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.model.toEventoOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class EventsMapUiState(
    val isLoading: Boolean = true,
    val events: List<Evento> = emptyList(),
    val errorMessage: String? = null
)

class EventsMapViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(EventsMapUiState())
    val uiState: StateFlow<EventsMapUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val snapshot = firestore
                    .collection("eventos")
                    .get()
                    .await()

                val events = snapshot.documents.mapNotNull { document ->
                    document.toEventoOrNull()
                }.filter {
                    (it.latitud ?: 0.0) != 0.0 && (it.longitud ?: 0.0) != 0.0
                }

                _uiState.value = EventsMapUiState(
                    isLoading = false,
                    events = events,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = EventsMapUiState(
                    isLoading = false,
                    events = emptyList(),
                    errorMessage = e.message ?: "Error cargando eventos"
                )
            }
        }
    }
}
