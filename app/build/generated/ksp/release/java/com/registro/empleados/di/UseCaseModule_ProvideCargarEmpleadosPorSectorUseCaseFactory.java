package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.empleado.CargarEmpleadosPorSectorUseCase;
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
public final class UseCaseModule_ProvideCargarEmpleadosPorSectorUseCaseFactory implements Factory<CargarEmpleadosPorSectorUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideCargarEmpleadosPorSectorUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public CargarEmpleadosPorSectorUseCase get() {
    return provideCargarEmpleadosPorSectorUseCase(empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideCargarEmpleadosPorSectorUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideCargarEmpleadosPorSectorUseCaseFactory(empleadoRepositoryProvider);
  }

  public static CargarEmpleadosPorSectorUseCase provideCargarEmpleadosPorSectorUseCase(
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideCargarEmpleadosPorSectorUseCase(empleadoRepository));
  }
}
