package com.registro.empleados.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.registro.empleados.data.local.dao.SectorDao
import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.local.entity.SectorEntity
import com.registro.empleados.data.remote.api.SectorsApiService
import com.registro.empleados.domain.model.Sector
import com.registro.empleados.domain.repository.SectorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SectorRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val sectorDao: SectorDao,
    private val sectorsApiService: SectorsApiService
) : SectorRepository {

    override fun observeSectors(): Flow<List<Sector>> {
        return sectorDao.getAllSectors().map { list ->
            list.map { Sector(id = it.id, name = it.name, encargado = it.encargado) }
        }
    }

    /**
     * Sync atómico:
     * - borra todo (deleteAll) para evitar balde viejo
     * - inserta los sectores nuevos
     */
    override suspend fun fetchSectorsFromServer(): List<Sector> = withContext(Dispatchers.IO) {
        val response = sectorsApiService.getSectors()
        if (!response.isSuccessful) {
            throw IllegalStateException("Error HTTP ${response.code()} al obtener sectores")
        }

        val sectorsDto = response.body()?.sectors.orEmpty()
        val entities = sectorsDto
            .filter { it.id.isNotBlank() && it.name.isNotBlank() }
            .map { SectorEntity(id = it.id, name = it.name, encargado = it.encargado) }

        database.withTransaction {
            sectorDao.deleteAll()
            sectorDao.insertAll(entities)
        }

        Log.d("DB_CHECK", "Sectores guardados en Room: ${entities.size}")

        entities.map { Sector(id = it.id, name = it.name, encargado = it.encargado) }
    }
}

