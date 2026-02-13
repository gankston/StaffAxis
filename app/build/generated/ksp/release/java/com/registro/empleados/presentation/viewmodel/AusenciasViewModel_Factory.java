package com.registro.empleados.presentation.viewmodel;

import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.domain.usecase.ausencia.ActualizarAusenciaUseCase;
import com.registro.empleados.domain.usecase.ausencia.CrearAusenciaUseCase;
import com.registro.empleados.domain.usecase.ausencia.EliminarAusenciaUseCase;
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase;
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByRangoUseCase;
import com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase;
import com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase;
import com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase;
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase;
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
public final class AusenciasViewModel_Factory implements Factory<AusenciasViewModel> {
  private final Provider<GetDiasLaboralesUseCase> getDiasLaboralesUseCaseProvider;

  private final Provider<GetPeriodoActualUseCase> getPeriodoActualUseCaseProvider;

  private final Provider<GenerarDiasLaboralesBasicosUseCase> generarDiasLaboralesBasicosUseCaseProvider;

  private final Provider<GetAusenciasByRangoUseCase> getAusenciasByRangoUseCaseProvider;

  private final Provider<GetAusenciasByFechaUseCase> getAusenciasByFechaUseCaseProvider;

  private final Provider<CrearAusenciaUseCase> crearAusenciaUseCaseProvider;

  private final Provider<ActualizarAusenciaUseCase> actualizarAusenciaUseCaseProvider;

  private final Provider<EliminarAusenciaUseCase> eliminarAusenciaUseCaseProvider;

  private final Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  public AusenciasViewModel_Factory(
      Provider<GetDiasLaboralesUseCase> getDiasLaboralesUseCaseProvider,
      Provider<GetPeriodoActualUseCase> getPeriodoActualUseCaseProvider,
      Provider<GenerarDiasLaboralesBasicosUseCase> generarDiasLaboralesBasicosUseCaseProvider,
      Provider<GetAusenciasByRangoUseCase> getAusenciasByRangoUseCaseProvider,
      Provider<GetAusenciasByFechaUseCase> getAusenciasByFechaUseCaseProvider,
      Provider<CrearAusenciaUseCase> crearAusenciaUseCaseProvider,
      Provider<ActualizarAusenciaUseCase> actualizarAusenciaUseCaseProvider,
      Provider<EliminarAusenciaUseCase> eliminarAusenciaUseCaseProvider,
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    this.getDiasLaboralesUseCaseProvider = getDiasLaboralesUseCaseProvider;
    this.getPeriodoActualUseCaseProvider = getPeriodoActualUseCaseProvider;
    this.generarDiasLaboralesBasicosUseCaseProvider = generarDiasLaboralesBasicosUseCaseProvider;
    this.getAusenciasByRangoUseCaseProvider = getAusenciasByRangoUseCaseProvider;
    this.getAusenciasByFechaUseCaseProvider = getAusenciasByFechaUseCaseProvider;
    this.crearAusenciaUseCaseProvider = crearAusenciaUseCaseProvider;
    this.actualizarAusenciaUseCaseProvider = actualizarAusenciaUseCaseProvider;
    this.eliminarAusenciaUseCaseProvider = eliminarAusenciaUseCaseProvider;
    this.getAllEmpleadosActivosUseCaseProvider = getAllEmpleadosActivosUseCaseProvider;
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public AusenciasViewModel get() {
    return newInstance(getDiasLaboralesUseCaseProvider.get(), getPeriodoActualUseCaseProvider.get(), generarDiasLaboralesBasicosUseCaseProvider.get(), getAusenciasByRangoUseCaseProvider.get(), getAusenciasByFechaUseCaseProvider.get(), crearAusenciaUseCaseProvider.get(), actualizarAusenciaUseCaseProvider.get(), eliminarAusenciaUseCaseProvider.get(), getAllEmpleadosActivosUseCaseProvider.get(), appPreferencesProvider.get());
  }

  public static AusenciasViewModel_Factory create(
      Provider<GetDiasLaboralesUseCase> getDiasLaboralesUseCaseProvider,
      Provider<GetPeriodoActualUseCase> getPeriodoActualUseCaseProvider,
      Provider<GenerarDiasLaboralesBasicosUseCase> generarDiasLaboralesBasicosUseCaseProvider,
      Provider<GetAusenciasByRangoUseCase> getAusenciasByRangoUseCaseProvider,
      Provider<GetAusenciasByFechaUseCase> getAusenciasByFechaUseCaseProvider,
      Provider<CrearAusenciaUseCase> crearAusenciaUseCaseProvider,
      Provider<ActualizarAusenciaUseCase> actualizarAusenciaUseCaseProvider,
      Provider<EliminarAusenciaUseCase> eliminarAusenciaUseCaseProvider,
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    return new AusenciasViewModel_Factory(getDiasLaboralesUseCaseProvider, getPeriodoActualUseCaseProvider, generarDiasLaboralesBasicosUseCaseProvider, getAusenciasByRangoUseCaseProvider, getAusenciasByFechaUseCaseProvider, crearAusenciaUseCaseProvider, actualizarAusenciaUseCaseProvider, eliminarAusenciaUseCaseProvider, getAllEmpleadosActivosUseCaseProvider, appPreferencesProvider);
  }

  public static AusenciasViewModel newInstance(GetDiasLaboralesUseCase getDiasLaboralesUseCase,
      GetPeriodoActualUseCase getPeriodoActualUseCase,
      GenerarDiasLaboralesBasicosUseCase generarDiasLaboralesBasicosUseCase,
      GetAusenciasByRangoUseCase getAusenciasByRangoUseCase,
      GetAusenciasByFechaUseCase getAusenciasByFechaUseCase,
      CrearAusenciaUseCase crearAusenciaUseCase,
      ActualizarAusenciaUseCase actualizarAusenciaUseCase,
      EliminarAusenciaUseCase eliminarAusenciaUseCase,
      GetAllEmpleadosActivosUseCase getAllEmpleadosActivosUseCase, AppPreferences appPreferences) {
    return new AusenciasViewModel(getDiasLaboralesUseCase, getPeriodoActualUseCase, generarDiasLaboralesBasicosUseCase, getAusenciasByRangoUseCase, getAusenciasByFechaUseCase, crearAusenciaUseCase, actualizarAusenciaUseCase, eliminarAusenciaUseCase, getAllEmpleadosActivosUseCase, appPreferences);
  }
}
