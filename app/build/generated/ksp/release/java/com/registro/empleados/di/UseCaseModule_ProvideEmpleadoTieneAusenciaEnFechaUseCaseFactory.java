package com.registro.empleados.di;

import com.registro.empleados.domain.repository.AusenciaRepository;
import com.registro.empleados.domain.usecase.ausencia.EmpleadoTieneAusenciaEnFechaUseCase;
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
public final class UseCaseModule_ProvideEmpleadoTieneAusenciaEnFechaUseCaseFactory implements Factory<EmpleadoTieneAusenciaEnFechaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public UseCaseModule_ProvideEmpleadoTieneAusenciaEnFechaUseCaseFactory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public EmpleadoTieneAusenciaEnFechaUseCase get() {
    return provideEmpleadoTieneAusenciaEnFechaUseCase(ausenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideEmpleadoTieneAusenciaEnFechaUseCaseFactory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new UseCaseModule_ProvideEmpleadoTieneAusenciaEnFechaUseCaseFactory(ausenciaRepositoryProvider);
  }

  public static EmpleadoTieneAusenciaEnFechaUseCase provideEmpleadoTieneAusenciaEnFechaUseCase(
      AusenciaRepository ausenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideEmpleadoTieneAusenciaEnFechaUseCase(ausenciaRepository));
  }
}
