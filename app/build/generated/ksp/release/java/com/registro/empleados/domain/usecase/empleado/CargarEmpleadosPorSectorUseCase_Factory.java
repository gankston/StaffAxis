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
public final class CargarEmpleadosPorSectorUseCase_Factory implements Factory<CargarEmpleadosPorSectorUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public CargarEmpleadosPorSectorUseCase_Factory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public CargarEmpleadosPorSectorUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static CargarEmpleadosPorSectorUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new CargarEmpleadosPorSectorUseCase_Factory(empleadoRepositoryProvider);
  }

  public static CargarEmpleadosPorSectorUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new CargarEmpleadosPorSectorUseCase(empleadoRepository);
  }
}
