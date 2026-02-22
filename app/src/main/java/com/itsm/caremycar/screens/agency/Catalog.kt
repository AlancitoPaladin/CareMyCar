package com.itsm.caremycar.screens.agency

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.itsm.caremycar.ui.theme.CareMyCarTheme

data class RefaccionItem(
    val id: Int,
    val nombre: String,
    val isFavorite: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoRefaccionesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddParts: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var refacciones by remember {
        mutableStateOf(
            listOf(
                RefaccionItem(1, "Filtro de aceite", false),
                RefaccionItem(2, "Pastillas de freno", false)
            )
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRefaccion by remember { mutableStateOf<RefaccionItem?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Catálogo de refacciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            // Barra de búsqueda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, "Buscar", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Buscar refacc...", color = Color.Gray) },
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

            // Botón "Añadir refacción" que navega a AddParts
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onNavigateToAddParts() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4FA3D1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Add, "Añadir", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir refacción", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(refacciones) { refaccion ->
                    RefaccionListItem(
                        refaccion = refaccion,
                        onFavoriteClick = {
                            refacciones = refacciones.map {
                                if (it.id == refaccion.id) it.copy(isFavorite = !it.isFavorite) else it
                            }
                        },
                        onEditClick = { /* Lógica editar */ },
                        onDeleteClick = {
                            selectedRefaccion = refaccion
                            showDeleteDialog = true
                        },
                        onClick = { /* Ver detalles */ }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && selectedRefaccion != null) {
        DeleteConfirmationDialog(
            refaccionName = selectedRefaccion!!.nombre,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                refacciones = refacciones.filter { it.id != selectedRefaccion!!.id }
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun RefaccionListItem(
    refaccion: RefaccionItem,
    onFavoriteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    Icons.Default.Star,
                    null,
                    tint = if (refaccion.isFavorite) Color(0xFFFFD700) else Color.Gray
                )
            }
            Text(refaccion.nombre, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, null, tint = Color(0xFF4FA3D1))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935))
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(refaccionName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar refacción?") },
        text = { Text("¿Seguro que deseas eliminar $refaccionName?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CatalogoPreview() {
    CareMyCarTheme { CatalogoRefaccionesScreen() }
}
