package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.CreateMaintenanceRequest
import com.itsm.caremycar.vehicle.MaintenanceRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CarMaintenanceViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    private val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")

    private val _uiState = MutableStateFlow(CarMaintenanceUiState())
    val uiState: StateFlow<CarMaintenanceUiState> = _uiState.asStateFlow()

    fun loadMaintenance(vehicleId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = vehicleRepository.listMaintenanceByVehicle(vehicleId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = result.data,
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

    fun createMaintenance(
        vehicleId: String,
        serviceType: String,
        serviceDate: String,
        description: String,
        cost: String,
        mileage: String
    ) {
        val st = serviceType.trim()
        val sd = serviceDate.trim()
        if (st.isBlank() || sd.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "service_type y service_date son requeridos (YYYY-MM-DD).")
            return
        }
        if (!dateRegex.matches(sd)) {
            _uiState.value = _uiState.value.copy(error = "service_date debe tener formato YYYY-MM-DD.")
            return
        }

        if (cost.isNotBlank() && cost.trim().toDoubleOrNull() == null) {
            _uiState.value = _uiState.value.copy(error = "cost debe ser numérico.")
            return
        }
        if (mileage.isNotBlank() && mileage.trim().toIntOrNull() == null) {
            _uiState.value = _uiState.value.copy(error = "mileage debe ser entero.")
            return
        }

        val request = CreateMaintenanceRequest(
            vehicleId = vehicleId,
            serviceType = st,
            serviceDate = sd,
            description = description.trim().ifBlank { null },
            cost = cost.trim().toDoubleOrNull(),
            mileage = mileage.trim().toIntOrNull()
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            when (val result = vehicleRepository.createMaintenance(request)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        items = listOf(result.data) + _uiState.value.items,
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

    fun requestEdit(item: MaintenanceRecord) {
        _uiState.value = _uiState.value.copy(selectedItemForEdit = item, error = null)
    }

    fun dismissEdit() {
        _uiState.value = _uiState.value.copy(selectedItemForEdit = null)
    }

    fun requestDelete(item: MaintenanceRecord) {
        _uiState.value = _uiState.value.copy(selectedItemForDelete = item, error = null)
    }

    fun dismissDelete() {
        _uiState.value = _uiState.value.copy(selectedItemForDelete = null)
    }

    fun updateMaintenance(
        maintenanceId: String,
        serviceType: String,
        serviceDate: String,
        description: String,
        cost: String,
        mileage: String
    ) {
        val payload = mutableMapOf<String, Any>()
        serviceType.trim().takeIf { it.isNotBlank() }?.let { payload["service_type"] = it }
        serviceDate.trim().takeIf { it.isNotBlank() }?.let {
            if (!dateRegex.matches(it)) {
                _uiState.value = _uiState.value.copy(error = "service_date debe tener formato YYYY-MM-DD.")
                return
            }
            payload["service_date"] = it
        }
        description.trim().takeIf { it.isNotBlank() }?.let { payload["description"] = it }
        if (cost.isNotBlank()) {
            val costValue = cost.trim().toDoubleOrNull()
            if (costValue == null) {
                _uiState.value = _uiState.value.copy(error = "cost debe ser numérico.")
                return
            }
            payload["cost"] = costValue
        }
        if (mileage.isNotBlank()) {
            val mileageValue = mileage.trim().toIntOrNull()
            if (mileageValue == null) {
                _uiState.value = _uiState.value.copy(error = "mileage debe ser entero.")
                return
            }
            payload["mileage"] = mileageValue
        }

        if (payload.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Agrega al menos un cambio para actualizar.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            when (val result = vehicleRepository.updateMaintenance(maintenanceId, payload)) {
                is Resource.Success -> {
                    val updated = result.data
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        selectedItemForEdit = null,
                        items = _uiState.value.items.map { if (it.id == updated.id) updated else it },
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

    fun confirmDeleteMaintenance() {
        val selected = _uiState.value.selectedItemForDelete ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, error = null)
            when (val result = vehicleRepository.deleteMaintenance(selected.id)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        selectedItemForDelete = null,
                        items = _uiState.value.items.filterNot { it.id == selected.id },
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }
}
