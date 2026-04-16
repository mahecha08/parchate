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
    val precio: String = "",
    val gratis: Boolean = true,
    val modalidad: String = "Presencial",
    val organizadorNombre: String = "",
    val contactoOrganizador: String = "",
    val capacidad: String = "",
    val etiquetas: String = "",
    val destacado: Boolean = false,
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class CreateEventViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    fun onFieldChange(transform: (CreateEventUiState) -> CreateEventUiState) {
        _uiState.update(transform)
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri, errorMessage = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun saveEvent(onSuccess: () -> Unit) {
        val state = _uiState.value
        val validationError = validate(state)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            val result = repository.createEvent(
                CreateEventRequest(
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
                    modalidad = state.modalidad.lowercase(),
                    organizadorNombre = state.organizadorNombre,
                    contactoOrganizador = state.contactoOrganizador,
                    capacidad = state.capacidad.toIntOrNull() ?: 0,
                    etiquetas = state.etiquetas
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() },
                    destacado = state.destacado,
                    imageUri = state.imageUri
                )
            )

            result
                .onSuccess {
                    _uiState.value = CreateEventUiState(successMessage = "Evento creado correctamente")
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo crear el evento"
                        )
                    }
                }
        }
    }

    private fun validate(state: CreateEventUiState): String? {
        return when {
            state.titulo.isBlank() -> "Debes ingresar el título del evento"
            state.descripcion.isBlank() -> "Debes ingresar una descripción"
            state.fecha.isBlank() -> "Debes ingresar la fecha en formato AAAA-MM-DD"
            !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(state.fecha) -> "La fecha debe estar en formato AAAA-MM-DD"
            state.hora.isBlank() -> "Debes seleccionar la hora"
            !Regex("^\\d{2}:\\d{2}$").matches(state.hora) -> "La hora debe estar en formato HH:MM"
            state.ubicacion.isBlank() -> "Debes ingresar el lugar del evento"
            state.direccion.isBlank() -> "Debes ingresar la dirección"
            state.ciudad.isBlank() -> "Debes ingresar la ciudad"
            state.organizadorNombre.isBlank() -> "Debes ingresar el nombre del organizador"
            state.contactoOrganizador.isBlank() -> "Debes ingresar el contacto del organizador"
            state.imageUri == null -> "Debes seleccionar una imagen del evento"
            !state.gratis && (state.precio.toDoubleOrNull() == null || (state.precio.toDoubleOrNull() ?: 0.0) <= 0.0) ->
                "Debes ingresar un precio válido"
            state.capacidad.isBlank() || (state.capacidad.toIntOrNull() ?: 0) <= 0 -> "Debes ingresar una capacidad válida"
            else -> null
        }
    }
}
