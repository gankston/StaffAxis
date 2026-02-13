package com.registro.empleados.presentation.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.model.RegistroAsistencia
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Componente de tarjeta de detalles del día seleccionado.
 */
@Composable
fun DayDetailsCard(
    fecha: LocalDate,
    detalles: DiaLaboral?,
    registros: List<RegistroAsistencia>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Encabezado con fecha y tipo de día
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fecha.format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                detalles?.let { diaLaboral ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = when (diaLaboral.tipoDia) {
                            DiaLaboral.TipoDia.LABORAL -> MaterialTheme.colorScheme.primary
                            DiaLaboral.TipoDia.FERIADO -> MaterialTheme.colorScheme.error
                            DiaLaboral.TipoDia.FIN_DE_SEMANA -> MaterialTheme.colorScheme.outline
                        }
                    ) {
                        Text(
                            text = when (diaLaboral.tipoDia) {
                                DiaLaboral.TipoDia.LABORAL -> "Laboral"
                                DiaLaboral.TipoDia.FERIADO -> "Feriado"
                                DiaLaboral.TipoDia.FIN_DE_SEMANA -> "Fin de semana"
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = when (diaLaboral.tipoDia) {
                                DiaLaboral.TipoDia.LABORAL -> MaterialTheme.colorScheme.onPrimary
                                DiaLaboral.TipoDia.FERIADO -> MaterialTheme.colorScheme.onError
                                DiaLaboral.TipoDia.FIN_DE_SEMANA -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
            
            // Descripción del día si es feriado
            detalles?.descripcion?.let { descripcion ->
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Resumen de asistencia
            if (registros.isNotEmpty()) {
                HorizontalDivider()
                
                Text(
                    text = "Registros de Asistencia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Estadísticas del día
                val totalEmpleados = registros.size
                val conRegistro = registros.count { it.horasTrabajadas > 0 }
                val horasTotales = registros.sumOf { it.horasTrabajadas.toDouble() }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        icon = Icons.Default.People,
                        title = "Empleados",
                        value = totalEmpleados.toString()
                    )
                    StatCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Con Registro",
                        value = conRegistro.toString()
                    )
                    StatCard(
                        icon = Icons.Default.Schedule,
                        title = "Horas Totales",
                        value = String.format("%.1f", horasTotales)
                    )
                }
                
                HorizontalDivider()
                
                // Lista de registros
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(registros) { registro ->
                        RegistroItem(registro = registro)
                    }
                }
            } else {
                // No hay registros
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No hay registros de asistencia para este día",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RegistroItem(
    registro: RegistroAsistencia
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Legajo: ${registro.legajoEmpleado}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (registro.observaciones != null) {
                    Text(
                        text = registro.observaciones,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Horas: ${registro.horasTrabajadas}h",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                registro.observaciones?.let { obs ->
                    Text(
                        text = "Obs: $obs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}