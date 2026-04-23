package com.universidad.parchate.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.parchate.data.repository.CreateEventRequest
import com.universidad.parchate.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UpdateEventUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "Concierto",
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val direccion: String = "",
    val ciudad: String = "",
    val precio: String = "",
    val gratis: Boolean = true,
    val modalidad: String = "Presencial",
    val organizadorNombre: String = "",
    val contactoOrganizador: String = "",
    val capacidad: String = "",
    val etiquetas: String = "",
    val destacado: Boolean = false,
    val imageUri: Uri? = null,
    val currentImageUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class UpdateEventViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateEventUiState())
    val uiState = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getEventById(eventId).onSuccess { evento ->
                evento?.let { e ->
                    _uiState.update { it.copy(
                        titulo = e.titulo,
                        descripcion = e.descripcion,
                        categoria = e.categoria,
                        fecha = e.fecha,
                        hora = e.hora,
                        ubicacion = e.ubicacion,
                        direccion = e.direccion,
                        ciudad = e.ciudad,
                        precio = e.precio?.toString() ?: "",
                        gratis = e.gratis,
                        modalidad = e.modalidad,
                        organizadorNombre = e.organizadorNombre,
                        contactoOrganizador = e.contactoOrganizador,
                        capacidad = e.capacidad?.toString() ?: "",
                        etiquetas = e.etiquetas.joinToString(", "),
                        destacado = e.destacado,
                        currentImageUrl = e.imagenUrl,
                        isLoading = false
                    ) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun onFieldChange(update: (UpdateEventUiState) -> UpdateEventUiState) {
        _uiState.update(update)
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun updateEvent(eventId: String, onSuccess: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val request = CreateEventRequest(
                titulo = state.titulo,
                descripcion = state.descripcion,
                categoria = state.categoria,
                fecha = state.fecha,
                hora = state.hora,
                ubicacion = state.ubicacion,
                direccion = state.direccion,
                ciudad = state.ciudad,
                precio = state.precio.toDoubleOrNull() ?: 0.0,
                gratis = state.gratis,
                modalidad = state.modalidad,
                organizadorNombre = state.organizadorNombre,
                contactoOrganizador = state.contactoOrganizador,
                capacidad = state.capacidad.toIntOrNull() ?: 0,
                etiquetas = state.etiquetas.split(",").map { it.trim() },
                destacado = state.destacado,
                imageUri = state.imageUri
            )

            repository.updateEvent(eventId, request).onSuccess {
                _uiState.update { it.copy(isLoading = false, successMessage = "Evento actualizado") }
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
}