package com.registro.empleados.domain.usecase.empleado;

import com.registro.empleados.domain.repository.EmpleadoRepository;
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
public final class GetAllEmpleadosActivosUseCase_Factory implements Factory<GetAllEmpleadosActivosUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public GetAllEmpleadosActivosUseCase_Factory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public GetAllEmpleadosActivosUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static GetAllEmpleadosActivosUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new GetAllEmpleadosActivosUseCase_Factory(empleadoRepositoryProvider);
  }

  public static GetAllEmpleadosActivosUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new GetAllEmpleadosActivosUseCase(empleadoRepository);
  }
}
