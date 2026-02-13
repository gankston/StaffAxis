package com.registro.empleados.di;

import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository;
import com.registro.empleados.domain.usecase.horas.GetHorasMensualesUseCase;
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
public final class UseCaseModule_ProvideGetHorasMensualesUseCaseFactory implements Factory<GetHorasMensualesUseCase> {
  private final Provider<HorasEmpleadoMesRepository> horasRepositoryProvider;

  public UseCaseModule_ProvideGetHorasMensualesUseCaseFactory(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider) {
    this.horasRepositoryProvider = horasRepositoryProvider;
  }

  @Override
  public GetHorasMensualesUseCase get() {
    return provideGetHorasMensualesUseCase(horasRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideGetHorasMensualesUseCaseFactory create(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider) {
    return new UseCaseModule_ProvideGetHorasMensualesUseCaseFactory(horasRepositoryProvider);
  }

  public static GetHorasMensualesUseCase provideGetHorasMensualesUseCase(
      HorasEmpleadoMesRepository horasRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetHorasMensualesUseCase(horasRepository));
  }
}
