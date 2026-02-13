package com.registro.empleados.di;

import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.asistencia.GetRegistrosByRangoUseCase;
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
public final class UseCaseModule_ProvideGetRegistrosByRangoUseCaseFactory implements Factory<GetRegistrosByRangoUseCase> {
  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  public UseCaseModule_ProvideGetRegistrosByRangoUseCaseFactory(
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
  }

  @Override
  public GetRegistrosByRangoUseCase get() {
    return provideGetRegistrosByRangoUseCase(registroAsistenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetRegistrosByRangoUseCaseFactory create(
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    return new UseCaseModule_ProvideGetRegistrosByRangoUseCaseFactory(registroAsistenciaRepositoryProvider);
  }

  public static GetRegistrosByRangoUseCase provideGetRegistrosByRangoUseCase(
      RegistroAsistenciaRepository registroAsistenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetRegistrosByRangoUseCase(registroAsistenciaRepository));
  }
}
