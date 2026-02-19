package com.itsm.caremycar.screens.user

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


enum class EstadoPedido(val label: String, val color: Color, val icon: ImageVector) {
    PENDIENTE("Pendiente", Color(0xFFFFA000), Icons.Default.Info),
    EN_PROCESO("En Proceso", Color(0xFF1976D2), Icons.Default.Refresh),
    COMPLETADO("Completado", Color(0xFF388E3C), Icons.Default.CheckCircle),
    CANCELADO("Cancelado", Color(0xFFD32F2F), Icons.Default.Close)
}

data class Pedido(
    val id: Int,
    val clienteNombre: String,
    val producto: String,
    val fecha: String,
    val precio: Double,
    val estado: EstadoPedido
)
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    pedidos: List<Pedido> = emptyList(),
    pedidosFiltrados: List<Pedido> = emptyList(),
    searchText: String = "",
    selectedEstadoFilter: EstadoPedido? = null,
    showDetailsDialog: Boolean = false,
    selectedPedido: Pedido? = null,
    onSearchTextChange: (String) -> Unit = {},
    onEstadoFilterChange: (EstadoPedido?) -> Unit = {},
    onNavigateToAddOrder: () -> Unit = {},
    onDetailsDialogShow: (Pedido) -> Unit = {},
    onDetailsDialogDismiss: () -> Unit = {},
    onEstadoChange: (Int, EstadoPedido) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit = {}
) {
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
                    Badge(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4FA3D1)
                    ) {
                        Text(
                            text = pedidos.count {
                                it.estado == EstadoPedido.EN_PROCESO ||
                                        it.estado == EstadoPedido.PENDIENTE
                            }.toString(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        },
        containerColor = Color(0xFFF5F5F5),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddOrder, // Updated to use navigation
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
                searchText = searchText,
                onSearchTextChange = onSearchTextChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilterChipsRow(
                pedidos = pedidos,
                selectedEstadoFilter = selectedEstadoFilter,
                onEstadoFilterChange = onEstadoFilterChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isNotEmpty() || selectedEstadoFilter != null) {
                Text(
                    text = "${pedidosFiltrados.size} resultado(s)",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (pedidosFiltrados.isEmpty()) {
                EmptyState()
            } else {
                PedidosList(
                    pedidos = pedidosFiltrados,
                    onPedidoClick = onDetailsDialogShow
                )
            }
        }
    }

    if (showDetailsDialog && selectedPedido != null) {
        PedidoDetailsDialog(
            pedido = selectedPedido,
            onDismiss = onDetailsDialogDismiss,
            onEstadoChange = { nuevoEstado ->
                onEstadoChange(selectedPedido.id, nuevoEstado)
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text(
                        "Buscar por cliente o producto...",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            if (searchText.isNotEmpty()) {
                IconButton(onClick = { onSearchTextChange("") }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipsRow(
    pedidos: List<Pedido>,
    selectedEstadoFilter: EstadoPedido?,
    onEstadoFilterChange: (EstadoPedido?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedEstadoFilter == null,
            onClick = { onEstadoFilterChange(null) },
            label = { Text("Todos (${pedidos.size})") },
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        FilterChip(
            selected = selectedEstadoFilter == EstadoPedido.PENDIENTE,
            onClick = {
                onEstadoFilterChange(
                    if (selectedEstadoFilter == EstadoPedido.PENDIENTE) null
                    else EstadoPedido.PENDIENTE
                )
            },
            label = {
                Text("Pendientes (${pedidos.count { it.estado == EstadoPedido.PENDIENTE }})")
            },
            leadingIcon = {
                Icon(
                    EstadoPedido.PENDIENTE.icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No se encontraron pedidos",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PedidosList(
    pedidos: List<Pedido>,
    onPedidoClick: (Pedido) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pedidos) { pedido ->
            PedidoCard(
                pedido = pedido,
                onClick = { onPedidoClick(pedido) }
            )
        }
    }
}

@Composable
fun PedidoCard(
    pedido: Pedido,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(pedido.estado.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = pedido.estado.icon,
                    contentDescription = null,
                    tint = pedido.estado.color
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pedido.clienteNombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = pedido.producto,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${pedido.precio}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4FA3D1)
                )
                Text(
                    text = pedido.fecha,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PedidoDetailsDialog(
    pedido: Pedido,
    onDismiss: () -> Unit,
    onEstadoChange: (EstadoPedido) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Detalles del Pedido", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Cliente: ${pedido.clienteNombre}")
                Text("Producto: ${pedido.producto}")
                Text("Precio: $${pedido.precio}")
                Text("Estado: ${pedido.estado.label}")
                HorizontalDivider()
                Text("Cambiar Estado:", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EstadoPedido.entries.forEach { estado ->
                        IconButton(onClick = { onEstadoChange(estado); onDismiss() }) {
                            Icon(estado.icon, contentDescription = estado.label, tint = estado.color)
                        }
                    }
                }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cerrar") }
            }
        }
    }
}

