package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(
    vehicleId: String,
    onBack: () -> Unit,
    viewModel: CarDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicle(vehicleId)
    }

    LaunchedEffect(uiState.vehicle?.id) {
        val vehicle = uiState.vehicle ?: return@LaunchedEffect
        make = vehicle.make.orEmpty()
        model = vehicle.model.orEmpty()
        year = vehicle.year?.toString().orEmpty()
        mileage = vehicle.currentMileage?.toInt()?.toString().orEmpty()
        color = vehicle.color.orEmpty()
        fuelType = vehicle.fuelType.orEmpty()
        transmission = vehicle.transmission.orEmpty()
        vehicleType = vehicle.vehicleType.orEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                title = { Text("Detalle del vehículo") },
                actions = {
                    TextButton(onClick = onBack) {
                        Text("Cerrar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(vertical = 12.dp))
            }

            uiState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            uiState.successMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Modelo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Año") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("Kilometraje actual") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = fuelType,
                onValueChange = { fuelType = it },
                label = { Text("Combustible") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = transmission,
                onValueChange = { transmission = it },
                label = { Text("Transmisión") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = vehicleType,
                onValueChange = { vehicleType = it },
                label = { Text("Tipo de vehículo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    viewModel.updateVehicle(
                        vehicleId = vehicleId,
                        make = make,
                        model = model,
                        year = year,
                        mileage = mileage,
                        color = color,
                        fuelType = fuelType,
                        transmission = transmission,
                        vehicleType = vehicleType
                    )
                },
                enabled = !uiState.isSaving && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text("Guardar cambios")
            }
        }
    }
}
