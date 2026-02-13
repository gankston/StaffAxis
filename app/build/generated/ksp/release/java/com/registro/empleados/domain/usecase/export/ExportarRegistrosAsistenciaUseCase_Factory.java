package com.registro.empleados.domain.usecase.export;

import com.registro.empleados.data.export.CsvExportService;
import com.registro.empleados.data.export.ExcelExportService;
import com.registro.empleados.domain.repository.AusenciaRepository;
import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class ExportarRegistrosAsistenciaUseCase_Factory implements Factory<ExportarRegistrosAsistenciaUseCase> {
  private final Provider<ExcelExportService> excelExportServiceProvider;

  private final Provider<CsvExportService> csvExportServiceProvider;

  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public ExportarRegistrosAsistenciaUseCase_Factory(
      Provider<ExcelExportService> excelExportServiceProvider,
      Provider<CsvExportService> csvExportServiceProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider,
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.excelExportServiceProvider = excelExportServiceProvider;
    this.csvExportServiceProvider = csvExportServiceProvider;
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public ExportarRegistrosAsistenciaUseCase get() {
    return newInstance(excelExportServiceProvider.get(), csvExportServiceProvider.get(), empleadoRepositoryProvider.get(), registroAsistenciaRepositoryProvider.get(), ausenciaRepositoryProvider.get());
  }

  public static ExportarRegistrosAsistenciaUseCase_Factory create(
      Provider<ExcelExportService> excelExportServiceProvider,
      Provider<CsvExportService> csvExportServiceProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider,
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new ExportarRegistrosAsistenciaUseCase_Factory(excelExportServiceProvider, csvExportServiceProvider, empleadoRepositoryProvider, registroAsistenciaRepositoryProvider, ausenciaRepositoryProvider);
  }

  public static ExportarRegistrosAsistenciaUseCase newInstance(
      ExcelExportService excelExportService, CsvExportService csvExportService,
      EmpleadoRepository empleadoRepository,
      RegistroAsistenciaRepository registroAsistenciaRepository,
      AusenciaRepository ausenciaRepository) {
    return new ExportarRegistrosAsistenciaUseCase(excelExportService, csvExportService, empleadoRepository, registroAsistenciaRepository, ausenciaRepository);
  }
}
