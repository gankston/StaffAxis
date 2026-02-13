package com.registro.empleados.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.registro.empleados.data.database.entities.Empleado
import com.registro.empleados.data.database.entities.RegistroAsistencia
import com.registro.empleados.data.database.entities.DiaLaboral
import com.registro.empleados.data.database.daos.EmpleadoDao
import com.registro.empleados.data.database.daos.RegistroAsistenciaDao
import com.registro.empleados.data.database.daos.DiaLaboralDao

/**
 * Base de datos principal de la aplicación usando Room.
 * 
 * Configuración:
 * - Versión 1 (inicial)
 * - 3 entidades: Empleado, RegistroAsistencia, DiaLaboral
 * - TypeConverters para manejo de fechas
 * - ExportSchema habilitado para migraciones
 */
@Database(
    entities = [
        Empleado::class, 
        RegistroAsistencia::class, 
        DiaLaboral::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * DAO para operaciones con empleados.
     */
    abstract fun empleadoDao(): EmpleadoDao
    
    /**
     * DAO para operaciones con registros de asistencia.
     */
    abstract fun registroAsistenciaDao(): RegistroAsistenciaDao
    
    /**
     * DAO para operaciones con días laborales.
     */
    abstract fun diaLaboralDao(): DiaLaboralDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia de la base de datos usando el patrón Singleton.
         * 
         * @param context Contexto de la aplicación
         * @return Instancia de AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "asistencia_database"
                )
                .fallbackToDestructiveMigration()  // Solo en desarrollo
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Destruye la instancia de la base de datos.
         * Útil para testing o reinicio completo.
         */
        fun destroyInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
