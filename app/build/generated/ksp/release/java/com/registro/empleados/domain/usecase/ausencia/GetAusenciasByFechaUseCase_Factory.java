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
public final class GetAusenciasByFechaUseCase_Factory implements Factory<GetAusenciasByFechaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public GetAusenciasByFechaUseCase_Factory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public GetAusenciasByFechaUseCase get() {
    return newInstance(ausenciaRepositoryProvider.get());
  }

  public static GetAusenciasByFechaUseCase_Factory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new GetAusenciasByFechaUseCase_Factory(ausenciaRepositoryProvider);
  }

  public static GetAusenciasByFechaUseCase newInstance(AusenciaRepository ausenciaRepository) {
    return new GetAusenciasByFechaUseCase(ausenciaRepository);
  }
}
