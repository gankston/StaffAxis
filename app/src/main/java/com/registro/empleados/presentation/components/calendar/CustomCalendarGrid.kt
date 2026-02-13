package com.registro.empleados.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.model.RegistroAsistencia
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Componente de grilla de calendario personalizado (período 26-25).
 */
@Composable
fun CustomCalendarGrid(
    diasDelMes: List<LocalDate>,
    diasLaborales: Map<String, DiaLaboral>,
    registrosAsistencia: Map<String, List<RegistroAsistencia>>,
    fechaSeleccionada: LocalDate?,
    onFechaSeleccionada: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Encabezados de días de la semana
        items(diasSemana) { dia ->
            Card(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dia,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Días del mes
        items(diasDelMes) { fecha ->
            val fechaStr = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val diaLaboral = diasLaborales[fechaStr]
            val registros = registrosAsistencia[fechaStr] ?: emptyList()
            val esSeleccionado = fechaSeleccionada == fecha
            
            DiaCalendarItem(
                fecha = fecha,
                diaLaboral = diaLaboral,
                registros = registros,
                esSeleccionado = esSeleccionado,
                onClick = { onFechaSeleccionada(fecha) }
            )
        }
    }
}

@Composable
private fun DiaCalendarItem(
    fecha: LocalDate,
    diaLaboral: DiaLaboral?,
    registros: List<RegistroAsistencia>,
    esSeleccionado: Boolean,
    onClick: () -> Unit
) {
    val color = when {
        esSeleccionado -> MaterialTheme.colorScheme.primary
        diaLaboral?.tipoDia == DiaLaboral.TipoDia.FERIADO -> MaterialTheme.colorScheme.error
        diaLaboral?.tipoDia == DiaLaboral.TipoDia.FIN_DE_SEMANA -> MaterialTheme.colorScheme.outline
        registros.isNotEmpty() -> Color(0xFF4CAF50) // Verde para días con asistencia
        else -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when {
        esSeleccionado -> MaterialTheme.colorScheme.onPrimary
        diaLaboral?.tipoDia == DiaLaboral.TipoDia.FERIADO -> MaterialTheme.colorScheme.onError
        diaLaboral?.tipoDia == DiaLaboral.TipoDia.FIN_DE_SEMANA -> MaterialTheme.colorScheme.onSurface
        registros.isNotEmpty() -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (esSeleccionado) 2.dp else 0.dp,
                color = if (esSeleccionado) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (esSeleccionado) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Número del día
            Text(
                text = fecha.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 14.sp
            )
            
            // Indicador de asistencia
            if (registros.isNotEmpty()) {
                val tieneRegistro = registros.any { it.horasTrabajadas > 0 }
                
                if (tieneRegistro) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Green, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}