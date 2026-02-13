package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.empleado.DarDeBajaEmpleadoUseCase;
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
public final class UseCaseModule_ProvideDarDeBajaEmpleadoUseCaseFactory implements Factory<DarDeBajaEmpleadoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideDarDeBajaEmpleadoUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public DarDeBajaEmpleadoUseCase get() {
    return provideDarDeBajaEmpleadoUseCase(empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideDarDeBajaEmpleadoUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideDarDeBajaEmpleadoUseCaseFactory(empleadoRepositoryProvider);
  }

  public static DarDeBajaEmpleadoUseCase provideDarDeBajaEmpleadoUseCase(
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideDarDeBajaEmpleadoUseCase(empleadoRepository));
  }
}
