package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase;
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
public final class UseCaseModule_ProvideGetAllEmpleadosActivosUseCaseFactory implements Factory<GetAllEmpleadosActivosUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideGetAllEmpleadosActivosUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public GetAllEmpleadosActivosUseCase get() {
    return provideGetAllEmpleadosActivosUseCase(empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetAllEmpleadosActivosUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideGetAllEmpleadosActivosUseCaseFactory(empleadoRepositoryProvider);
  }

  public static GetAllEmpleadosActivosUseCase provideGetAllEmpleadosActivosUseCase(
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetAllEmpleadosActivosUseCase(empleadoRepository));
  }
}
