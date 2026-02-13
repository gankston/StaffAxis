package com.registro.empleados.domain.usecase.ausencia;

import com.registro.empleados.domain.repository.AusenciaRepository;
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
public final class EmpleadoTieneAusenciaEnFechaUseCase_Factory implements Factory<EmpleadoTieneAusenciaEnFechaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public EmpleadoTieneAusenciaEnFechaUseCase_Factory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public EmpleadoTieneAusenciaEnFechaUseCase get() {
    return newInstance(ausenciaRepositoryProvider.get());
  }

  public static EmpleadoTieneAusenciaEnFechaUseCase_Factory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new EmpleadoTieneAusenciaEnFechaUseCase_Factory(ausenciaRepositoryProvider);
  }

  public static EmpleadoTieneAusenciaEnFechaUseCase newInstance(
      AusenciaRepository ausenciaRepository) {
    return new EmpleadoTieneAusenciaEnFechaUseCase(ausenciaRepository);
  }
}
