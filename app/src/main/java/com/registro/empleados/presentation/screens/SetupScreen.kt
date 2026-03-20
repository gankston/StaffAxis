package com.registro.empleados.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.registro.empleados.presentation.viewmodel.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel(),
    onConfiguracionCompletada: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sectors by viewModel.sectors.collectAsState()
    var expandido by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.configuracionGuardada) {
        if (uiState.configuracionGuardada) onConfiguracionCompletada()
    }

    // En configuración inicial: mientras se sincroniza NO mostrar lista "offline" vieja.
    val showLoading = uiState.isSyncingSectors || uiState.isSaving

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A1B9A),
                        Color(0xFF4A148C),
                        Color(0xFF1E1E2E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A3E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Seleccione el Encargado",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = { if (!showLoading) expandido = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedSector?.let { s ->
                                "${s.encargado?.takeIf { it.isNotBlank() } ?: "Sin asignar"} - ${s.name}"
                            } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Seleccione un encargado", color = Color(0xFF888888)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            enabled = !showLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF26C6DA),
                                unfocusedBorderColor = Color(0xFF555555),
                                focusedContainerColor = Color(0x11FFFFFF),
                                unfocusedContainerColor = Color(0x08FFFFFF),
                                disabledTextColor = Color(0xFFAAAAAA),
                                disabledBorderColor = Color(0xFF444444),
                                disabledContainerColor = Color(0x08FFFFFF)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = { expandido = false },
                            modifier = Modifier.background(Color(0xFF2A2A3E))
                        ) {
                            sectors.forEach { sector ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                sector.encargado?.takeIf { it.isNotBlank() } ?: "Sin asignar",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                sector.name,
                                                color = Color(0xFF26C6DA),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.onSectorSelected(sector)
                                        expandido = false
                                    },
                                    modifier = Modifier.background(Color(0xFF2A2A3E))
                                )
                            }
                        }
                    }

                    if (showLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color(0xFF26C6DA),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "",
                            color = Color(0xFFFF6B6B),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.onContinuar() },
                        enabled = uiState.selectedSector != null && !showLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color(0xFF555555)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF9C27B0),
                                            Color(0xFF26C6DA)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Continuar",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

