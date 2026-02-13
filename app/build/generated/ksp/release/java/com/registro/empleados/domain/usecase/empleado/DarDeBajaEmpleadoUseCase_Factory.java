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
public final class DarDeBajaEmpleadoUseCase_Factory implements Factory<DarDeBajaEmpleadoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public DarDeBajaEmpleadoUseCase_Factory(Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public DarDeBajaEmpleadoUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static DarDeBajaEmpleadoUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new DarDeBajaEmpleadoUseCase_Factory(empleadoRepositoryProvider);
  }

  public static DarDeBajaEmpleadoUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new DarDeBajaEmpleadoUseCase(empleadoRepository);
  }
}
