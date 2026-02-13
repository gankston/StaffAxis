package com.registro.empleados.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.registro.empleados.domain.model.DiaLaboral

/**
 * Componente de leyenda para el calendario.
 */
@Composable
fun CalendarLegend(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Leyenda",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Día laboral",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(MaterialTheme.colorScheme.error)
                )
                Text(
                    text = "Feriado",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                Text(
                    text = "Fin de semana",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}