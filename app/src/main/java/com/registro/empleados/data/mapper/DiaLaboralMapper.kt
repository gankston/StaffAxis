package com.registro.empleados.data.mapper

import com.registro.empleados.data.database.entities.DiaLaboral as DiaLaboralEntity
import com.registro.empleados.domain.model.DiaLaboral as DiaLaboralDomain
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Mapper para convertir entre entidades de base de datos y modelos de dominio para DiaLaboral.
 */
object DiaLaboralMapper {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Convierte una entidad de base de datos a un modelo de dominio.
     * @param entity Entidad de base de datos
     * @return Modelo de dominio
     */
    fun toDomain(entity: DiaLaboralEntity): DiaLaboralDomain {
        return DiaLaboralDomain(
            fecha = stringToLocalDate(entity.fecha),
            esLaboral = entity.esLaboral,
            tipoDia = stringToTipoDia(entity.tipoDia),
            descripcion = entity.descripcion,
            fechaActualizacion = timestampToLocalDate(entity.fechaActualizacion)
        )
    }

    /**
     * Convierte un modelo de dominio a una entidad de base de datos.
     * @param domain Modelo de dominio
     * @return Entidad de base de datos
     */
    fun toEntity(domain: DiaLaboralDomain): DiaLaboralEntity {
        return DiaLaboralEntity(
            fecha = localDateToString(domain.fecha),
            esLaboral = domain.esLaboral,
            tipoDia = tipoDiaToString(domain.tipoDia),
            descripcion = domain.descripcion,
            fechaActualizacion = localDateToTimestamp(domain.fechaActualizacion)
        )
    }

    /**
     * Convierte una lista de entidades a una lista de modelos de dominio.
     * @param entities Lista de entidades
     * @return Lista de modelos de dominio
     */
    fun toDomainList(entities: List<DiaLaboralEntity>): List<DiaLaboralDomain> {
        return entities.map { toDomain(it) }
    }

    /**
     * Convierte un String de fecha (yyyy-MM-dd) a LocalDate.
     */
    private fun stringToLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, dateFormatter)
    }

    /**
     * Convierte un LocalDate a String de fecha (yyyy-MM-dd).
     */
    private fun localDateToString(localDate: LocalDate): String {
        return localDate.format(dateFormatter)
    }

    /**
     * Convierte un timestamp (Long) a LocalDate.
     */
    private fun timestampToLocalDate(timestamp: Long): LocalDate {
        return java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
    }

    /**
     * Convierte un LocalDate a timestamp (Long).
     */
    private fun localDateToTimestamp(localDate: LocalDate): Long {
        return localDate.atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Convierte un String a TipoDia enum.
     */
    private fun stringToTipoDia(tipoString: String): DiaLaboralDomain.TipoDia {
        return when (tipoString.uppercase()) {
            "LABORAL" -> DiaLaboralDomain.TipoDia.LABORAL
            "FERIADO" -> DiaLaboralDomain.TipoDia.FERIADO
            "FIN_DE_SEMANA" -> DiaLaboralDomain.TipoDia.FIN_DE_SEMANA
            else -> DiaLaboralDomain.TipoDia.LABORAL
        }
    }

    /**
     * Convierte un TipoDia enum a String.
     */
    private fun tipoDiaToString(tipoDia: DiaLaboralDomain.TipoDia): String {
        return when (tipoDia) {
            DiaLaboralDomain.TipoDia.LABORAL -> "LABORAL"
            DiaLaboralDomain.TipoDia.FERIADO -> "FERIADO"
            DiaLaboralDomain.TipoDia.FIN_DE_SEMANA -> "FIN_DE_SEMANA"
        }
    }
}
