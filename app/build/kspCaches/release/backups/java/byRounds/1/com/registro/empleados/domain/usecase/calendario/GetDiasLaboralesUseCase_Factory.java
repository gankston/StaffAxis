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
public final class GetDiasLaboralesUseCase_Factory implements Factory<GetDiasLaboralesUseCase> {
  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  public GetDiasLaboralesUseCase_Factory(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
  }

  @Override
  public GetDiasLaboralesUseCase get() {
    return newInstance(diaLaboralRepositoryProvider.get());
  }

  public static GetDiasLaboralesUseCase_Factory create(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    return new GetDiasLaboralesUseCase_Factory(diaLaboralRepositoryProvider);
  }

  public static GetDiasLaboralesUseCase newInstance(DiaLaboralRepository diaLaboralRepository) {
    return new GetDiasLaboralesUseCase(diaLaboralRepository);
  }
}
