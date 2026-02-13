package com.registro.empleados.domain.usecase.ausencia;

import com.registro.empleados.domain.repository.AusenciaRepository;
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
public final class ActualizarAusenciaUseCase_Factory implements Factory<ActualizarAusenciaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public ActualizarAusenciaUseCase_Factory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public ActualizarAusenciaUseCase get() {
    return newInstance(ausenciaRepositoryProvider.get());
  }

  public static ActualizarAusenciaUseCase_Factory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new ActualizarAusenciaUseCase_Factory(ausenciaRepositoryProvider);
  }

  public static ActualizarAusenciaUseCase newInstance(AusenciaRepository ausenciaRepository) {
    return new ActualizarAusenciaUseCase(ausenciaRepository);
  }
}
