package com.registro.empleados.di

import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource
import com.registro.empleados.domain.repository.DiaLaboralRepository
import com.registro.empleados.domain.repository.EmpleadoRepository
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import com.registro.empleados.domain.repository.AusenciaRepository
import com.registro.empleados.domain.usecase.asistencia.GetRegistrosByRangoUseCase
import com.registro.empleados.domain.usecase.calendario.GetCalendarioPeriodoUseCase
import com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase
import com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase
import com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase
import com.registro.empleados.domain.usecase.empleado.BuscarEmpleadoSimpleUseCase
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase
import com.registro.empleados.domain.usecase.empleado.DarDeBajaEmpleadoUseCase
import com.registro.empleados.domain.usecase.database.LimpiarBaseDatosUseCase
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase
import com.registro.empleados.domain.usecase.empleado.CargarEmpleadosPorSectorUseCase
import com.registro.empleados.domain.usecase.ausencia.CrearAusenciaUseCase
import com.registro.empleados.domain.usecase.ausencia.EmpleadoTieneAusenciaEnFechaUseCase
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByRangoUseCase
import com.registro.empleados.domain.usecase.export.ExportarRegistrosAsistenciaUseCase
import com.registro.empleados.domain.usecase.feriados.SincronizarFeriadosUseCase
import com.registro.empleados.domain.usecase.horas.ActualizarHorasMensualesUseCase
import com.registro.empleados.domain.usecase.horas.GetHorasMensualesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para la configuración de casos de uso.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * Proporciona el caso de uso para obtener todos los empleados activos.
     */
    @Provides
    @Singleton
    fun provideGetAllEmpleadosActivosUseCase(
        empleadoRepository: EmpleadoRepository
    ): GetAllEmpleadosActivosUseCase {
        return GetAllEmpleadosActivosUseCase(empleadoRepository)
    }

    /**
     * Proporciona el caso de uso para obtener un empleado por legajo.
     */
    @Provides
    @Singleton
    fun provideGetEmpleadoByLegajoUseCase(
        empleadoRepository: EmpleadoRepository
    ): GetEmpleadoByLegajoUseCase {
        return GetEmpleadoByLegajoUseCase(empleadoRepository)
    }

    /**
     * Proporciona el caso de uso para insertar un empleado.
     */
    @Provides
    @Singleton
    fun provideInsertEmpleadoUseCase(
        empleadoRepository: EmpleadoRepository
    ): InsertEmpleadoUseCase {
        return InsertEmpleadoUseCase(empleadoRepository)
    }


    /**
     * Proporciona el caso de uso para obtener registros por rango.
     */
    @Provides
    @Singleton
    fun provideGetRegistrosByRangoUseCase(
        registroAsistenciaRepository: RegistroAsistenciaRepository
    ): GetRegistrosByRangoUseCase {
        return GetRegistrosByRangoUseCase(registroAsistenciaRepository)
    }

    /**
     * Proporciona el caso de uso para obtener el período actual.
     */
    @Provides
    @Singleton
    fun provideGetPeriodoActualUseCase(): GetPeriodoActualUseCase {
        return GetPeriodoActualUseCase()
    }

    /**
     * Proporciona el caso de uso para obtener días laborales.
     */
    @Provides
    @Singleton
    fun provideGetDiasLaboralesUseCase(
        diaLaboralRepository: DiaLaboralRepository
    ): GetDiasLaboralesUseCase {
        return GetDiasLaboralesUseCase(diaLaboralRepository)
    }

    /**
     * Proporciona el caso de uso para obtener datos completos del calendario.
     */
    @Provides
    @Singleton
    fun provideGetCalendarioPeriodoUseCase(
        diaLaboralRepository: DiaLaboralRepository,
        registroAsistenciaRepository: RegistroAsistenciaRepository
    ): GetCalendarioPeriodoUseCase {
        return GetCalendarioPeriodoUseCase(diaLaboralRepository, registroAsistenciaRepository)
    }

    /**
     * Proporciona el caso de uso para generar días laborales básicos.
     */
    @Provides
    @Singleton
    fun provideGenerarDiasLaboralesBasicosUseCase(
        diaLaboralRepository: DiaLaboralRepository
    ): GenerarDiasLaboralesBasicosUseCase {
        return GenerarDiasLaboralesBasicosUseCase(diaLaboralRepository)
    }

    /**
     * Proporciona el caso de uso para sincronizar feriados.
     */
    @Provides
    @Singleton
    fun provideSincronizarFeriadosUseCase(
        feriadosRemoteDataSource: FeriadosRemoteDataSource,
        diaLaboralRepository: DiaLaboralRepository
    ): SincronizarFeriadosUseCase {
        return SincronizarFeriadosUseCase(feriadosRemoteDataSource, diaLaboralRepository)
    }

    /**
     * Proporciona el caso de uso para exportar registros de asistencia.
     */
    @Provides
    @Singleton
    fun provideExportarRegistrosAsistenciaUseCase(
        excelExportService: com.registro.empleados.data.export.ExcelExportService,
        csvExportService: com.registro.empleados.data.export.CsvExportService,
        empleadoRepository: EmpleadoRepository,
        registroAsistenciaRepository: RegistroAsistenciaRepository,
        ausenciaRepository: AusenciaRepository
    ): ExportarRegistrosAsistenciaUseCase {
        return ExportarRegistrosAsistenciaUseCase(
            excelExportService, csvExportService, empleadoRepository, registroAsistenciaRepository, ausenciaRepository
        )
    }


    /**
     * Proporciona el caso de uso para actualizar horas mensuales.
     */
    @Provides
    @Singleton
    fun provideActualizarHorasMensualesUseCase(
        horasRepository: HorasEmpleadoMesRepository,
        registroRepository: RegistroAsistenciaRepository,
        empleadoRepository: EmpleadoRepository
    ): ActualizarHorasMensualesUseCase {
        return ActualizarHorasMensualesUseCase(horasRepository, registroRepository, empleadoRepository)
    }

    /**
     * Proporciona el caso de uso para obtener horas mensuales.
     */
    @Provides
    @Singleton
    fun provideGetHorasMensualesUseCase(
        horasRepository: HorasEmpleadoMesRepository
    ): GetHorasMensualesUseCase {
        return GetHorasMensualesUseCase(horasRepository)
    }

    /**
     * Proporciona el caso de uso para buscar empleados de forma simple.
     */
    @Provides
    @Singleton
    fun provideBuscarEmpleadoSimpleUseCase(
        empleadoRepository: EmpleadoRepository
    ): BuscarEmpleadoSimpleUseCase {
        return BuscarEmpleadoSimpleUseCase(empleadoRepository)
    }

    /**
     * Proporciona el caso de uso para actualizar empleados.
     */
    @Provides
    @Singleton
    fun provideUpdateEmpleadoUseCase(
        empleadoRepository: EmpleadoRepository
    ): UpdateEmpleadoUseCase {
        return UpdateEmpleadoUseCase(empleadoRepository)
    }

    /**
     * Proporciona el caso de uso para dar de baja empleados.
     */
    @Provides
    @Singleton
    fun provideDarDeBajaEmpleadoUseCase(
        empleadoRepository: EmpleadoRepository
    ): DarDeBajaEmpleadoUseCase {
        return DarDeBajaEmpleadoUseCase(empleadoRepository)
    }

    /**
     * Proporciona el caso de uso para limpiar la base de datos.
     */
    @Provides
    @Singleton
    fun provideLimpiarBaseDatosUseCase(
        empleadoRepository: EmpleadoRepository,
        registroAsistenciaRepository: RegistroAsistenciaRepository
    ): LimpiarBaseDatosUseCase {
        return LimpiarBaseDatosUseCase(empleadoRepository, registroAsistenciaRepository)
    }

    // Eliminados: use cases de inserción de datos de prueba

    /**
     * Proporciona el caso de uso para verificar si un empleado tiene horas cargadas hoy.
     */
    @Provides
    @Singleton
    fun provideTieneHorasCargadasHoyUseCase(
        registroAsistenciaRepository: RegistroAsistenciaRepository
    ): TieneHorasCargadasHoyUseCase {
        return TieneHorasCargadasHoyUseCase(registroAsistenciaRepository)
    }

    /**
     * Proporciona el caso de uso para cargar empleados por sector.
     */
    @Provides
    @Singleton
    fun provideCargarEmpleadosPorSectorUseCase(
        empleadoRepository: EmpleadoRepository
    ): CargarEmpleadosPorSectorUseCase {
        return CargarEmpleadosPorSectorUseCase(empleadoRepository)
    }

    /**
     * Proporciona el caso de uso para crear ausencias.
     */
    @Provides
    @Singleton
    fun provideCrearAusenciaUseCase(
        ausenciaRepository: AusenciaRepository
    ): CrearAusenciaUseCase {
        return CrearAusenciaUseCase(ausenciaRepository)
    }

    /**
     * Proporciona el caso de uso para verificar si un empleado tiene ausencia en una fecha.
     */
    @Provides
    @Singleton
    fun provideEmpleadoTieneAusenciaEnFechaUseCase(
        ausenciaRepository: AusenciaRepository
    ): EmpleadoTieneAusenciaEnFechaUseCase {
        return EmpleadoTieneAusenciaEnFechaUseCase(ausenciaRepository)
    }

    /**
     * Proporciona el caso de uso para obtener ausencias por fecha.
     */
    @Provides
    @Singleton
    fun provideGetAusenciasByFechaUseCase(
        ausenciaRepository: AusenciaRepository
    ): GetAusenciasByFechaUseCase {
        return GetAusenciasByFechaUseCase(ausenciaRepository)
    }

    /**
     * Proporciona el caso de uso para obtener ausencias por rango de fechas.
     */
    @Provides
    @Singleton
    fun provideGetAusenciasByRangoUseCase(
        ausenciaRepository: AusenciaRepository
    ): GetAusenciasByRangoUseCase {
        return GetAusenciasByRangoUseCase(ausenciaRepository)
    }

}
