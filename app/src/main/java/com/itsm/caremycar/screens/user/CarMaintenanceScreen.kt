package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.vehicle.MaintenanceRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarMaintenanceContent(
    vehicleId: String,
    viewModel: CarMaintenanceViewModel = hiltViewModel()
) {
    val serviceTypeOptions = listOf(
        "Cambio de aceite",
        "Afinación",
        "Frenos",
        "Alineación y balanceo",
        "Llantas",
        "Batería",
        "Inspección general",
        "Otro"
    )

    val uiState by viewModel.uiState.collectAsState()
    var serviceType by remember { mutableStateOf("") }
    var serviceTypeExpanded by remember { mutableStateOf(false) }
    var serviceDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }

    LaunchedEffect(vehicleId) {
        viewModel.loadMaintenance(vehicleId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = serviceTypeExpanded,
            onExpandedChange = { serviceTypeExpanded = !serviceTypeExpanded }
        ) {
            OutlinedTextField(
                value = serviceType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de servicio *") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceTypeExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                singleLine = true
            )
            DropdownMenu(
                expanded = serviceTypeExpanded,
                onDismissRequest = { serviceTypeExpanded = false }
            ) {
                serviceTypeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            serviceType = option
                            serviceTypeExpanded = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(
            value = serviceDate,
            onValueChange = { serviceDate = it },
            label = { Text("Fecha de servicio (YYYY-MM-DD) *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Costo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = mileage,
            onValueChange = { mileage = it },
            label = { Text("Kilometraje") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = {
                viewModel.createMaintenance(
                    vehicleId = vehicleId,
                    serviceType = serviceType,
                    serviceDate = serviceDate,
                    description = description,
                    cost = cost,
                    mileage = mileage
                )
            },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
            }
            Text("Agregar mantenimiento")
        }

        uiState.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.items, key = { it.id }) { item ->
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.serviceType ?: "Servicio",
                                style = MaterialTheme.typography.titleMedium
                            )
                            androidx.compose.foundation.layout.Row {
                                IconButton(onClick = { viewModel.requestEdit(item) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.requestDelete(item) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        Text(text = "Fecha: ${item.serviceDate ?: "N/D"}")
                        Text(text = "Km: ${item.mileage ?: 0}")
                        Text(text = "Costo: ${item.cost ?: 0.0}")
                        item.description?.takeIf { it.isNotBlank() }?.let { desc ->
                            Text(text = desc, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    uiState.selectedItemForEdit?.let { item ->
        EditMaintenanceDialog(
            item = item,
            serviceTypeOptions = serviceTypeOptions,
            isSaving = uiState.isSaving,
            onDismiss = viewModel::dismissEdit,
            onSave = { serviceTypeValue, serviceDateValue, descriptionValue, costValue, mileageValue ->
                viewModel.updateMaintenance(
                    maintenanceId = item.id,
                    serviceType = serviceTypeValue,
                    serviceDate = serviceDateValue,
                    description = descriptionValue,
                    cost = costValue,
                    mileage = mileageValue
                )
            }
        )
    }

    uiState.selectedItemForDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { if (!uiState.isDeleting) viewModel.dismissDelete() },
            title = { Text("Eliminar mantenimiento") },
            text = { Text("¿Deseas eliminar el registro de ${item.serviceType ?: "servicio"}?") },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissDelete,
                    enabled = !uiState.isDeleting
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::confirmDeleteMaintenance,
                    enabled = !uiState.isDeleting
                ) {
                    Text("Eliminar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMaintenanceDialog(
    item: MaintenanceRecord,
    serviceTypeOptions: List<String>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var serviceType by remember(item.id) { mutableStateOf(item.serviceType.orEmpty()) }
    var serviceTypeExpanded by remember(item.id) { mutableStateOf(false) }
    var serviceDate by remember(item.id) { mutableStateOf(item.serviceDate.orEmpty()) }
    var description by remember(item.id) { mutableStateOf(item.description.orEmpty()) }
    var cost by remember(item.id) { mutableStateOf(item.cost?.toString().orEmpty()) }
    var mileage by remember(item.id) { mutableStateOf(item.mileage?.toString().orEmpty()) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Editar mantenimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = serviceTypeExpanded,
                    onExpandedChange = { serviceTypeExpanded = !serviceTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = serviceType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceTypeExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = serviceTypeExpanded,
                        onDismissRequest = { serviceTypeExpanded = false }
                    ) {
                        serviceTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    serviceType = option
                                    serviceTypeExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(value = serviceDate, onValueChange = { serviceDate = it }, label = { Text("Fecha") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Costo") })
                OutlinedTextField(value = mileage, onValueChange = { mileage = it }, label = { Text("Kilometraje") })
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(serviceType, serviceDate, description, cost, mileage) },
                enabled = !isSaving
            ) {
                Text("Guardar")
            }
        }
    )
}
