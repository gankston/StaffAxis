package com.registro.empleados.di

import android.content.Context
import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.local.dao.AusenciaDao
import com.registro.empleados.data.local.dao.EmpleadoDao
import com.registro.empleados.data.local.dao.ApprovedAttendanceDao
import com.registro.empleados.data.local.dao.OutboxSubmissionDao
import com.registro.empleados.data.local.dao.RegistroAsistenciaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para la configuración de la base de datos.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Proporciona la instancia de AppDatabase.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    /**
     * Proporciona el DAO de ausencias.
     */
    @Provides
    fun provideAusenciaDao(database: AppDatabase): AusenciaDao {
        return database.ausenciaDao()
    }
    
    /**
     * Proporciona el DAO de empleados.
     */
    @Provides
    fun provideEmpleadoDao(database: AppDatabase): EmpleadoDao {
        return database.empleadoDao()
    }

    @Provides
    fun provideOutboxSubmissionDao(database: AppDatabase): OutboxSubmissionDao {
        return database.outboxSubmissionDao()
    }

    @Provides
    fun provideApprovedAttendanceDao(database: AppDatabase): ApprovedAttendanceDao {
        return database.approvedAttendanceDao()
    }

    @Provides
    fun provideRegistroAsistenciaDao(database: AppDatabase): RegistroAsistenciaDao {
        return database.registroAsistenciaDao()
    }
}
