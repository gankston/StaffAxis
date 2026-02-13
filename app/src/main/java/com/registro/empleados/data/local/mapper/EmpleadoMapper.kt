package com.registro.empleados.data.local.mapper

import com.registro.empleados.data.local.entity.EmpleadoEntity
import com.registro.empleados.domain.model.Empleado

/**
 * Mapper para convertir entre EmpleadoEntity y Empleado.
 */
object EmpleadoMapper {
    
    /**
     * Convierte EmpleadoEntity a Empleado.
     */
    fun toDomain(entity: EmpleadoEntity): Empleado {
        return Empleado(
            id = entity.id,
            legajo = entity.legajo,  // Puede ser null
            nombreCompleto = entity.nombreCompleto,
            sector = entity.sector,
            fechaIngreso = entity.fechaIngreso,
            activo = entity.activo,
            fechaCreacion = entity.fechaCreacion,
            observacion = entity.observacion
        )
    }
    
    /**
     * Convierte Empleado a EmpleadoEntity.
     */
    fun toEntity(domain: Empleado): EmpleadoEntity {
        return EmpleadoEntity(
            id = domain.id,
            legajo = domain.legajo,  // Puede ser null
            nombreCompleto = domain.nombreCompleto,
            sector = domain.sector,
            fechaIngreso = domain.fechaIngreso,
            activo = domain.activo,
            fechaCreacion = domain.fechaCreacion,
            observacion = domain.observacion
        )
    }
    
    /**
     * Convierte lista de entidades a lista de dominios.
     */
    fun toDomainList(entities: List<EmpleadoEntity>): List<Empleado> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * Convierte lista de dominios a lista de entidades.
     */
    fun toEntityList(domains: List<Empleado>): List<EmpleadoEntity> {
        return domains.map { toEntity(it) }
    }
}
