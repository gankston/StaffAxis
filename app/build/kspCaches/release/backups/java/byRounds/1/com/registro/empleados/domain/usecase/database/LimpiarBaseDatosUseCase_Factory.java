package com.registro.empleados.domain.usecase.database;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
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
public final class LimpiarBaseDatosUseCase_Factory implements Factory<LimpiarBaseDatosUseCase> {
  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  public LimpiarBaseDatosUseCase_Factory(Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
  }

  @Override
  public LimpiarBaseDatosUseCase get() {
    return newInstance(empleadoRepositoryProvider.get(), registroAsistenciaRepositoryProvider.get());
  }

  public static LimpiarBaseDatosUseCase_Factory create(
      Provider<EmpleadoRepository> empleadoRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    return new LimpiarBaseDatosUseCase_Factory(empleadoRepositoryProvider, registroAsistenciaRepositoryProvider);
  }

  public static LimpiarBaseDatosUseCase newInstance(EmpleadoRepository empleadoRepository,
      RegistroAsistenciaRepository registroAsistenciaRepository) {
    return new LimpiarBaseDatosUseCase(empleadoRepository, registroAsistenciaRepository);
  }
}
