package com.registro.empleados.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Converter para LocalDate en Room.
 */
class LocalDateConverter {
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
}
