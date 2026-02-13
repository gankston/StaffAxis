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
public final class BuscarEmpleadosUseCase_Factory implements Factory<BuscarEmpleadosUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public BuscarEmpleadosUseCase_Factory(Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public BuscarEmpleadosUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static BuscarEmpleadosUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new BuscarEmpleadosUseCase_Factory(empleadoRepositoryProvider);
  }

  public static BuscarEmpleadosUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new BuscarEmpleadosUseCase(empleadoRepository);
  }
}
