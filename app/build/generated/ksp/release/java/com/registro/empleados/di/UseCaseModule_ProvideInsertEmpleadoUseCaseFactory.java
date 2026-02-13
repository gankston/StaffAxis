package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase;
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
public final class UseCaseModule_ProvideInsertEmpleadoUseCaseFactory implements Factory<InsertEmpleadoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideInsertEmpleadoUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public InsertEmpleadoUseCase get() {
    return provideInsertEmpleadoUseCase(empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideInsertEmpleadoUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideInsertEmpleadoUseCaseFactory(empleadoRepositoryProvider);
  }

  public static InsertEmpleadoUseCase provideInsertEmpleadoUseCase(
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideInsertEmpleadoUseCase(empleadoRepository));
  }
}
