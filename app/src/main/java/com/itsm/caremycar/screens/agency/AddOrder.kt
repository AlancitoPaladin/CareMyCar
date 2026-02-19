package com.itsm.caremycar.screens.agency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun AddOrderScreen(
    onNavigateBack: () -> Unit = {},
    onAddOrder: () -> Unit = {}
) {
    var clientName by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var selectedPart by remember { mutableStateOf("") }
    var selectedQuantity by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Añadir Pedido",
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
                    .clickable { onAddOrder() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Confirmar Pedido",
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

            // Input fields based on the image style
            CustomTextField(
                label = "Ingrese nombre del cliente.",
                value = clientName,
                onValueChange = { clientName = it },
                placeholder = "Nombre"
            )

            CustomTextField(
                label = "Ingrese VIN",
                value = vin,
                onValueChange = { vin = it },
                placeholder = "Vin"
            )

            CustomDropdownField(
                label = "Año del Modelo",
                selectedOption = selectedYear,
                onOptionSelected = { selectedYear = it },
                options = listOf("2020", "2021", "2022", "2023", "2024", "2025"),
                placeholder = "Año"
            )

            CustomDropdownField(
                label = "Modelo",
                selectedOption = selectedModel,
                onOptionSelected = { selectedModel = it },
                options = listOf("Golf", "Jetta"),
                placeholder = "Modelo"
            )

            CustomDropdownField(
                label = "Seleccione Refaccion",
                selectedOption = selectedPart,
                onOptionSelected = { selectedPart = it },
                options = listOf(
                    "Filtro de Aceite",
                    "Pastillas de Freno",
                    "Bujías",
                    "Amortiguadores"
                ),
                placeholder = "Pieza"
            )

            CustomDropdownField(
                label = "Cantidad",
                selectedOption = selectedQuantity,
                onOptionSelected = { selectedQuantity = it },
                options = listOf("1", "2", "3", "4", "5"),
                placeholder = "Pieza"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE8F0F5),
                unfocusedContainerColor = Color(0xFFE8F0F5),
                disabledContainerColor = Color(0xFFE8F0F5),
                focusedIndicatorColor = Color(0xFF4FA3D1),
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownField(
    label: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    options: List<String>,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, color = Color.LightGray) },
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF4FA3D1)
                    )
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE8F0F5),
                    unfocusedContainerColor = Color(0xFFE8F0F5),
                    disabledContainerColor = Color(0xFFE8F0F5),
                    focusedIndicatorColor = Color(0xFF4FA3D1),
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddOrderScreenPreview() {
    CareMyCarTheme {
        AddOrderScreen()
    }
}
