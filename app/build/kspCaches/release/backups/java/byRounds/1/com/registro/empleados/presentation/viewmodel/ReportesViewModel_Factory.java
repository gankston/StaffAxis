package com.registro.empleados.presentation.viewmodel;

import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.domain.usecase.asistencia.GetRegistrosByRangoUseCase;
import com.registro.empleados.domain.usecase.database.LimpiarBaseDatosUseCase;
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase;
import com.registro.empleados.domain.usecase.export.ExportarRegistrosAsistenciaUseCase;
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
public final class ReportesViewModel_Factory implements Factory<ReportesViewModel> {
  private final Provider<GetRegistrosByRangoUseCase> getRegistrosByRangoUseCaseProvider;

  private final Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider;

  private final Provider<ExportarRegistrosAsistenciaUseCase> exportarRegistrosAsistenciaUseCaseProvider;

  private final Provider<LimpiarBaseDatosUseCase> limpiarBaseDatosUseCaseProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  public ReportesViewModel_Factory(
      Provider<GetRegistrosByRangoUseCase> getRegistrosByRangoUseCaseProvider,
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<ExportarRegistrosAsistenciaUseCase> exportarRegistrosAsistenciaUseCaseProvider,
      Provider<LimpiarBaseDatosUseCase> limpiarBaseDatosUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    this.getRegistrosByRangoUseCaseProvider = getRegistrosByRangoUseCaseProvider;
    this.getAllEmpleadosActivosUseCaseProvider = getAllEmpleadosActivosUseCaseProvider;
    this.exportarRegistrosAsistenciaUseCaseProvider = exportarRegistrosAsistenciaUseCaseProvider;
    this.limpiarBaseDatosUseCaseProvider = limpiarBaseDatosUseCaseProvider;
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public ReportesViewModel get() {
    return newInstance(getRegistrosByRangoUseCaseProvider.get(), getAllEmpleadosActivosUseCaseProvider.get(), exportarRegistrosAsistenciaUseCaseProvider.get(), limpiarBaseDatosUseCaseProvider.get(), appPreferencesProvider.get());
  }

  public static ReportesViewModel_Factory create(
      Provider<GetRegistrosByRangoUseCase> getRegistrosByRangoUseCaseProvider,
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<ExportarRegistrosAsistenciaUseCase> exportarRegistrosAsistenciaUseCaseProvider,
      Provider<LimpiarBaseDatosUseCase> limpiarBaseDatosUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    return new ReportesViewModel_Factory(getRegistrosByRangoUseCaseProvider, getAllEmpleadosActivosUseCaseProvider, exportarRegistrosAsistenciaUseCaseProvider, limpiarBaseDatosUseCaseProvider, appPreferencesProvider);
  }

  public static ReportesViewModel newInstance(GetRegistrosByRangoUseCase getRegistrosByRangoUseCase,
      GetAllEmpleadosActivosUseCase getAllEmpleadosActivosUseCase,
      ExportarRegistrosAsistenciaUseCase exportarRegistrosAsistenciaUseCase,
      LimpiarBaseDatosUseCase limpiarBaseDatosUseCase, AppPreferences appPreferences) {
    return new ReportesViewModel(getRegistrosByRangoUseCase, getAllEmpleadosActivosUseCase, exportarRegistrosAsistenciaUseCase, limpiarBaseDatosUseCase, appPreferences);
  }
}
