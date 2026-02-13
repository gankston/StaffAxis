package com.registro.empleados.data.remote.mapper

import com.registro.empleados.data.remote.dto.FeriadoDto
import com.registro.empleados.domain.model.DiaLaboral
import java.time.LocalDate

/**
 * Mapper para convertir DTOs de feriados a entidades del dominio.
 */
object FeriadoMapper {
    
    /**
     * Convierte un FeriadoDto a DiaLaboral.
     * @param feriadoDto DTO del feriado
     * @param año Año del feriado
     * @return DiaLaboral representando el feriado
     */
    fun toDiaLaboral(feriadoDto: FeriadoDto, año: Int): DiaLaboral {
        val fecha = LocalDate.of(año, feriadoDto.mes, feriadoDto.dia)
        
        return DiaLaboral(
            fecha = fecha,
            esLaboral = false,
            tipoDia = DiaLaboral.TipoDia.FERIADO,
            descripcion = buildDescripcionCompleta(feriadoDto)
        )
    }
    
    /**
     * Convierte una lista de FeriadoDto a lista de DiaLaboral.
     * @param feriadosDto Lista de DTOs de feriados
     * @param año Año de los feriados
     * @return Lista de DiaLaboral representando los feriados
     */
    fun toDiaLaboralList(feriadosDto: List<FeriadoDto>, año: Int): List<DiaLaboral> {
        return feriadosDto.map { feriadoDto ->
            toDiaLaboral(feriadoDto, año)
        }
    }
    
    /**
     * Construye la descripción completa del feriado.
     * @param feriadoDto DTO del feriado
     * @return String con la descripción completa del feriado
     */
    private fun buildDescripcionCompleta(feriadoDto: FeriadoDto): String {
        val descripcion = StringBuilder()
        
        // Agregar el motivo principal
        descripcion.append(feriadoDto.motivo)
        
        // Agregar información del tipo si está disponible
        if (feriadoDto.tipo.isNotEmpty()) {
            descripcion.append(" (${feriadoDto.tipo})")
        }
        
        // Agregar información adicional si está disponible
        if (feriadoDto.info.isNotEmpty()) {
            descripcion.append(" - ${feriadoDto.info}")
        }
        
        return descripcion.toString()
    }
    
    /**
     * Determina el tipo de feriado basado en la información del DTO.
     * @param feriadoDto DTO del feriado
     * @return Tipo de día laboral correspondiente
     */
    private fun determinarTipoFeriado(feriadoDto: FeriadoDto): DiaLaboral.TipoDia {
        return when (feriadoDto.tipo.lowercase()) {
            "inamovible" -> DiaLaboral.TipoDia.FERIADO
            "trasladable" -> DiaLaboral.TipoDia.FERIADO
            "puente" -> DiaLaboral.TipoDia.FERIADO
            else -> DiaLaboral.TipoDia.FERIADO
        }
    }
}
