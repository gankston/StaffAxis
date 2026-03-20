package com.registro.empleados.domain.repository

import com.registro.empleados.domain.model.Sector
import kotlinx.coroutines.flow.Flow

interface SectorRepository {
    fun observeSectors(): Flow<List<Sector>>
    suspend fun fetchSectorsFromServer(): List<Sector>
}

