package com.registro.empleados.data.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Convertidores de tipos para Room Database.
 * Permite almacenar tipos complejos como LocalDate en SQLite.
 */
class Converters {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Convierte un timestamp (Long) a LocalDate.
     * Se utiliza para fechas de ingreso y creación.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { 
            java.time.Instant.ofEpochMilli(it)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    /**
     * Convierte un LocalDate a timestamp (Long).
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.let {
            it.atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }

    /**
     * Convierte un String de fecha (yyyy-MM-dd) a LocalDate.
     */
    @TypeConverter
    fun fromDateString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, dateFormatter) }
    }

    /**
     * Convierte un LocalDate a String de fecha (yyyy-MM-dd).
     */
    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }
}
