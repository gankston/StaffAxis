package com.registro.empleados.data.repository

import com.registro.empleados.data.local.dao.AusenciaDao
import com.registro.empleados.data.local.mapper.AusenciaMapper
import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.repository.AusenciaRepository
import java.time.LocalDate
import javax.inject.Inject

class AusenciaRepositoryImpl @Inject constructor(
    private val ausenciaDao: AusenciaDao
) : AusenciaRepository {
    
    override suspend fun insertAusencia(ausencia: Ausencia): Long {
        return ausenciaDao.insertAusencia(AusenciaMapper.toEntity(ausencia))
    }
    
    override suspend fun updateAusencia(ausencia: Ausencia) {
        ausenciaDao.updateAusencia(AusenciaMapper.toEntity(ausencia))
    }
    
    override suspend fun deleteAusencia(ausencia: Ausencia) {
        ausenciaDao.deleteAusencia(AusenciaMapper.toEntity(ausencia))
    }
    
    override suspend fun getAusenciasByLegajo(legajo: String): List<Ausencia> {
        return ausenciaDao.getAusenciasByLegajo(legajo).map { 
            AusenciaMapper.toDomain(it) 
        }
    }
    
    override suspend fun getAusenciasByFecha(fecha: LocalDate): List<Ausencia> {
        return ausenciaDao.getAusenciasByFecha(fecha).map { 
            AusenciaMapper.toDomain(it) 
        }
    }
    
    override suspend fun getAusenciasByRango(
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<Ausencia> {
        return ausenciaDao.getAusenciasByRango(fechaInicio, fechaFin).map { 
            AusenciaMapper.toDomain(it) 
        }
    }
    
    override suspend fun empleadoTieneAusenciaEnFecha(
        legajo: String,
        fecha: LocalDate
    ): Boolean {
        val ausencia = ausenciaDao.getAusenciaByLegajoYFecha(legajo, fecha)
        android.util.Log.d("AusenciaRepository", "Buscando ausencia - Legajo: $legajo, Fecha: $fecha, Encontrada: ${ausencia != null}")
        return ausencia != null
    }
    
    override suspend fun getAllAusencias(): List<Ausencia> {
        return ausenciaDao.getAllAusencias().map { 
            AusenciaMapper.toDomain(it) 
        }
    }
}
