package com.registro.empleados.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.registro.empleados.data.local.entity.SectorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectorDao {

    @Query("SELECT * FROM sectors ORDER BY name ASC")
    fun getAllSectors(): Flow<List<SectorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sectors: List<SectorEntity>)

    @Query("SELECT id FROM sectors WHERE name = :name LIMIT 1")
    suspend fun getSectorIdByName(name: String): String?

    @Query("DELETE FROM sectors")
    suspend fun deleteAll()
}

