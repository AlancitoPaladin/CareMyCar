package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.Vehicle

data class VehicleUiState(
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val isDeletingVehicle: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val removingVehicleId: String? = null,
    val selectedVehicle: Vehicle? = null,
    val vehiclePendingDelete: Vehicle? = null,
    val error: String? = null,
    val detailError: String? = null,
    val deleteError: String? = null
)
