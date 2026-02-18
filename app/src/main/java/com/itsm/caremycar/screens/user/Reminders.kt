package com.itsm.caremycar.screens.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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

data class VehicleReminder(
    val id: String,
    val ownerName: String,
    val vehicleModel: String,
    val estimatedDate: String,
    val isSelected: Boolean = false
)

data class ReminderGroup(
    val id: String,
    val title: String,
    val count: Int,
    val reminders: List<VehicleReminder>,
    val isExpanded: Boolean = false,
    val isGroupSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit = {},
    onSendReminders: (String) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var reminderMessage by remember { 
        mutableStateOf("Recordatorio de Mantenimiento. Tu Vehiculo esta pronto por tener mantenimiento") 
    }
    
    // Mock data
    var groups by remember {
        mutableStateOf(
            listOf(
                ReminderGroup(
                    id = "1",
                    title = "Cambio de aceite",
                    count = 32,
                    isExpanded = true,
                    reminders = listOf(
                        VehicleReminder("1", "Jair Emiliano Romero", "VW Golf TSI 2.0 2018", "19/02/26"),
                        VehicleReminder("2", "Juan Perez", "VW Jetta Confortline 2024", "14/03/26")
                    )
                ),
                ReminderGroup(
                    id = "2",
                    title = "Servicio General",
                    count = 15,
                    reminders = emptyList()
                )
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Recordatorios",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.DarkGray
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
                            tint = Color.DarkGray
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Bottom blue bar as seen in the image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFF4FA3D1))
            )
        },
        containerColor = Color(0xFFE0F0F0) // Matching the background tint in the image
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E6ED))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Buscar usuario", fontSize = 14.sp, color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true
                    )
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // List of reminders
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(groups) { group ->
                    ReminderGroupItem(
                        group = group,
                        onToggleExpand = {
                            groups = groups.map { 
                                if (it.id == group.id) it.copy(isExpanded = !it.isExpanded) else it 
                            }
                        },
                        onToggleGroupSelection = { isSelected ->
                            groups = groups.map { g ->
                                if (g.id == group.id) {
                                    g.copy(
                                        isGroupSelected = isSelected,
                                        reminders = g.reminders.map { it.copy(isSelected = isSelected) }
                                    )
                                } else g
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message Box Section
            Text(
                text = "Estimado:",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD1DEE7))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = reminderMessage,
                        onValueChange = { reminderMessage = it },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color.DarkGray)
                    )
                    IconButton(onClick = { reminderMessage = "" }) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = "Limpiar",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onSendReminders(reminderMessage) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FA3D1))
                ) {
                    Text("Enviar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReminderGroupItem(
    group: ReminderGroup,
    onToggleExpand: () -> Unit,
    onToggleGroupSelection: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = group.isGroupSelected,
                onCheckedChange = onToggleGroupSelection,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF5C5CA7))
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "(${group.count} vehiculos)",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = group.isGroupSelected,
                    onCheckedChange = onToggleGroupSelection,
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF5C5CA7))
                )
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFD9E6ED), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = if (group.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.DarkGray
                    )
                }
            }
        }

        AnimatedVisibility(visible = group.isExpanded) {
            Column(
                modifier = Modifier.padding(start = 56.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                group.reminders.forEach { reminder ->
                    VehicleReminderDetail(reminder)
                }
            }
        }
    }
}

@Composable
fun VehicleReminderDetail(reminder: VehicleReminder) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = reminder.ownerName,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = reminder.vehicleModel,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            text = "Fecha aproximada de servicio:",
            fontSize = 13.sp,
            color = Color.Gray
        )
        Text(
            text = reminder.estimatedDate,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RemindersScreenPreview() {
    CareMyCarTheme {
        RemindersScreen()
    }
}
