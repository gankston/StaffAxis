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
public final class UpdateEstadoEmpleadoUseCase_Factory implements Factory<UpdateEstadoEmpleadoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UpdateEstadoEmpleadoUseCase_Factory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public UpdateEstadoEmpleadoUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static UpdateEstadoEmpleadoUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UpdateEstadoEmpleadoUseCase_Factory(empleadoRepositoryProvider);
  }

  public static UpdateEstadoEmpleadoUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new UpdateEstadoEmpleadoUseCase(empleadoRepository);
  }
}
