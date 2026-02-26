package com.registro.empleados.presentation.viewmodel;

import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.domain.usecase.ausencia.EmpleadoTieneAusenciaEnFechaUseCase;
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase;
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase;
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase;
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase;
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
public final class EmpleadosViewModel_Factory implements Factory<EmpleadosViewModel> {
  private final Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider;

  private final Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider;

  private final Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider;

  private final Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider;

  private final Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider;

  private final Provider<EmpleadoTieneAusenciaEnFechaUseCase> empleadoTieneAusenciaEnFechaUseCaseProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  public EmpleadosViewModel_Factory(
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider,
      Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider,
      Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider,
      Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider,
      Provider<EmpleadoTieneAusenciaEnFechaUseCase> empleadoTieneAusenciaEnFechaUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    this.getAllEmpleadosActivosUseCaseProvider = getAllEmpleadosActivosUseCaseProvider;
    this.getEmpleadoByLegajoUseCaseProvider = getEmpleadoByLegajoUseCaseProvider;
    this.insertEmpleadoUseCaseProvider = insertEmpleadoUseCaseProvider;
    this.updateEmpleadoUseCaseProvider = updateEmpleadoUseCaseProvider;
    this.tieneHorasCargadasHoyUseCaseProvider = tieneHorasCargadasHoyUseCaseProvider;
    this.empleadoTieneAusenciaEnFechaUseCaseProvider = empleadoTieneAusenciaEnFechaUseCaseProvider;
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public EmpleadosViewModel get() {
    return newInstance(getAllEmpleadosActivosUseCaseProvider.get(), getEmpleadoByLegajoUseCaseProvider.get(), insertEmpleadoUseCaseProvider.get(), updateEmpleadoUseCaseProvider.get(), tieneHorasCargadasHoyUseCaseProvider.get(), empleadoTieneAusenciaEnFechaUseCaseProvider.get(), appPreferencesProvider.get());
  }

  public static EmpleadosViewModel_Factory create(
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider,
      Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider,
      Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider,
      Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider,
      Provider<EmpleadoTieneAusenciaEnFechaUseCase> empleadoTieneAusenciaEnFechaUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    return new EmpleadosViewModel_Factory(getAllEmpleadosActivosUseCaseProvider, getEmpleadoByLegajoUseCaseProvider, insertEmpleadoUseCaseProvider, updateEmpleadoUseCaseProvider, tieneHorasCargadasHoyUseCaseProvider, empleadoTieneAusenciaEnFechaUseCaseProvider, appPreferencesProvider);
  }

  public static EmpleadosViewModel newInstance(
      GetAllEmpleadosActivosUseCase getAllEmpleadosActivosUseCase,
      GetEmpleadoByLegajoUseCase getEmpleadoByLegajoUseCase,
      InsertEmpleadoUseCase insertEmpleadoUseCase, UpdateEmpleadoUseCase updateEmpleadoUseCase,
      TieneHorasCargadasHoyUseCase tieneHorasCargadasHoyUseCase,
      EmpleadoTieneAusenciaEnFechaUseCase empleadoTieneAusenciaEnFechaUseCase,
      AppPreferences appPreferences) {
    return new EmpleadosViewModel(getAllEmpleadosActivosUseCase, getEmpleadoByLegajoUseCase, insertEmpleadoUseCase, updateEmpleadoUseCase, tieneHorasCargadasHoyUseCase, empleadoTieneAusenciaEnFechaUseCase, appPreferences);
  }
}
