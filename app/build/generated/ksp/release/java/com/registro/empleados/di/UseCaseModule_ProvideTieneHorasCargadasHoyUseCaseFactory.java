package com.registro.empleados.di;

import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase;
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
public final class UseCaseModule_ProvideTieneHorasCargadasHoyUseCaseFactory implements Factory<TieneHorasCargadasHoyUseCase> {
  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  public UseCaseModule_ProvideTieneHorasCargadasHoyUseCaseFactory(
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
  }

  @Override
  public TieneHorasCargadasHoyUseCase get() {
    return provideTieneHorasCargadasHoyUseCase(registroAsistenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideTieneHorasCargadasHoyUseCaseFactory create(
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    return new UseCaseModule_ProvideTieneHorasCargadasHoyUseCaseFactory(registroAsistenciaRepositoryProvider);
  }

  public static TieneHorasCargadasHoyUseCase provideTieneHorasCargadasHoyUseCase(
      RegistroAsistenciaRepository registroAsistenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideTieneHorasCargadasHoyUseCase(registroAsistenciaRepository));
  }
}
