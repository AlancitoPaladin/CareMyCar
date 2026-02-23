package com.itsm.caremycar.screens.agency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.vehicle.Order

enum class EstadoPedido(val label: String, val color: Color, val icon: ImageVector) {
    PENDIENTE("Pendiente", Color(0xFFFFA000), Icons.Default.Info),
    CONFIRMADO("Confirmado", Color(0xFF1976D2), Icons.Default.CheckCircle),
    ENTREGADO("Entregado", Color(0xFF388E3C), Icons.Default.LocalShipping),
    CANCELADO("Cancelado", Color(0xFFD32F2F), Icons.Default.Close);

    companion object {
        fun fromString(status: String): EstadoPedido {
            return when (status.lowercase()) {
                "confirmed" -> CONFIRMADO
                "delivered" -> ENTREGADO
                "canceled" -> CANCELADO
                else -> PENDIENTE
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddOrder: () -> Unit = {},
    viewModel: OrdersAgencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pedidos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF4FA3D1)
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        Text(
                            text = "Pendientes: ${uiState.pendingCount}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddOrder,
                containerColor = Color(0xFF4FA3D1),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Añadir pedido",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            SearchBar(
                searchText = uiState.searchQuery,
                onSearchTextChange = { viewModel.onSearchQueryChange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4FA3D1))
                }
            } else if (uiState.orders.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.orders) { order ->
                        PedidoCard(
                            order = order,
                            onClick = { selectedOrder = order }
                        )
                    }
                }
            }
        }
    }

    if (selectedOrder != null) {
        OrderDetailsDialog(
            order = selectedOrder!!,
            onDismiss = { selectedOrder = null },
            onStatusChange = { newStatus ->
                viewModel.updateOrderStatus(selectedOrder!!.id, newStatus)
                selectedOrder = null
            }
        )
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, "Buscar", tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = { Text("Buscar por cliente o pieza...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
            Text("No hay pedidos registrados", color = Color.Gray)
        }
    }
}

@Composable
fun PedidoCard(
    order: Order,
    onClick: () -> Unit
) {
    val status = EstadoPedido.fromString(order.status)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(status.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(status.icon, null, tint = status.color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(order.clientName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(order.partName ?: "Sin nombre", color = Color.Gray, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${order.totalPrice}", fontWeight = FontWeight.Bold, color = Color(0xFF4FA3D1))
                Text(status.label, color = status.color, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun OrderDetailsDialog(
    order: Order,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Detalles del Pedido", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Cliente: ${order.clientName}")
                Text("Pieza: ${order.partName}")
                Text("Vehículo: ${order.make} ${order.model} (${order.year})")
                Text("VIN: ${order.vin}")
                Text("Total: $${order.totalPrice}")
                HorizontalDivider()
                Text("Cambiar Estado:", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    statusButton(EstadoPedido.PENDIENTE, "pending", onStatusChange)
                    statusButton(EstadoPedido.CONFIRMADO, "confirmed", onStatusChange)
                    statusButton(EstadoPedido.ENTREGADO, "delivered", onStatusChange)
                    statusButton(EstadoPedido.CANCELADO, "canceled", onStatusChange)
                }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cerrar") }
            }
        }
    }
}

@Composable
fun statusButton(estado: EstadoPedido, statusKey: String, onStatusChange: (String) -> Unit) {
    IconButton(onClick = { onStatusChange(statusKey) }) {
        Icon(estado.icon, contentDescription = estado.label, tint = estado.color)
    }
}
