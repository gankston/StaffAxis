package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.database.LimpiarBaseDatosUseCase;
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
public final class UseCaseModule_ProvideLimpiarBaseDatosUseCaseFactory implements Factory<LimpiarBaseDatosUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  public UseCaseModule_ProvideLimpiarBaseDatosUseCaseFactory(
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
  }

  @Override
  public LimpiarBaseDatosUseCase get() {
    return provideLimpiarBaseDatosUseCase(empleadoRepositoryProvider.get(), registroAsistenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideLimpiarBaseDatosUseCaseFactory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    return new UseCaseModule_ProvideLimpiarBaseDatosUseCaseFactory(empleadoRepositoryProvider, registroAsistenciaRepositoryProvider);
  }

  public static LimpiarBaseDatosUseCase provideLimpiarBaseDatosUseCase(
      EmpleadoRepository empleadoRepository,
      RegistroAsistenciaRepository registroAsistenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideLimpiarBaseDatosUseCase(empleadoRepository, registroAsistenciaRepository));
  }
}
