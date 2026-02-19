package com.itsm.caremycar.screens.user

data class AddVehicleUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
