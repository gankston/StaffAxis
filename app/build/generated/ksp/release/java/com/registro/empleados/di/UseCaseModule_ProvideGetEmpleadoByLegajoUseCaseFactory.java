package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase;
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
public final class UseCaseModule_ProvideGetEmpleadoByLegajoUseCaseFactory implements Factory<GetEmpleadoByLegajoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideGetEmpleadoByLegajoUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public GetEmpleadoByLegajoUseCase get() {
    return provideGetEmpleadoByLegajoUseCase(empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetEmpleadoByLegajoUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideGetEmpleadoByLegajoUseCaseFactory(empleadoRepositoryProvider);
  }

  public static GetEmpleadoByLegajoUseCase provideGetEmpleadoByLegajoUseCase(
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetEmpleadoByLegajoUseCase(empleadoRepository));
  }
}
