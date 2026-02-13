package com.registro.empleados.di

import com.registro.empleados.data.repository.DiaLaboralRepositoryImpl
import com.registro.empleados.data.repository.EmpleadoRepositoryImpl
import com.registro.empleados.data.repository.HorasEmpleadoMesRepositoryImpl
import com.registro.empleados.data.repository.RegistroAsistenciaRepositoryImpl
import com.registro.empleados.data.repository.AusenciaRepositoryImpl
import com.registro.empleados.domain.repository.DiaLaboralRepository
import com.registro.empleados.domain.repository.EmpleadoRepository
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import com.registro.empleados.domain.repository.AusenciaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para la configuración de repositorios.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Proporciona la implementación del repositorio de empleados.
     */
    @Binds
    @Singleton
    abstract fun bindEmpleadoRepository(
        empleadoRepositoryImpl: EmpleadoRepositoryImpl
    ): EmpleadoRepository
    
    /**
     * Proporciona la implementación del repositorio de registros de asistencia.
     */
    @Binds
    @Singleton
    abstract fun bindRegistroAsistenciaRepository(
        registroAsistenciaRepositoryImpl: RegistroAsistenciaRepositoryImpl
    ): RegistroAsistenciaRepository
    
    /**
     * Proporciona la implementación del repositorio de días laborales.
     */
    @Binds
    @Singleton
    abstract fun bindDiaLaboralRepository(
        diaLaboralRepositoryImpl: DiaLaboralRepositoryImpl
    ): DiaLaboralRepository
    
    /**
     * Proporciona la implementación del repositorio de horas mensuales.
     */
    @Binds
    @Singleton
    abstract fun bindHorasEmpleadoMesRepository(
        horasEmpleadoMesRepositoryImpl: HorasEmpleadoMesRepositoryImpl
    ): HorasEmpleadoMesRepository
    
    /**
     * Proporciona la implementación del repositorio de ausencias.
     */
    @Binds
    @Singleton
    abstract fun bindAusenciaRepository(
        ausenciaRepositoryImpl: AusenciaRepositoryImpl
    ): AusenciaRepository
}
