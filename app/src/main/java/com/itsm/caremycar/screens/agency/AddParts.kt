package com.itsm.caremycar.screens.agency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsm.caremycar.ui.theme.CareMyCarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPartsScreen(
    onNavigateBack: () -> Unit = {},
    onAddPart: () -> Unit = {}
) {
    var partName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var selectedCompatibility by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Añadir Refacción",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4FA3D1),
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF4FA3D1))
                    .clickable { onAddPart() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Confirmar Refacción",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                label = "Ingrese Nombre de la pieza",
                value = partName,
                onValueChange = { partName = it },
                placeholder = "Nombre"
            )

            CustomDropdownField(
                label = "Categoria",
                selectedOption = selectedCategory,
                onOptionSelected = { selectedCategory = it },
                options = listOf("Motor", "Frenos", "Suspensión", "Eléctrico", "Carrocería"),
                placeholder = "Seleccione una categoria"
            )

            CustomDropdownField(
                label = "Año",
                selectedOption = selectedYear,
                onOptionSelected = { selectedYear = it },
                options = listOf("2020", "2021", "2022", "2023", "2024", "2025"),
                placeholder = "Seleccione año del Modelo"
            )

            CustomDropdownField(
                label = "Modelo",
                selectedOption = selectedModel,
                onOptionSelected = { selectedModel = it },
                options = listOf("Golf", "Jetta", "Vento", "Polo", "Tiguan"),
                placeholder = "Modelo"
            )

            CustomDropdownField(
                label = "Compatiblidad",
                selectedOption = selectedCompatibility,
                onOptionSelected = { selectedCompatibility = it },
                options = listOf("Universal", "Específico", "Multimarca"),
                placeholder = "Seleccione los modelos compatibles"
            )

            CustomTextField(
                label = "Precio",
                value = price,
                onValueChange = { price = it },
                placeholder = "Ingrese Precio final"
            )

            CustomTextField(
                label = "Cantidad",
                value = quantity,
                onValueChange = { quantity = it },
                placeholder = "Ingrese cantidad"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPartsScreenPreview() {
    CareMyCarTheme {
        AddPartsScreen()
    }
}
