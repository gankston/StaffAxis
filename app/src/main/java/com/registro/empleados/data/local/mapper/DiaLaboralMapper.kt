package com.registro.empleados.data.local.mapper

import com.registro.empleados.data.local.entity.DiaLaboralEntity
import com.registro.empleados.domain.model.DiaLaboral

/**
 * Mapper para convertir entre DiaLaboralEntity y DiaLaboral.
 */
object DiaLaboralMapper {
    
    /**
     * Convierte DiaLaboralEntity a DiaLaboral.
     */
    fun toDomain(entity: DiaLaboralEntity): DiaLaboral {
        return DiaLaboral(
            fecha = entity.fecha,
            esLaboral = entity.esLaboral,
            tipoDia = DiaLaboral.TipoDia.valueOf(entity.tipoDia),
            descripcion = entity.descripcion
        )
    }
    
    /**
     * Convierte DiaLaboral a DiaLaboralEntity.
     */
    fun toEntity(domain: DiaLaboral): DiaLaboralEntity {
        return DiaLaboralEntity(
            fecha = domain.fecha,
            esLaboral = domain.esLaboral,
            tipoDia = domain.tipoDia.name,
            descripcion = domain.descripcion
        )
    }
    
    /**
     * Convierte lista de entidades a lista de dominios.
     */
    fun toDomainList(entities: List<DiaLaboralEntity>): List<DiaLaboral> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * Convierte lista de dominios a lista de entidades.
     */
    fun toEntityList(domains: List<DiaLaboral>): List<DiaLaboralEntity> {
        return domains.map { toEntity(it) }
    }
}