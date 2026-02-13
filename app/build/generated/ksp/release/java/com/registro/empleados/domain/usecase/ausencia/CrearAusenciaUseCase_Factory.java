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
public final class CrearAusenciaUseCase_Factory implements Factory<CrearAusenciaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public CrearAusenciaUseCase_Factory(Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public CrearAusenciaUseCase get() {
    return newInstance(ausenciaRepositoryProvider.get());
  }

  public static CrearAusenciaUseCase_Factory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new CrearAusenciaUseCase_Factory(ausenciaRepositoryProvider);
  }

  public static CrearAusenciaUseCase newInstance(AusenciaRepository ausenciaRepository) {
    return new CrearAusenciaUseCase(ausenciaRepository);
  }
}
