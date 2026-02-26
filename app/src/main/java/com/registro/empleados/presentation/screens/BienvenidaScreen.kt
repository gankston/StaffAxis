package com.registro.empleados.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.registro.empleados.R
import com.registro.empleados.domain.model.EncargadosDisponibles
import com.registro.empleados.presentation.viewmodel.BienvenidaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienvenidaScreen(
    viewModel: BienvenidaViewModel = hiltViewModel(),
    onConfiguracionCompletada: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandido by remember { mutableStateOf(false) }
    val encargados = remember { EncargadosDisponibles.ENCARGADOS_SECTORES }
    
    LaunchedEffect(uiState.configuracionGuardada) {
        if (uiState.configuracionGuardada) {
            onConfiguracionCompletada()
        }
    }
    
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
            Image(
                painter = painterResource(id = R.drawable.logo_staffaxis),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Bienvenido a StaffAxis",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            when {
                !uiState.mostrarSelectorEncargado -> {
                    // Pantalla inicial: solo botón Continuar
                    Button(
                        onClick = { viewModel.onContinuarClicked() },
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
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                else -> {
                    // Selector de encargado (encargado = sector)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A3E)
                        )
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
                                onExpandedChange = { expandido = it }
                            ) {
                                OutlinedTextField(
                                    value = uiState.encargadoSeleccionado?.let { 
                                        "${it.nombreEncargado} - ${it.sector}" 
                                    } ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { 
                                        Text("Seleccione un encargado", color = Color(0xFF888888)) 
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color(0xFF26C6DA)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF26C6DA),
                                        unfocusedBorderColor = Color(0xFF555555),
                                        focusedContainerColor = Color(0x11FFFFFF),
                                        unfocusedContainerColor = Color(0x08FFFFFF)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                
                                ExposedDropdownMenu(
                                    expanded = expandido,
                                    onDismissRequest = { expandido = false },
                                    modifier = Modifier.background(Color(0xFF2A2A3E))
                                ) {
                                    encargados.forEach { encargado ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(
                                                        encargado.nombreEncargado,
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                    Text(
                                                        encargado.sector,
                                                        color = Color(0xFF26C6DA),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.onEncargadoSelected(encargado)
                                                expandido = false
                                            },
                                            modifier = Modifier.background(Color(0xFF2A2A3E))
                                        )
                                    }
                                }
                            }
                            
                            if (uiState.isLoading) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
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
                                    color = Color(0xFFFF5252),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
