package com.registro.empleados.data.mapper

import com.registro.empleados.data.local.entity.DiaLaboralEntity
import com.registro.empleados.domain.model.DiaLaboral as DiaLaboralDomain
import java.time.LocalDate

object DiaLaboralMapper {

    fun toDomain(entity: DiaLaboralEntity): DiaLaboralDomain {
        return DiaLaboralDomain(
            fecha = entity.fecha,
            esLaboral = entity.esLaboral,
            tipoDia = stringToTipoDia(entity.tipoDia),
            descripcion = entity.descripcion
        )
    }

    fun toEntity(domain: DiaLaboralDomain): DiaLaboralEntity {
        return DiaLaboralEntity(
            fecha = domain.fecha,
            esLaboral = domain.esLaboral,
            tipoDia = tipoDiaToString(domain.tipoDia),
            descripcion = domain.descripcion
        )
    }

    fun toDomainList(entities: List<DiaLaboralEntity>): List<DiaLaboralDomain> {
        return entities.map { toDomain(it) }
    }

    private fun stringToTipoDia(tipoString: String): DiaLaboralDomain.TipoDia {
        return when (tipoString.uppercase()) {
            "LABORAL" -> DiaLaboralDomain.TipoDia.LABORAL
            "FERIADO" -> DiaLaboralDomain.TipoDia.FERIADO
            "FIN_DE_SEMANA" -> DiaLaboralDomain.TipoDia.FIN_DE_SEMANA
            else -> DiaLaboralDomain.TipoDia.LABORAL
        }
    }

    private fun tipoDiaToString(tipoDia: DiaLaboralDomain.TipoDia): String {
        return when (tipoDia) {
            DiaLaboralDomain.TipoDia.LABORAL -> "LABORAL"
            DiaLaboralDomain.TipoDia.FERIADO -> "FERIADO"
            DiaLaboralDomain.TipoDia.FIN_DE_SEMANA -> "FIN_DE_SEMANA"
        }
    }
}
