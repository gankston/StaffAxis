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
public final class GetEmpleadoByLegajoUseCase_Factory implements Factory<GetEmpleadoByLegajoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public GetEmpleadoByLegajoUseCase_Factory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public GetEmpleadoByLegajoUseCase get() {
    return newInstance(empleadoRepositoryProvider.get());
  }

  public static GetEmpleadoByLegajoUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new GetEmpleadoByLegajoUseCase_Factory(empleadoRepositoryProvider);
  }

  public static GetEmpleadoByLegajoUseCase newInstance(EmpleadoRepository empleadoRepository) {
    return new GetEmpleadoByLegajoUseCase(empleadoRepository);
  }
}
