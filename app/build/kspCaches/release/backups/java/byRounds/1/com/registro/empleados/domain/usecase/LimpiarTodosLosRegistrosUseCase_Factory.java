package com.registro.empleados.domain.usecase;

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
public final class LimpiarTodosLosRegistrosUseCase_Factory implements Factory<LimpiarTodosLosRegistrosUseCase> {
  private final Provider<RegistroAsistenciaRepository> registroRepositoryProvider;

  public LimpiarTodosLosRegistrosUseCase_Factory(
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    this.registroRepositoryProvider = registroRepositoryProvider;
  }

  @Override
  public LimpiarTodosLosRegistrosUseCase get() {
    return newInstance(registroRepositoryProvider.get());
  }

  public static LimpiarTodosLosRegistrosUseCase_Factory create(
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    return new LimpiarTodosLosRegistrosUseCase_Factory(registroRepositoryProvider);
  }

  public static LimpiarTodosLosRegistrosUseCase newInstance(
      RegistroAsistenciaRepository registroRepository) {
    return new LimpiarTodosLosRegistrosUseCase(registroRepository);
  }
}
