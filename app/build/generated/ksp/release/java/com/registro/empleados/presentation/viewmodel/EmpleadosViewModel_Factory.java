package com.registro.empleados.presentation.viewmodel;

import android.content.Context;
import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase;
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase;
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase;
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase;
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase;
import com.registro.empleados.domain.usecase.sync.SyncEmpleadosFromApiUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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

  private final Provider<GetAusenciasByFechaUseCase> getAusenciasByFechaUseCaseProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  private final Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider;

  private final Provider<Context> appContextProvider;

  public EmpleadosViewModel_Factory(
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider,
      Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider,
      Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider,
      Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider,
      Provider<GetAusenciasByFechaUseCase> getAusenciasByFechaUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider,
      Provider<Context> appContextProvider) {
    this.getAllEmpleadosActivosUseCaseProvider = getAllEmpleadosActivosUseCaseProvider;
    this.getEmpleadoByLegajoUseCaseProvider = getEmpleadoByLegajoUseCaseProvider;
    this.insertEmpleadoUseCaseProvider = insertEmpleadoUseCaseProvider;
    this.updateEmpleadoUseCaseProvider = updateEmpleadoUseCaseProvider;
    this.tieneHorasCargadasHoyUseCaseProvider = tieneHorasCargadasHoyUseCaseProvider;
    this.getAusenciasByFechaUseCaseProvider = getAusenciasByFechaUseCaseProvider;
    this.appPreferencesProvider = appPreferencesProvider;
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
    this.syncEmpleadosFromApiUseCaseProvider = syncEmpleadosFromApiUseCaseProvider;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public EmpleadosViewModel get() {
    return newInstance(getAllEmpleadosActivosUseCaseProvider.get(), getEmpleadoByLegajoUseCaseProvider.get(), insertEmpleadoUseCaseProvider.get(), updateEmpleadoUseCaseProvider.get(), tieneHorasCargadasHoyUseCaseProvider.get(), getAusenciasByFechaUseCaseProvider.get(), appPreferencesProvider.get(), empleadoRepositoryProvider.get(), syncEmpleadosFromApiUseCaseProvider.get(), appContextProvider.get());
  }

  public static EmpleadosViewModel_Factory create(
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider,
      Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider,
      Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider,
      Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider,
      Provider<GetAusenciasByFechaUseCase> getAusenciasByFechaUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider,
      Provider<Context> appContextProvider) {
    return new EmpleadosViewModel_Factory(getAllEmpleadosActivosUseCaseProvider, getEmpleadoByLegajoUseCaseProvider, insertEmpleadoUseCaseProvider, updateEmpleadoUseCaseProvider, tieneHorasCargadasHoyUseCaseProvider, getAusenciasByFechaUseCaseProvider, appPreferencesProvider, empleadoRepositoryProvider, syncEmpleadosFromApiUseCaseProvider, appContextProvider);
  }

  public static EmpleadosViewModel newInstance(
      GetAllEmpleadosActivosUseCase getAllEmpleadosActivosUseCase,
      GetEmpleadoByLegajoUseCase getEmpleadoByLegajoUseCase,
      InsertEmpleadoUseCase insertEmpleadoUseCase, UpdateEmpleadoUseCase updateEmpleadoUseCase,
      TieneHorasCargadasHoyUseCase tieneHorasCargadasHoyUseCase,
      GetAusenciasByFechaUseCase getAusenciasByFechaUseCase, AppPreferences appPreferences,
      EmpleadoRepository empleadoRepository,
      SyncEmpleadosFromApiUseCase syncEmpleadosFromApiUseCase, Context appContext) {
    return new EmpleadosViewModel(getAllEmpleadosActivosUseCase, getEmpleadoByLegajoUseCase, insertEmpleadoUseCase, updateEmpleadoUseCase, tieneHorasCargadasHoyUseCase, getAusenciasByFechaUseCase, appPreferences, empleadoRepository, syncEmpleadosFromApiUseCase, appContext);
  }
}
