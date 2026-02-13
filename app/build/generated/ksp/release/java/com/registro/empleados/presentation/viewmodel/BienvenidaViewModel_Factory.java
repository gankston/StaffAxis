package com.registro.empleados.presentation.viewmodel;

import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.domain.usecase.empleado.CargarEmpleadosPorSectorUseCase;
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

  private final Provider<CargarEmpleadosPorSectorUseCase> cargarEmpleadosPorSectorUseCaseProvider;

  public BienvenidaViewModel_Factory(Provider<AppPreferences> appPreferencesProvider,
      Provider<CargarEmpleadosPorSectorUseCase> cargarEmpleadosPorSectorUseCaseProvider) {
    this.appPreferencesProvider = appPreferencesProvider;
    this.cargarEmpleadosPorSectorUseCaseProvider = cargarEmpleadosPorSectorUseCaseProvider;
  }

  @Override
  public BienvenidaViewModel get() {
    return newInstance(appPreferencesProvider.get(), cargarEmpleadosPorSectorUseCaseProvider.get());
  }

  public static BienvenidaViewModel_Factory create(Provider<AppPreferences> appPreferencesProvider,
      Provider<CargarEmpleadosPorSectorUseCase> cargarEmpleadosPorSectorUseCaseProvider) {
    return new BienvenidaViewModel_Factory(appPreferencesProvider, cargarEmpleadosPorSectorUseCaseProvider);
  }

  public static BienvenidaViewModel newInstance(AppPreferences appPreferences,
      CargarEmpleadosPorSectorUseCase cargarEmpleadosPorSectorUseCase) {
    return new BienvenidaViewModel(appPreferences, cargarEmpleadosPorSectorUseCase);
  }
}
