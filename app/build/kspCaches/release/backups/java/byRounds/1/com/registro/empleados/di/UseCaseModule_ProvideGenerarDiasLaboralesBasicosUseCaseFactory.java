package com.registro.empleados.di;

import com.registro.empleados.domain.repository.DiaLaboralRepository;
import com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase;
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
public final class UseCaseModule_ProvideGenerarDiasLaboralesBasicosUseCaseFactory implements Factory<GenerarDiasLaboralesBasicosUseCase> {
  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  public UseCaseModule_ProvideGenerarDiasLaboralesBasicosUseCaseFactory(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
  }

  @Override
  public GenerarDiasLaboralesBasicosUseCase get() {
    return provideGenerarDiasLaboralesBasicosUseCase(diaLaboralRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGenerarDiasLaboralesBasicosUseCaseFactory create(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    return new UseCaseModule_ProvideGenerarDiasLaboralesBasicosUseCaseFactory(diaLaboralRepositoryProvider);
  }

  public static GenerarDiasLaboralesBasicosUseCase provideGenerarDiasLaboralesBasicosUseCase(
      DiaLaboralRepository diaLaboralRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGenerarDiasLaboralesBasicosUseCase(diaLaboralRepository));
  }
}
