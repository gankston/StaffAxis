package com.registro.empleados.data.mapper

import com.registro.empleados.data.local.entity.EmpleadoEntity
import com.registro.empleados.domain.model.Empleado

/**
 * Mapper para convertir entre entidades de base de datos y modelos de dominio para Empleado.
 */
object EmpleadoMapper {
    
    /**
     * Convierte una entidad de base de datos a un modelo de dominio.
     * @param entity Entidad de base de datos
     * @return Modelo de dominio
     */
    fun toDomain(entity: EmpleadoEntity): Empleado {
        return Empleado(
            id = entity.id,
            legajo = entity.legajo,
            nombreCompleto = entity.nombreCompleto,
            sector = entity.sector,
            fechaIngreso = entity.fechaIngreso,
            activo = entity.activo,
            fechaCreacion = entity.fechaCreacion,
            observacion = entity.observacion
        )
    }

    /**
     * Convierte un modelo de dominio a una entidad de base de datos.
     * @param domain Modelo de dominio
     * @return Entidad de base de datos
     */
    fun toEntity(domain: Empleado): EmpleadoEntity {
        return EmpleadoEntity(
            id = domain.id,
            legajo = domain.legajo,
            nombreCompleto = domain.nombreCompleto,
            sector = domain.sector,
            fechaIngreso = domain.fechaIngreso,
            activo = domain.activo,
            fechaCreacion = domain.fechaCreacion,
            observacion = domain.observacion
        )
    }

    /**
     * Convierte una lista de entidades a una lista de modelos de dominio.
     * @param entities Lista de entidades
     * @return Lista de modelos de dominio
     */
    fun toDomainList(entities: List<EmpleadoEntity>): List<Empleado> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * Convierte una lista de modelos de dominio a una lista de entidades.
     * @param domains Lista de modelos de dominio
     * @return Lista de entidades
     */
    fun toEntityList(domains: List<Empleado>): List<EmpleadoEntity> {
        return domains.map { toEntity(it) }
    }
}
