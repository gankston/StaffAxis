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
public final class EliminarAusenciaUseCase_Factory implements Factory<EliminarAusenciaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public EliminarAusenciaUseCase_Factory(Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public EliminarAusenciaUseCase get() {
    return newInstance(ausenciaRepositoryProvider.get());
  }

  public static EliminarAusenciaUseCase_Factory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new EliminarAusenciaUseCase_Factory(ausenciaRepositoryProvider);
  }

  public static EliminarAusenciaUseCase newInstance(AusenciaRepository ausenciaRepository) {
    return new EliminarAusenciaUseCase(ausenciaRepository);
  }
}
