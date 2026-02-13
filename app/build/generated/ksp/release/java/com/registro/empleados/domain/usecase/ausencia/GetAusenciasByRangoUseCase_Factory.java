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
public final class GetAusenciasByRangoUseCase_Factory implements Factory<GetAusenciasByRangoUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public GetAusenciasByRangoUseCase_Factory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public GetAusenciasByRangoUseCase get() {
    return newInstance(ausenciaRepositoryProvider.get());
  }

  public static GetAusenciasByRangoUseCase_Factory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new GetAusenciasByRangoUseCase_Factory(ausenciaRepositoryProvider);
  }

  public static GetAusenciasByRangoUseCase newInstance(AusenciaRepository ausenciaRepository) {
    return new GetAusenciasByRangoUseCase(ausenciaRepository);
  }
}
