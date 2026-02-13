package com.registro.empleados.di;

import com.registro.empleados.domain.repository.AusenciaRepository;
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByRangoUseCase;
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
public final class UseCaseModule_ProvideGetAusenciasByRangoUseCaseFactory implements Factory<GetAusenciasByRangoUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public UseCaseModule_ProvideGetAusenciasByRangoUseCaseFactory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public GetAusenciasByRangoUseCase get() {
    return provideGetAusenciasByRangoUseCase(ausenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetAusenciasByRangoUseCaseFactory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new UseCaseModule_ProvideGetAusenciasByRangoUseCaseFactory(ausenciaRepositoryProvider);
  }

  public static GetAusenciasByRangoUseCase provideGetAusenciasByRangoUseCase(
      AusenciaRepository ausenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetAusenciasByRangoUseCase(ausenciaRepository));
  }
}
