package com.registro.empleados.domain.usecase.calendario;

import com.registro.empleados.domain.repository.DiaLaboralRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
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
public final class GetCalendarioPeriodoUseCase_Factory implements Factory<GetCalendarioPeriodoUseCase> {
  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  public GetCalendarioPeriodoUseCase_Factory(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
  }

  @Override
  public GetCalendarioPeriodoUseCase get() {
    return newInstance(diaLaboralRepositoryProvider.get(), registroAsistenciaRepositoryProvider.get());
  }

  public static GetCalendarioPeriodoUseCase_Factory create(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    return new GetCalendarioPeriodoUseCase_Factory(diaLaboralRepositoryProvider, registroAsistenciaRepositoryProvider);
  }

  public static GetCalendarioPeriodoUseCase newInstance(DiaLaboralRepository diaLaboralRepository,
      RegistroAsistenciaRepository registroAsistenciaRepository) {
    return new GetCalendarioPeriodoUseCase(diaLaboralRepository, registroAsistenciaRepository);
  }
}
