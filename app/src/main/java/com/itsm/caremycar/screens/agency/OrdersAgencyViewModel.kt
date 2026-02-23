package com.itsm.caremycar.screens.agency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersAgencyViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersAgencyUiState())
    val uiState: StateFlow<OrdersAgencyUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = repository.listOrders(
                query = _uiState.value.searchQuery.ifBlank { null },
                status = _uiState.value.selectedStatus,
                page = 1,
                limit = 50
            )

            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            orders = data?.items?.map { dto -> dto.toOrder() } ?: emptyList(),
                            allCount = data?.allCount ?: 0,
                            pendingCount = data?.pendingCount ?: 0
                        ) 
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        loadOrders()
    }

    fun onStatusFilterChange(status: String) {
        _uiState.update { it.copy(selectedStatus = status) }
        loadOrders()
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrder(orderId, mapOf("status" to newStatus))
            loadOrders()
        }
    }
}
