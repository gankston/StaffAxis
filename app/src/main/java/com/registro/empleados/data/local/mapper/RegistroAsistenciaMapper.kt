package com.registro.empleados.data.local.mapper

import com.registro.empleados.data.local.entity.RegistroAsistenciaEntity
import com.registro.empleados.domain.model.RegistroAsistencia

/**
 * Mapper para convertir entre RegistroAsistenciaEntity y RegistroAsistencia.
 */
object RegistroAsistenciaMapper {
    
    /**
     * Convierte RegistroAsistenciaEntity a RegistroAsistencia.
     */
    fun toDomain(entity: RegistroAsistenciaEntity): RegistroAsistencia {
        return RegistroAsistencia(
            id = entity.id,
            legajoEmpleado = entity.legajoEmpleado ?: "",
            fecha = entity.fecha,
            horasTrabajadas = entity.horasTrabajadas,
            observaciones = entity.observaciones,
            fechaRegistro = entity.fechaRegistro
        )
    }
    
    /**
     * Convierte RegistroAsistencia a RegistroAsistenciaEntity.
     */
    fun toEntity(domain: RegistroAsistencia): RegistroAsistenciaEntity {
        return RegistroAsistenciaEntity(
            id = domain.id,
            idEmpleado = 0L,
            legajoEmpleado = domain.legajoEmpleado,
            fecha = domain.fecha,
            horasTrabajadas = domain.horasTrabajadas,
            observaciones = domain.observaciones,
            fechaRegistro = domain.fechaRegistro
        )
    }
    
    /**
     * Convierte lista de entidades a lista de dominios.
     */
    fun toDomainList(entities: List<RegistroAsistenciaEntity>): List<RegistroAsistencia> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * Convierte lista de dominios a lista de entidades.
     */
    fun toEntityList(domains: List<RegistroAsistencia>): List<RegistroAsistenciaEntity> {
        return domains.map { toEntity(it) }
    }
}