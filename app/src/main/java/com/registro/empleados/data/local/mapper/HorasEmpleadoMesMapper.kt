package com.registro.empleados.data.local.mapper

import com.registro.empleados.data.local.entity.HorasEmpleadoMesEntity
import com.registro.empleados.domain.model.HorasEmpleadoMes

/**
 * Mapper para convertir entre HorasEmpleadoMesEntity y HorasEmpleadoMes.
 */
object HorasEmpleadoMesMapper {
    
    /**
     * Convierte HorasEmpleadoMesEntity a HorasEmpleadoMes.
     */
    fun toDomain(entity: HorasEmpleadoMesEntity): HorasEmpleadoMes {
        return HorasEmpleadoMes(
            legajoEmpleado = entity.legajoEmpleado,
            año = entity.año,
            mes = entity.mes,
            totalHoras = entity.totalHoras,
            diasTrabajados = entity.diasTrabajados,
            promedioDiario = entity.promedioDiario,
            ultimaActualizacion = entity.ultimaActualizacion
        )
    }
    
    /**
     * Convierte HorasEmpleadoMes a HorasEmpleadoMesEntity.
     */
    fun toEntity(domain: HorasEmpleadoMes): HorasEmpleadoMesEntity {
        return HorasEmpleadoMesEntity(
            legajoEmpleado = domain.legajoEmpleado,
            año = domain.año,
            mes = domain.mes,
            totalHoras = domain.totalHoras,
            diasTrabajados = domain.diasTrabajados,
            promedioDiario = domain.promedioDiario,
            ultimaActualizacion = domain.ultimaActualizacion
        )
    }
    
    /**
     * Convierte lista de entidades a lista de dominios.
     */
    fun toDomainList(entities: List<HorasEmpleadoMesEntity>): List<HorasEmpleadoMes> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * Convierte lista de dominios a lista de entidades.
     */
    fun toEntityList(domains: List<HorasEmpleadoMes>): List<HorasEmpleadoMesEntity> {
        return domains.map { toEntity(it) }
    }
}
