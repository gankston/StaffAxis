package com.registro.empleados.di;

import com.registro.empleados.data.export.CsvExportService;
import com.registro.empleados.data.export.ExcelExportService;
import com.registro.empleados.domain.repository.AusenciaRepository;
import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.export.ExportarRegistrosAsistenciaUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class UseCaseModule_ProvideExportarRegistrosAsistenciaUseCaseFactory implements Factory<ExportarRegistrosAsistenciaUseCase> {
  private final Provider<ExcelExportService> excelExportServiceProvider;

  private final Provider<CsvExportService> csvExportServiceProvider;

  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public UseCaseModule_ProvideExportarRegistrosAsistenciaUseCaseFactory(
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
    return provideExportarRegistrosAsistenciaUseCase(excelExportServiceProvider.get(), csvExportServiceProvider.get(), empleadoRepositoryProvider.get(), registroAsistenciaRepositoryProvider.get(), ausenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideExportarRegistrosAsistenciaUseCaseFactory create(
      Provider<ExcelExportService> excelExportServiceProvider,
      Provider<CsvExportService> csvExportServiceProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider,
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new UseCaseModule_ProvideExportarRegistrosAsistenciaUseCaseFactory(excelExportServiceProvider, csvExportServiceProvider, empleadoRepositoryProvider, registroAsistenciaRepositoryProvider, ausenciaRepositoryProvider);
  }

  public static ExportarRegistrosAsistenciaUseCase provideExportarRegistrosAsistenciaUseCase(
      ExcelExportService excelExportService, CsvExportService csvExportService,
      EmpleadoRepository empleadoRepository,
      RegistroAsistenciaRepository registroAsistenciaRepository,
      AusenciaRepository ausenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideExportarRegistrosAsistenciaUseCase(excelExportService, csvExportService, empleadoRepository, registroAsistenciaRepository, ausenciaRepository));
  }
}
