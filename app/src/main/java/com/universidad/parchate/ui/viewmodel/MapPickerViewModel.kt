package com.universidad.parchate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.parchate.data.repository.AddressResult
import com.google.android.gms.maps.model.LatLng
import com.universidad.parchate.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapPickerUiState(
    val pais: String = "",
    val ciudad: String = "",
    val direccion: String = "",
    val ubicacion: String = "",
    val isResolvingAddress: Boolean = false,
    val errorMessage: String? = null
)

class MapPickerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(MapPickerUiState())
    val uiState: StateFlow<MapPickerUiState> = _uiState.asStateFlow()

    var selectedLatLng: LatLng? = null
        private set

    fun updateSelectedLocation(latLng: LatLng) {
        selectedLatLng = latLng
        resolveAddress(latLng)
    }

    fun setSelectedLocation(
        latLng: LatLng,
        addressResult: AddressResult
    ) {
        selectedLatLng = latLng
        _uiState.value = _uiState.value.copy(
            pais = addressResult.pais,
            ciudad = addressResult.ciudad,
            direccion = addressResult.direccion,
            ubicacion = addressResult.ubicacion.ifBlank { addressResult.direccion },
            isResolvingAddress = false,
            errorMessage = null
        )
    }

    private fun resolveAddress(latLng: LatLng) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isResolvingAddress = true,
                errorMessage = null
            )

            try {
                val result = locationRepository.resolveAddress(latLng)
                _uiState.value = _uiState.value.copy(
                    pais = result.pais,
                    ciudad = result.ciudad,
                    direccion = result.direccion,
                    ubicacion = result.ubicacion.ifBlank { result.direccion },
                    isResolvingAddress = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isResolvingAddress = false,
                    errorMessage = e.message ?: "No se pudo obtener la dirección"
                )
            }
        }
    }
}
