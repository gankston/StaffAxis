package com.registro.empleados.di;

import com.registro.empleados.domain.repository.DiaLaboralRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.calendario.GetCalendarioPeriodoUseCase;
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
public final class UseCaseModule_ProvideGetCalendarioPeriodoUseCaseFactory implements Factory<GetCalendarioPeriodoUseCase> {
  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider;

  public UseCaseModule_ProvideGetCalendarioPeriodoUseCaseFactory(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
    this.registroAsistenciaRepositoryProvider = registroAsistenciaRepositoryProvider;
  }

  @Override
  public GetCalendarioPeriodoUseCase get() {
    return provideGetCalendarioPeriodoUseCase(diaLaboralRepositoryProvider.get(), registroAsistenciaRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetCalendarioPeriodoUseCaseFactory create(
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroAsistenciaRepositoryProvider) {
    return new UseCaseModule_ProvideGetCalendarioPeriodoUseCaseFactory(diaLaboralRepositoryProvider, registroAsistenciaRepositoryProvider);
  }

  public static GetCalendarioPeriodoUseCase provideGetCalendarioPeriodoUseCase(
      DiaLaboralRepository diaLaboralRepository,
      RegistroAsistenciaRepository registroAsistenciaRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetCalendarioPeriodoUseCase(diaLaboralRepository, registroAsistenciaRepository));
  }
}
