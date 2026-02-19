package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.MaintenanceRecord

data class CarMaintenanceUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val items: List<MaintenanceRecord> = emptyList(),
    val selectedItemForEdit: MaintenanceRecord? = null,
    val selectedItemForDelete: MaintenanceRecord? = null,
    val error: String? = null
)
