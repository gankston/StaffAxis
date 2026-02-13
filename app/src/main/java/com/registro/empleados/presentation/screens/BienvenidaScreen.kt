package com.registro.empleados.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.registro.empleados.R
import com.registro.empleados.presentation.viewmodel.BienvenidaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienvenidaScreen(
    viewModel: BienvenidaViewModel = hiltViewModel(),
    onConfiguracionCompletada: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Variables de estado para el dropdown
    var expandido by remember { mutableStateOf(false) }
    
    val sectores = listOf(
        "RUTA 5",
        "VIALSA",
        "MOSCONI",
        "CUCHUY",
        "CONSTRUCCION",
        "PICADO Y COSECHA"
    )
    
    // Navegar cuando se complete la configuración
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
                        Color(0xFF6A1B9A),  // Púrpura oscuro (respetando paleta actual)
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
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_staffaxis),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Título
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Configuración Inicial",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Card formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A3E)  // Fondo oscuro de la paleta actual
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Nombre del encargado
                    Text(
                        text = "Nombre del Encargado",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = uiState.nombreEncargado,
                        onValueChange = { valor ->
                            // Capitalizar cada palabra
                            val capitalizado = valor.split(" ")
                                .joinToString(" ") { palabra ->
                                    palabra.lowercase().replaceFirstChar { 
                                        if (it.isLowerCase()) it.titlecase() else it.toString() 
                                    }
                                }
                            viewModel.onNombreChanged(capitalizado)
                        },
                        placeholder = { 
                            Text("Ingrese su nombre", color = Color(0xFF888888)) 
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF26C6DA)  // Turquesa de la paleta
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF26C6DA),
                            unfocusedBorderColor = Color(0xFF555555),
                            cursorColor = Color(0xFF26C6DA),
                            focusedContainerColor = Color(0x11FFFFFF),
                            unfocusedContainerColor = Color(0x08FFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Selector de sector
                    Text(
                        text = "Seleccione el Sector",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    
                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = { expandido = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.sectorSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { 
                                Text("Seleccione un sector", color = Color(0xFF888888)) 
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Work,
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
                            sectores.forEach { sector ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            sector,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onClick = {
                                        viewModel.onSectorChanged(sector)
                                        expandido = false
                                    },
                                    modifier = Modifier.background(Color(0xFF2A2A3E))
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Mensaje de error
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = Color(0xFFFF5252),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    // Botón Continuar con gradiente
                    Button(
                        onClick = { viewModel.guardarConfiguracion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isLoading,
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
                                            Color(0xFF9C27B0),  // Púrpura
                                            Color(0xFF26C6DA)   // Turquesa
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    "Continuar",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
