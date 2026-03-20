package com.registro.empleados.presentation.viewmodel;

import com.registro.empleados.data.device.DeviceIdentityManager;
import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.domain.usecase.sync.SyncEmpleadosFromApiUseCase;
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
public final class BienvenidaViewModel_Factory implements Factory<BienvenidaViewModel> {
  private final Provider<AppPreferences> appPreferencesProvider;

  private final Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider;

  private final Provider<DeviceIdentityManager> deviceIdentityManagerProvider;

  public BienvenidaViewModel_Factory(Provider<AppPreferences> appPreferencesProvider,
      Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider,
      Provider<DeviceIdentityManager> deviceIdentityManagerProvider) {
    this.appPreferencesProvider = appPreferencesProvider;
    this.syncEmpleadosFromApiUseCaseProvider = syncEmpleadosFromApiUseCaseProvider;
    this.deviceIdentityManagerProvider = deviceIdentityManagerProvider;
  }

  @Override
  public BienvenidaViewModel get() {
    return newInstance(appPreferencesProvider.get(), syncEmpleadosFromApiUseCaseProvider.get(), deviceIdentityManagerProvider.get());
  }

  public static BienvenidaViewModel_Factory create(Provider<AppPreferences> appPreferencesProvider,
      Provider<SyncEmpleadosFromApiUseCase> syncEmpleadosFromApiUseCaseProvider,
      Provider<DeviceIdentityManager> deviceIdentityManagerProvider) {
    return new BienvenidaViewModel_Factory(appPreferencesProvider, syncEmpleadosFromApiUseCaseProvider, deviceIdentityManagerProvider);
  }

  public static BienvenidaViewModel newInstance(AppPreferences appPreferences,
      SyncEmpleadosFromApiUseCase syncEmpleadosFromApiUseCase,
      DeviceIdentityManager deviceIdentityManager) {
    return new BienvenidaViewModel(appPreferences, syncEmpleadosFromApiUseCase, deviceIdentityManager);
  }
}
