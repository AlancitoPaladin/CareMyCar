package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.itsm.caremycar.session.LogoutViewModel
import com.itsm.caremycar.vehicle.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onAddVehicleClick: () -> Unit = {},
    onVehicleClick: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    shouldRefreshOnResume: Boolean = false,
    onRefreshHandled: () -> Unit = {},
    viewModel: VehicleViewModel = hiltViewModel(),
    logoutViewModel: LogoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val logoutUiState by logoutViewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(shouldRefreshOnResume) {
        if (shouldRefreshOnResume) {
            viewModel.loadVehicles()
            onRefreshHandled()
        }
    }

    LaunchedEffect(logoutUiState.isLoggedOut) {
        if (logoutUiState.isLoggedOut) {
            logoutViewModel.consumeLoggedOut()
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text(if (selectedTab == 0) "Mis vehículos" else "Productos") },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = viewModel::loadVehicles) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                        }
                    }
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        enabled = !logoutUiState.isLoggingOut
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
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
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Mis vehículos",
                            tint = if (selectedTab == 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  Mis vehículos",
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                TextButton(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Productos",
                            tint = if (selectedTab == 1) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  Productos",
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = onAddVehicleClick) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar vehículo")
                }
            }
        }
    ) { innerPadding ->
        if (selectedTab == 0) {
            UserScreenContent(
                innerPadding = innerPadding,
                uiState = uiState,
                onRetry = viewModel::loadVehicles,
                onVehicleClick = onVehicleClick,
                onDeleteVehicleClick = viewModel::requestDeleteVehicle
            )
        } else {
            ProductDetailsContent(innerPadding = innerPadding)
        }
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

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que deseas cerrar sesión?") },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    enabled = !logoutUiState.isLoggingOut
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        logoutViewModel.logout()
                    },
                    enabled = !logoutUiState.isLoggingOut
                ) {
                    Text("Cerrar sesión")
                }
            }
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
    val context = LocalContext.current
    val imageUrl = vehicle.imageUrls.firstOrNull()

    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
                AndroidView(
                    factory = { ctx ->
                        android.widget.ImageView(ctx).apply {
                            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                        }
                    },
                    modifier = Modifier
                        .width(96.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    update = { imageView ->
                        Glide.with(context).load(imageUrl).into(imageView)
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = vehicle.title,
                    style = MaterialTheme.typography.titleMedium
                )
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

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar vehículo",
                    tint = MaterialTheme.colorScheme.error
                )
            }
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
