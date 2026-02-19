package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.vehicle.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onAddVehicleClick: () -> Unit = {},
    onVehicleClick: (String) -> Unit = {},
    onProductsClick: () -> Unit = {},
    shouldRefreshOnResume: Boolean = false,
    onRefreshHandled: () -> Unit = {},
    viewModel: VehicleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(shouldRefreshOnResume) {
        if (shouldRefreshOnResume) {
            viewModel.loadVehicles()
            onRefreshHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("Mis vehículos") },
                actions = {
                    IconButton(onClick = viewModel::loadVehicles) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                TextButton(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Mis vehículos",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                TextButton(
                    onClick = onProductsClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Productos",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddVehicleClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar vehículo")
            }
        }
    ) { innerPadding ->
        UserScreenContent(
            innerPadding = innerPadding,
            uiState = uiState,
            onRetry = viewModel::loadVehicles,
            onVehicleClick = onVehicleClick,
            onDeleteVehicleClick = viewModel::requestDeleteVehicle
        )
    }

    val pendingDelete = uiState.vehiclePendingDelete
    if (pendingDelete != null) {
        DeleteVehicleDialog(
            vehicle = pendingDelete,
            isDeleting = uiState.isDeletingVehicle,
            error = uiState.deleteError,
            onConfirm = viewModel::confirmDeleteVehicle,
            onDismiss = viewModel::cancelDeleteVehicle
        )
    }
}

@Composable
private fun UserScreenContent(
    innerPadding: PaddingValues,
    uiState: VehicleUiState,
    onRetry: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onDeleteVehicleClick: (Vehicle) -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null && uiState.vehicles.isEmpty() -> {
            EmptyState(
                modifier = Modifier.padding(innerPadding),
                message = uiState.error,
                actionLabel = "Reintentar",
                onAction = onRetry
            )
        }

        uiState.vehicles.isEmpty() -> {
            EmptyState(
                modifier = Modifier.padding(innerPadding),
                message = "Aún no tienes vehículos registrados.",
                actionLabel = "Actualizar",
                onAction = onRetry
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoadingDetail) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 12.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Cargando detalle...")
                    }
                }

                uiState.detailError?.let { detailError ->
                    Text(
                        text = detailError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                uiState.deleteError?.let { deleteError ->
                    Text(
                        text = deleteError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.vehicles, key = { it.id }) { vehicle ->
                        val isRemoving = uiState.removingVehicleId == vehicle.id
                        AnimatedVisibility(
                            visible = !isRemoving,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            VehicleCard(
                                vehicle = vehicle,
                                onClick = { onVehicleClick(vehicle.id) },
                                onDeleteClick = { onDeleteVehicleClick(vehicle) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onAction,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(actionLabel)
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicle.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar vehículo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                text = vehicle.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Km: ${vehicle.currentMileage?.toInt() ?: 0}",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun DeleteVehicleDialog(
    vehicle: Vehicle,
    isDeleting: Boolean,
    error: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = { Text("Eliminar vehículo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("¿Seguro que deseas eliminar ${vehicle.title}? Esta acción no se puede deshacer.")
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isDeleting) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text("Eliminar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun UserScreenPreview() {
    val previewState = VehicleUiState(
        vehicles = listOf(
            Vehicle(
                id = "1",
                make = "Toyota",
                model = "Corolla",
                year = 2020,
                color = "Rojo",
                vehicleType = "sedan",
                transmission = "Automática",
                fuelType = "Gasolina",
                currentMileage = 54210.0,
                imageUrls = emptyList()
            ),
            Vehicle(
                id = "2",
                make = "Mazda",
                model = "CX-5",
                year = 2019,
                color = "Blanco",
                vehicleType = "suv",
                transmission = "Automática",
                fuelType = "Gasolina",
                currentMileage = 61234.0,
                imageUrls = emptyList()
            )
        )
    )

    Scaffold {
        UserScreenContent(
            innerPadding = it,
            uiState = previewState,
            onRetry = {},
            onVehicleClick = {},
            onDeleteVehicleClick = {}
        )
    }
}
