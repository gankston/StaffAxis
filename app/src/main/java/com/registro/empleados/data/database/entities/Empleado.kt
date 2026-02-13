package com.registro.empleados.data.database.entities

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * Entidad que representa un empleado en la base de datos.
 * El legajo es la clave primaria única e inmutable.
 */
@Entity(tableName = "empleados")
data class Empleado(
    @PrimaryKey
    @ColumnInfo(name = "legajo")
    val legajo: String, // Clave primaria, único e inmutable

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellido")
    val apellido: String,

    @ColumnInfo(name = "fecha_ingreso")
    val fechaIngreso: Long,  // Timestamp en milisegundos

    @ColumnInfo(name = "activo")
    val activo: Boolean = true,  // Para bajas lógicas

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis()
)
