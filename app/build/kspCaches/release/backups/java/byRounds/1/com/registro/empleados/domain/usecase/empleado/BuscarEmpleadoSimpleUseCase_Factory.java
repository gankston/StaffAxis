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
public final class BuscarEmpleadoSimpleUseCase_Factory implements Factory<BuscarEmpleadoSimpleUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public BuscarEmpleadoSimpleUseCase_Factory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public BuscarEmpleadoSimpleUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static BuscarEmpleadoSimpleUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new BuscarEmpleadoSimpleUseCase_Factory(empleadoRepositoryProvider);
  }

  public static BuscarEmpleadoSimpleUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new BuscarEmpleadoSimpleUseCase(empleadoRepository);
  }
}
