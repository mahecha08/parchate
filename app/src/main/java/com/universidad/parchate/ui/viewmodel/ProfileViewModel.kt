package com.universidad.parchate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.parchate.data.repository.UpdateProfileRequest
import com.universidad.parchate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val nombres: String = "",
    val cedula: String = "",
    val correo: String = "",
    val fechaNacimiento: String = "",
    val bio: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getCurrentUser()
                .onSuccess { user ->
                    _uiState.update { state ->
                        state.copy(
                            nombres = user?.nombres ?: "",
                            cedula = user?.cedula ?: "",
                            correo = user?.correo ?: "",
                            fechaNacimiento = user?.fechaNacimiento ?: "",
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error: ${error.message}"
                        )
                    }
                }
        }
    }

    fun onFieldChange(transform: (ProfileUiState) -> ProfileUiState) {
        _uiState.update(transform)
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun updateProfile(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.nombres.isBlank() || state.cedula.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre y la cédula/teléfono son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            val result = repository.updateProfile(
                UpdateProfileRequest(
                    nombres = state.nombres,
                    cedula = state.cedula,
                    correo = state.correo,
                    fechaNacimiento = state.fechaNacimiento
                )
            )

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Perfil actualizado correctamente") }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al actualizar el perfil"
                        )
                    }
                }
        }
    }
}