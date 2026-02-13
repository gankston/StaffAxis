package com.registro.empleados.presentation.viewmodel;

import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase;
import com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase;
import com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase;
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
public final class CalendarioViewModel_Factory implements Factory<CalendarioViewModel> {
  private final Provider<GetDiasLaboralesUseCase> getDiasLaboralesUseCaseProvider;

  private final Provider<GetPeriodoActualUseCase> getPeriodoActualUseCaseProvider;

  private final Provider<GenerarDiasLaboralesBasicosUseCase> generarDiasLaboralesBasicosUseCaseProvider;

  private final Provider<RegistroAsistenciaRepository> registroRepositoryProvider;

  public CalendarioViewModel_Factory(
      Provider<GetDiasLaboralesUseCase> getDiasLaboralesUseCaseProvider,
      Provider<GetPeriodoActualUseCase> getPeriodoActualUseCaseProvider,
      Provider<GenerarDiasLaboralesBasicosUseCase> generarDiasLaboralesBasicosUseCaseProvider,
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    this.getDiasLaboralesUseCaseProvider = getDiasLaboralesUseCaseProvider;
    this.getPeriodoActualUseCaseProvider = getPeriodoActualUseCaseProvider;
    this.generarDiasLaboralesBasicosUseCaseProvider = generarDiasLaboralesBasicosUseCaseProvider;
    this.registroRepositoryProvider = registroRepositoryProvider;
  }

  @Override
  public CalendarioViewModel get() {
    return newInstance(getDiasLaboralesUseCaseProvider.get(), getPeriodoActualUseCaseProvider.get(), generarDiasLaboralesBasicosUseCaseProvider.get(), registroRepositoryProvider.get());
  }

  public static CalendarioViewModel_Factory create(
      Provider<GetDiasLaboralesUseCase> getDiasLaboralesUseCaseProvider,
      Provider<GetPeriodoActualUseCase> getPeriodoActualUseCaseProvider,
      Provider<GenerarDiasLaboralesBasicosUseCase> generarDiasLaboralesBasicosUseCaseProvider,
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider) {
    return new CalendarioViewModel_Factory(getDiasLaboralesUseCaseProvider, getPeriodoActualUseCaseProvider, generarDiasLaboralesBasicosUseCaseProvider, registroRepositoryProvider);
  }

  public static CalendarioViewModel newInstance(GetDiasLaboralesUseCase getDiasLaboralesUseCase,
      GetPeriodoActualUseCase getPeriodoActualUseCase,
      GenerarDiasLaboralesBasicosUseCase generarDiasLaboralesBasicosUseCase,
      RegistroAsistenciaRepository registroRepository) {
    return new CalendarioViewModel(getDiasLaboralesUseCase, getPeriodoActualUseCase, generarDiasLaboralesBasicosUseCase, registroRepository);
  }
}
