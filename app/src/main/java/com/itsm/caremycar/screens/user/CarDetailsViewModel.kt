package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarDetailsUiState())
    val uiState: StateFlow<CarDetailsUiState> = _uiState.asStateFlow()

    fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            when (val result = vehicleRepository.getVehicleById(vehicleId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        vehicle = result.data,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun updateVehicle(
        vehicleId: String,
        make: String,
        model: String,
        year: String,
        mileage: String,
        color: String,
        fuelType: String,
        transmission: String,
        vehicleType: String
    ) {
        val current = _uiState.value.vehicle ?: return
        val yearInt = year.trim().toIntOrNull()
        val mileageInt = mileage.trim().toIntOrNull()

        if (make.isBlank() || model.isBlank() || yearInt == null || mileageInt == null) {
            _uiState.value = _uiState.value.copy(error = "Marca, modelo, año y kilometraje son requeridos.")
            return
        }

        val payload = mutableMapOf<String, Any>()
        if (make.trim() != (current.make ?: "")) payload["make"] = make.trim()
        if (model.trim() != (current.model ?: "")) payload["model"] = model.trim()
        if (yearInt != (current.year ?: 0)) payload["year"] = yearInt
        if (mileageInt != (current.currentMileage?.toInt() ?: 0)) payload["current_mileage"] = mileageInt
        if (color.trim() != (current.color ?: "")) payload["color"] = color.trim()
        if (fuelType.trim().lowercase() != (current.fuelType ?: "").lowercase()) {
            payload["fuel_type"] = fuelType.trim().lowercase()
        }
        if (transmission.trim().lowercase() != (current.transmission ?: "").lowercase()) {
            payload["transmission"] = transmission.trim().lowercase()
        }
        if (vehicleType.trim().lowercase() != (current.vehicleType ?: "").lowercase()) {
            payload["vehicle_type"] = vehicleType.trim().lowercase()
        }

        if (payload.isEmpty()) {
            _uiState.value = _uiState.value.copy(successMessage = "No hay cambios para guardar.", error = null)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            when (val result = vehicleRepository.updateVehicle(vehicleId, payload)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        vehicle = result.data,
                        successMessage = "Vehículo actualizado correctamente.",
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
