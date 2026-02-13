package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase;
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
public final class UseCaseModule_ProvideUpdateEmpleadoUseCaseFactory implements Factory<UpdateEmpleadoUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideUpdateEmpleadoUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public UpdateEmpleadoUseCase get() {
    return provideUpdateEmpleadoUseCase(empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideUpdateEmpleadoUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideUpdateEmpleadoUseCaseFactory(empleadoRepositoryProvider);
  }

  public static UpdateEmpleadoUseCase provideUpdateEmpleadoUseCase(
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideUpdateEmpleadoUseCase(empleadoRepository));
  }
}
