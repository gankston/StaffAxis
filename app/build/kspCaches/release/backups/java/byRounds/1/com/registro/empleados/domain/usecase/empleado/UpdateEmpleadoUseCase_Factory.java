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
public final class UpdateEmpleadoUseCase_Factory implements Factory<UpdateEmpleadoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UpdateEmpleadoUseCase_Factory(Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public UpdateEmpleadoUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static UpdateEmpleadoUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UpdateEmpleadoUseCase_Factory(empleadoRepositoryProvider);
  }

  public static UpdateEmpleadoUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new UpdateEmpleadoUseCase(empleadoRepository);
  }
}
