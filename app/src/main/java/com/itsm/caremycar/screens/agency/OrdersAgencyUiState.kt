package com.itsm.caremycar.screens.agency

import com.itsm.caremycar.vehicle.Order

data class OrdersAgencyUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedStatus: String = "all",
    val allCount: Int = 0,
    val pendingCount: Int = 0
)
