package com.registro.empleados.presentation.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/**
 * Componente de navegación de períodos.
 */
@Composable
fun PeriodoNavigation(
    periodoActual: String,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAnterior) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Período anterior")
            }
            
            Text(
                text = periodoActual,
                style = MaterialTheme.typography.titleMedium
            )
            
            IconButton(onClick = onSiguiente) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Período siguiente")
            }
        }
    }
}