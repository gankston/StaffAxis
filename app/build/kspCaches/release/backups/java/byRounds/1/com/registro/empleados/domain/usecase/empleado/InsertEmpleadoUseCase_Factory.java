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
public final class InsertEmpleadoUseCase_Factory implements Factory<InsertEmpleadoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public InsertEmpleadoUseCase_Factory(Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public InsertEmpleadoUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static InsertEmpleadoUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new InsertEmpleadoUseCase_Factory(empleadoRepositoryProvider);
  }

  public static InsertEmpleadoUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new InsertEmpleadoUseCase(empleadoRepository);
  }
}
