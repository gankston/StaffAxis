package com.registro.empleados.domain.usecase.empleado;

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
public final class TieneHorasCargadasHoyUseCase_Factory implements Factory<TieneHorasCargadasHoyUseCase> {
  private final Provider<RegistroAsistenciaRepository> registroRepositoryProvider;

  public TieneHorasCargadasHoyUseCase_Factory(
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    this.registroRepositoryProvider = registroRepositoryProvider;
  }

  @Override
  public TieneHorasCargadasHoyUseCase get() {
    return newInstance(registroRepositoryProvider.get());
  }

  public static TieneHorasCargadasHoyUseCase_Factory create(
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    return new TieneHorasCargadasHoyUseCase_Factory(registroRepositoryProvider);
  }

  public static TieneHorasCargadasHoyUseCase newInstance(
      RegistroAsistenciaRepository registroRepository) {
    return new TieneHorasCargadasHoyUseCase(registroRepository);
  }
}
