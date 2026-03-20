package com.registro.empleados.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sectors")
data class SectorEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val encargado: String? = null
)

