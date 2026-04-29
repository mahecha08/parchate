package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MapSelectionAddress(
    val country: String = "",
    val city: String = "",
    val addressLine: String = "",
    val placeName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

class MapSelectionViewModel : ViewModel() {

    private val _selectedLocation = MutableStateFlow<MapSelectionAddress?>(null)
    val selectedLocation: StateFlow<MapSelectionAddress?> = _selectedLocation.asStateFlow()

    fun setSelectedLocation(location: MapSelectionAddress) {
        _selectedLocation.value = location
    }

    fun clearSelectedLocation() {
        _selectedLocation.value = null
    }
}