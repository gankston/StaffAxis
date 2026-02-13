package com.registro.empleados.domain.usecase.asistencia;

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
public final class GetRegistrosByRangoUseCase_Factory implements Factory<GetRegistrosByRangoUseCase> {
  private final Provider<RegistroAsistenciaRepository> registroRepositoryProvider;

  public GetRegistrosByRangoUseCase_Factory(
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    this.registroRepositoryProvider = registroRepositoryProvider;
  }

  @Override
  public GetRegistrosByRangoUseCase get() {
    return newInstance(registroRepositoryProvider.get());
  }

  public static GetRegistrosByRangoUseCase_Factory create(
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    return new GetRegistrosByRangoUseCase_Factory(registroRepositoryProvider);
  }

  public static GetRegistrosByRangoUseCase newInstance(
      RegistroAsistenciaRepository registroRepository) {
    return new GetRegistrosByRangoUseCase(registroRepository);
  }
}
