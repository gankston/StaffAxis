package com.registro.empleados.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.registro.empleados.domain.model.Empleado
import java.time.format.DateTimeFormatter

/**
 * Componente de tarjeta para mostrar información de un empleado.
 */
@Composable
fun EmployeeCard(
    empleado: Empleado,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = empleado.nombreCompleto,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Legajo: ${empleado.legajo}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Sector: ${empleado.sector}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Ingreso: ${empleado.fechaIngreso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (empleado.activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = if (empleado.activo) "Activo" else "Inactivo",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}