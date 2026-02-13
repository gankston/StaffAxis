package com.registro.empleados.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime

/**
 * Converter para LocalDateTime en Room.
 */
class LocalDateTimeConverter {
    
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it) }
    }
}
