package com.registro.empleados.di;

import com.registro.empleados.domain.repository.AusenciaRepository;
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase;
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
public final class UseCaseModule_ProvideGetAusenciasByFechaUseCaseFactory implements Factory<GetAusenciasByFechaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public UseCaseModule_ProvideGetAusenciasByFechaUseCaseFactory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public GetAusenciasByFechaUseCase get() {
    return provideGetAusenciasByFechaUseCase(ausenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetAusenciasByFechaUseCaseFactory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new UseCaseModule_ProvideGetAusenciasByFechaUseCaseFactory(ausenciaRepositoryProvider);
  }

  public static GetAusenciasByFechaUseCase provideGetAusenciasByFechaUseCase(
      AusenciaRepository ausenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetAusenciasByFechaUseCase(ausenciaRepository));
  }
}
