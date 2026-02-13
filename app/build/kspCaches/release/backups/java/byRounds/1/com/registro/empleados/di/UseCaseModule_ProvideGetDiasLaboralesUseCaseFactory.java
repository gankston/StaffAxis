package com.registro.empleados.di;

import com.registro.empleados.domain.repository.DiaLaboralRepository;
import com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase;
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
public final class UseCaseModule_ProvideGetDiasLaboralesUseCaseFactory implements Factory<GetDiasLaboralesUseCase> {
  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  public UseCaseModule_ProvideGetDiasLaboralesUseCaseFactory(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
  }

  @Override
  public GetDiasLaboralesUseCase get() {
    return provideGetDiasLaboralesUseCase(diaLaboralRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetDiasLaboralesUseCaseFactory create(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    return new UseCaseModule_ProvideGetDiasLaboralesUseCaseFactory(diaLaboralRepositoryProvider);
  }

  public static GetDiasLaboralesUseCase provideGetDiasLaboralesUseCase(
      DiaLaboralRepository diaLaboralRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetDiasLaboralesUseCase(diaLaboralRepository));
  }
}
