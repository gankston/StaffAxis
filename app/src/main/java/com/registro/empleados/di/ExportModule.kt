package com.registro.empleados.di

import android.content.Context
import com.registro.empleados.data.export.CsvExportService
import com.registro.empleados.data.export.ExcelExportService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para los servicios de exportación.
 */
@Module
@InstallIn(SingletonComponent::class)
object ExportModule {

    /**
     * Proporciona el servicio de exportación a Excel.
     */
    @Provides
    @Singleton
    fun provideExcelExportService(@ApplicationContext context: Context): ExcelExportService {
        return ExcelExportService(context)
    }

    /**
     * Proporciona el servicio de exportación a CSV.
     */
    @Provides
    @Singleton
    fun provideCsvExportService(@ApplicationContext context: Context): CsvExportService {
        return CsvExportService(context)
    }
}
