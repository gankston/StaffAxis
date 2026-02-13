package com.registro.empleados.domain.usecase.calendario;

import com.registro.empleados.domain.repository.DiaLaboralRepository;
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
public final class GenerarDiasLaboralesBasicosUseCase_Factory implements Factory<GenerarDiasLaboralesBasicosUseCase> {
  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  public GenerarDiasLaboralesBasicosUseCase_Factory(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
  }

  @Override
  public GenerarDiasLaboralesBasicosUseCase get() {
    return newInstance(diaLaboralRepositoryProvider.get());
  }

  public static GenerarDiasLaboralesBasicosUseCase_Factory create(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    return new GenerarDiasLaboralesBasicosUseCase_Factory(diaLaboralRepositoryProvider);
  }

  public static GenerarDiasLaboralesBasicosUseCase newInstance(
      DiaLaboralRepository diaLaboralRepository) {
    return new GenerarDiasLaboralesBasicosUseCase(diaLaboralRepository);
  }
}
