package com.registro.empleados.di;

import com.registro.empleados.domain.repository.AusenciaRepository;
import com.registro.empleados.domain.usecase.ausencia.CrearAusenciaUseCase;
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
public final class UseCaseModule_ProvideCrearAusenciaUseCaseFactory implements Factory<CrearAusenciaUseCase> {
  private final Provider<AusenciaRepository> ausenciaRepositoryProvider;

  public UseCaseModule_ProvideCrearAusenciaUseCaseFactory(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    this.ausenciaRepositoryProvider = ausenciaRepositoryProvider;
  }

  @Override
  public CrearAusenciaUseCase get() {
    return provideCrearAusenciaUseCase(ausenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideCrearAusenciaUseCaseFactory create(
      Provider<AusenciaRepository> ausenciaRepositoryProvider) {
    return new UseCaseModule_ProvideCrearAusenciaUseCaseFactory(ausenciaRepositoryProvider);
  }

  public static CrearAusenciaUseCase provideCrearAusenciaUseCase(
      AusenciaRepository ausenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideCrearAusenciaUseCase(ausenciaRepository));
  }
}
