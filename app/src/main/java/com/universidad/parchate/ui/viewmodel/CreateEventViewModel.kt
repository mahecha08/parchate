package com.universidad.parchate.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.parchate.data.repository.CreateEventRequest
import com.universidad.parchate.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateEventUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "Concierto",
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val direccion: String = "",
    val ciudad: String = "",
    val pais: String = "",
    val precio: String = "",
    val gratis: Boolean = true,
    val modalidad: String = "presencial",
    val organizadorNombre: String = "",
    val contactoOrganizador: String = "",
    val capacidad: String = "",
    val etiquetas: String = "",
    val destacado: Boolean = false,
    val latitud: String = "",
    val longitud: String = "",
    val imageUri: Uri? = null,
    val isSaving: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null
)

class CreateEventViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    fun onFieldChange(transform: (CreateEventUiState) -> CreateEventUiState) {
        _uiState.update { current ->
            transform(current).copy(errorMessage = null)
        }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri, errorMessage = null) }
    }

    fun applySelectedLocation(
        latitud: Double?,
        longitud: Double?,
        pais: String?,
        ciudad: String?,
        direccion: String?,
        ubicacion: String?
    ) {
        if (
            latitud == null &&
            longitud == null &&
            pais == null &&
            ciudad == null &&
            direccion == null &&
            ubicacion == null
        ) {
            return
        }

        _uiState.update { current ->
            current.copy(
                latitud = latitud?.toString() ?: current.latitud,
                longitud = longitud?.toString() ?: current.longitud,
                pais = pais?.takeIf { it.isNotBlank() } ?: current.pais,
                ciudad = ciudad?.takeIf { it.isNotBlank() } ?: current.ciudad,
                direccion = direccion?.takeIf { it.isNotBlank() } ?: current.direccion,
                ubicacion = ubicacion?.takeIf { it.isNotBlank() } ?: current.ubicacion,
                errorMessage = null
            )
        }
    }

    fun saveEvent() {
        val state = _uiState.value
        val validationError = validate(state)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    success = false,
                    errorMessage = null
                )
            }

            repository.createEvent(
                CreateEventRequest(
                    titulo = state.titulo,
                    descripcion = state.descripcion,
                    categoria = state.categoria,
                    fecha = state.fecha,
                    hora = state.hora,
                    ubicacion = state.ubicacion,
                    direccion = state.direccion,
                    ciudad = state.ciudad,
                    pais = state.pais,
                    precio = state.precio.toDoubleOrNull() ?: 0.0,
                    gratis = state.gratis,
                    modalidad = state.modalidad,
                    organizadorNombre = state.organizadorNombre,
                    contactoOrganizador = state.contactoOrganizador,
                    capacidad = state.capacidad.toIntOrNull() ?: 0,
                    etiquetas = state.etiquetas
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() },
                    destacado = state.destacado,
                    latitud = state.latitud.toDoubleOrNull(),
                    longitud = state.longitud.toDoubleOrNull(),
                    imageUri = state.imageUri
                )
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            success = true,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            success = false,
                            errorMessage = error.message ?: "No se pudo guardar el evento"
                        )
                    }
                }
        }
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun validate(state: CreateEventUiState): String? {
        val isOnline = state.modalidad.equals("Online", ignoreCase = true)

        return when {
            state.titulo.isBlank() -> "Debes ingresar el título del evento"
            state.descripcion.isBlank() -> "Debes ingresar una descripción"
            state.fecha.isBlank() -> "Debes ingresar la fecha en formato AAAA-MM-DD"
            !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(state.fecha) ->
                "La fecha debe estar en formato AAAA-MM-DD"
            state.hora.isBlank() -> "Debes seleccionar la hora"
            !Regex("^\\d{2}:\\d{2}$").matches(state.hora) ->
                "La hora debe estar en formato HH:MM"
            state.ubicacion.isBlank() -> "Debes ingresar el lugar o referencia del evento"
            !isOnline && state.pais.isBlank() -> "Debes indicar el país del evento"
            !isOnline && state.ciudad.isBlank() -> "Debes indicar la ciudad del evento"
            !isOnline && state.direccion.isBlank() -> "Debes indicar la dirección del evento"
            !isOnline && (state.latitud.toDoubleOrNull() == null || state.longitud.toDoubleOrNull() == null) ->
                "Selecciona una ubicación válida en el mapa"
            state.organizadorNombre.isBlank() -> "Debes ingresar el nombre del organizador"
            state.contactoOrganizador.isBlank() -> "Debes ingresar el contacto del organizador"
            state.imageUri == null -> "Debes seleccionar una imagen del evento"
            !state.gratis && (state.precio.toDoubleOrNull() == null || (state.precio.toDoubleOrNull() ?: 0.0) <= 0.0) ->
                "Debes ingresar un precio válido"
            state.capacidad.isBlank() || (state.capacidad.toIntOrNull() ?: 0) <= 0 ->
                "Debes ingresar una capacidad válida"
            else -> null
        }
    }
}
