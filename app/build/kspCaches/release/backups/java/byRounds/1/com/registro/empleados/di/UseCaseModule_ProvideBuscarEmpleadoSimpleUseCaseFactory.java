package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.empleado.BuscarEmpleadoSimpleUseCase;
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
public final class UseCaseModule_ProvideBuscarEmpleadoSimpleUseCaseFactory implements Factory<BuscarEmpleadoSimpleUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideBuscarEmpleadoSimpleUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public BuscarEmpleadoSimpleUseCase get() {
    return provideBuscarEmpleadoSimpleUseCase(empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideBuscarEmpleadoSimpleUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideBuscarEmpleadoSimpleUseCaseFactory(empleadoRepositoryProvider);
  }

  public static BuscarEmpleadoSimpleUseCase provideBuscarEmpleadoSimpleUseCase(
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideBuscarEmpleadoSimpleUseCase(empleadoRepository));
  }
}
