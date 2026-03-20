package com.registro.empleados.presentation.viewmodel;

import android.content.Context;
import com.registro.empleados.data.device.DeviceIdentityManager;
import com.registro.empleados.data.local.dao.EmpleadoDao;
import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.data.remote.api.AusenciasApiService;
import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.LimpiarTodosLosRegistrosUseCase;
import com.registro.empleados.domain.usecase.database.CorregirEmpleadosDBDirectoUseCase;
import com.registro.empleados.domain.usecase.empleado.BuscarEmpleadoSimpleUseCase;
import com.registro.empleados.domain.usecase.empleado.DarDeBajaEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase;
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase;
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase;
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.UpdateEstadoEmpleadoUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<BuscarEmpleadoSimpleUseCase> buscarEmpleadoSimpleUseCaseProvider;

  private final Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider;

  private final Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider;

  private final Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider;

  private final Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider;

  private final Provider<LimpiarTodosLosRegistrosUseCase> limpiarTodosLosRegistrosUseCaseProvider;

  private final Provider<CorregirEmpleadosDBDirectoUseCase> corregirEmpleadosDBDirectoUseCaseProvider;

  private final Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider;

  private final Provider<DarDeBajaEmpleadoUseCase> darDeBajaEmpleadoUseCaseProvider;

  private final Provider<UpdateEstadoEmpleadoUseCase> updateEstadoEmpleadoUseCaseProvider;

  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  private final Provider<HorasEmpleadoMesRepository> horasEmpleadoMesRepositoryProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  private final Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider;

  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  private final Provider<EmpleadoDao> empleadoDaoProvider;

  private final Provider<AusenciasApiService> ausenciasApiServiceProvider;

  private final Provider<DeviceIdentityManager> deviceIdentityManagerProvider;

  private final Provider<Context> appContextProvider;

  public DashboardViewModel_Factory(
      Provider<BuscarEmpleadoSimpleUseCase> buscarEmpleadoSimpleUseCaseProvider,
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider,
      Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider,
      Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider,
      Provider<LimpiarTodosLosRegistrosUseCase> limpiarTodosLosRegistrosUseCaseProvider,
      Provider<CorregirEmpleadosDBDirectoUseCase> corregirEmpleadosDBDirectoUseCaseProvider,
      Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider,
      Provider<DarDeBajaEmpleadoUseCase> darDeBajaEmpleadoUseCaseProvider,
      Provider<UpdateEstadoEmpleadoUseCase> updateEstadoEmpleadoUseCaseProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider,
      Provider<HorasEmpleadoMesRepository> horasEmpleadoMesRepositoryProvider,
      Provider<AppPreferences> appPreferencesProvider,
      Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<EmpleadoDao> empleadoDaoProvider,
      Provider<AusenciasApiService> ausenciasApiServiceProvider,
      Provider<DeviceIdentityManager> deviceIdentityManagerProvider,
      Provider<Context> appContextProvider) {
    this.buscarEmpleadoSimpleUseCaseProvider = buscarEmpleadoSimpleUseCaseProvider;
    this.getAllEmpleadosActivosUseCaseProvider = getAllEmpleadosActivosUseCaseProvider;
    this.getEmpleadoByLegajoUseCaseProvider = getEmpleadoByLegajoUseCaseProvider;
    this.insertEmpleadoUseCaseProvider = insertEmpleadoUseCaseProvider;
    this.tieneHorasCargadasHoyUseCaseProvider = tieneHorasCargadasHoyUseCaseProvider;
    this.limpiarTodosLosRegistrosUseCaseProvider = limpiarTodosLosRegistrosUseCaseProvider;
    this.corregirEmpleadosDBDirectoUseCaseProvider = corregirEmpleadosDBDirectoUseCaseProvider;
    this.updateEmpleadoUseCaseProvider = updateEmpleadoUseCaseProvider;
    this.darDeBajaEmpleadoUseCaseProvider = darDeBajaEmpleadoUseCaseProvider;
    this.updateEstadoEmpleadoUseCaseProvider = updateEstadoEmpleadoUseCaseProvider;
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
    this.horasEmpleadoMesRepositoryProvider = horasEmpleadoMesRepositoryProvider;
    this.appPreferencesProvider = appPreferencesProvider;
    this.syncEmpleadosFromApiUseCaseProvider = syncEmpleadosFromApiUseCaseProvider;
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
    this.empleadoDaoProvider = empleadoDaoProvider;
    this.ausenciasApiServiceProvider = ausenciasApiServiceProvider;
    this.deviceIdentityManagerProvider = deviceIdentityManagerProvider;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(buscarEmpleadoSimpleUseCaseProvider.get(), getAllEmpleadosActivosUseCaseProvider.get(), getEmpleadoByLegajoUseCaseProvider.get(), insertEmpleadoUseCaseProvider.get(), tieneHorasCargadasHoyUseCaseProvider.get(), limpiarTodosLosRegistrosUseCaseProvider.get(), corregirEmpleadosDBDirectoUseCaseProvider.get(), updateEmpleadoUseCaseProvider.get(), darDeBajaEmpleadoUseCaseProvider.get(), updateEstadoEmpleadoUseCaseProvider.get(), registroAsistenciaRepositoryProvider.get(), horasEmpleadoMesRepositoryProvider.get(), appPreferencesProvider.get(), syncEmpleadosFromApiUseCaseProvider.get(), empleadoRepositoryProvider.get(), empleadoDaoProvider.get(), ausenciasApiServiceProvider.get(), deviceIdentityManagerProvider.get(), appContextProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<BuscarEmpleadoSimpleUseCase> buscarEmpleadoSimpleUseCaseProvider,
      Provider<GetAllEmpleadosActivosUseCase> getAllEmpleadosActivosUseCaseProvider,
      Provider<GetEmpleadoByLegajoUseCase> getEmpleadoByLegajoUseCaseProvider,
      Provider<InsertEmpleadoUseCase> insertEmpleadoUseCaseProvider,
      Provider<TieneHorasCargadasHoyUseCase> tieneHorasCargadasHoyUseCaseProvider,
      Provider<LimpiarTodosLosRegistrosUseCase> limpiarTodosLosRegistrosUseCaseProvider,
      Provider<CorregirEmpleadosDBDirectoUseCase> corregirEmpleadosDBDirectoUseCaseProvider,
      Provider<UpdateEmpleadoUseCase> updateEmpleadoUseCaseProvider,
      Provider<DarDeBajaEmpleadoUseCase> darDeBajaEmpleadoUseCaseProvider,
      Provider<UpdateEstadoEmpleadoUseCase> updateEstadoEmpleadoUseCaseProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider,
      Provider<HorasEmpleadoMesRepository> horasEmpleadoMesRepositoryProvider,
      Provider<AppPreferences> appPreferencesProvider,
      Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<EmpleadoDao> empleadoDaoProvider,
      Provider<AusenciasApiService> ausenciasApiServiceProvider,
      Provider<DeviceIdentityManager> deviceIdentityManagerProvider,
      Provider<Context> appContextProvider) {
    return new DashboardViewModel_Factory(buscarEmpleadoSimpleUseCaseProvider, getAllEmpleadosActivosUseCaseProvider, getEmpleadoByLegajoUseCaseProvider, insertEmpleadoUseCaseProvider, tieneHorasCargadasHoyUseCaseProvider, limpiarTodosLosRegistrosUseCaseProvider, corregirEmpleadosDBDirectoUseCaseProvider, updateEmpleadoUseCaseProvider, darDeBajaEmpleadoUseCaseProvider, updateEstadoEmpleadoUseCaseProvider, registroAsistenciaRepositoryProvider, horasEmpleadoMesRepositoryProvider, appPreferencesProvider, syncEmpleadosFromApiUseCaseProvider, empleadoRepositoryProvider, empleadoDaoProvider, ausenciasApiServiceProvider, deviceIdentityManagerProvider, appContextProvider);
  }

  public static DashboardViewModel newInstance(
      BuscarEmpleadoSimpleUseCase buscarEmpleadoSimpleUseCase,
      GetAllEmpleadosActivosUseCase getAllEmpleadosActivosUseCase,
      GetEmpleadoByLegajoUseCase getEmpleadoByLegajoUseCase,
      InsertEmpleadoUseCase insertEmpleadoUseCase,
      TieneHorasCargadasHoyUseCase tieneHorasCargadasHoyUseCase,
      LimpiarTodosLosRegistrosUseCase limpiarTodosLosRegistrosUseCase,
      CorregirEmpleadosDBDirectoUseCase corregirEmpleadosDBDirectoUseCase,
      UpdateEmpleadoUseCase updateEmpleadoUseCase,
      DarDeBajaEmpleadoUseCase darDeBajaEmpleadoUseCase,
      UpdateEstadoEmpleadoUseCase updateEstadoEmpleadoUseCase,
      RegistroAsistenciaRepository registroAsistenciaRepository,
      HorasEmpleadoMesRepository horasEmpleadoMesRepository, AppPreferences appPreferences,
      SyncEmpleadosFromApiUseCase syncEmpleadosFromApiUseCase,
      EmpleadoRepository empleadoRepository, EmpleadoDao empleadoDao,
      AusenciasApiService ausenciasApiService, DeviceIdentityManager deviceIdentityManager,
      Context appContext) {
    return new DashboardViewModel(buscarEmpleadoSimpleUseCase, getAllEmpleadosActivosUseCase, getEmpleadoByLegajoUseCase, insertEmpleadoUseCase, tieneHorasCargadasHoyUseCase, limpiarTodosLosRegistrosUseCase, corregirEmpleadosDBDirectoUseCase, updateEmpleadoUseCase, darDeBajaEmpleadoUseCase, updateEstadoEmpleadoUseCase, registroAsistenciaRepository, horasEmpleadoMesRepository, appPreferences, syncEmpleadosFromApiUseCase, empleadoRepository, empleadoDao, ausenciasApiService, deviceIdentityManager, appContext);
  }
}
