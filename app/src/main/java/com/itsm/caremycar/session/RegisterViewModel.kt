package com.itsm.caremycar.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.AuthRepository
import com.itsm.caremycar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String, name: String?) {
        viewModelScope.launch {
            // Validación básica
            if (email.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "El email es requerido"
                )
                return@launch
            }

            if (password.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "La contraseña es requerida"
                )
                return@launch
            }

            // Validación de email básica
            if (!isValidEmail(email)) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Email inválido"
                )
                return@launch
            }

            // Validación de contraseña básica
            if (password.length < 6) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "La contraseña debe tener al menos 6 caracteres"
                )
                return@launch
            }

            // Loading
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            // Llamada al repositorio
            when (val result = authRepository.register(email, password, name)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistered = true,
                        user = result.data,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    // Ya manejado arriba
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}