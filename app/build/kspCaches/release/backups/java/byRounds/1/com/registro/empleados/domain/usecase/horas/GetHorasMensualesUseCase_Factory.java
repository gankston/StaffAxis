package com.registro.empleados.domain.usecase.horas;

import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository;
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
public final class GetHorasMensualesUseCase_Factory implements Factory<GetHorasMensualesUseCase> {
  private final Provider<HorasEmpleadoMesRepository> horasRepositoryProvider;

  public GetHorasMensualesUseCase_Factory(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider) {
    this.horasRepositoryProvider = horasRepositoryProvider;
  }

  @Override
  public GetHorasMensualesUseCase get() {
    return newInstance(horasRepositoryProvider.get());
  }

  public static GetHorasMensualesUseCase_Factory create(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider) {
    return new GetHorasMensualesUseCase_Factory(horasRepositoryProvider);
  }

  public static GetHorasMensualesUseCase newInstance(HorasEmpleadoMesRepository horasRepository) {
    return new GetHorasMensualesUseCase(horasRepository);
  }
}
