package com.registro.empleados.data.local.mapper

import com.registro.empleados.data.local.entity.AusenciaEntity
import com.registro.empleados.domain.model.Ausencia

object AusenciaMapper {
    
    fun toDomain(entity: AusenciaEntity): Ausencia {
        return Ausencia(
            id = entity.id,
            legajoEmpleado = entity.legajoEmpleado,
            nombreEmpleado = entity.nombreEmpleado,
            fechaInicio = entity.fechaInicio,
            fechaFin = entity.fechaFin,
            motivo = entity.motivo,
            fechaCreacion = entity.fechaCreacion
        )
    }
    
    fun toEntity(domain: Ausencia): AusenciaEntity {
        return AusenciaEntity(
            id = domain.id,
            legajoEmpleado = domain.legajoEmpleado,
            nombreEmpleado = domain.nombreEmpleado,
            fechaInicio = domain.fechaInicio,
            fechaFin = domain.fechaFin,
            motivo = domain.motivo,
            fechaCreacion = domain.fechaCreacion
        )
    }
}
