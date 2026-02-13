package com.registro.empleados.di;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository;
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository;
import com.registro.empleados.domain.usecase.horas.ActualizarHorasMensualesUseCase;
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
public final class UseCaseModule_ProvideActualizarHorasMensualesUseCaseFactory implements Factory<ActualizarHorasMensualesUseCase> {
  private final Provider<HorasEmpleadoMesRepository> horasRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroRepositoryProvider;

  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public UseCaseModule_ProvideActualizarHorasMensualesUseCaseFactory(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.horasRepositoryProvider = horasRepositoryProvider;
    this.registroRepositoryProvider = registroRepositoryProvider;
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public ActualizarHorasMensualesUseCase get() {
    return provideActualizarHorasMensualesUseCase(horasRepositoryProvider.get(), registroRepositoryProvider.get(), empleadoRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideActualizarHorasMensualesUseCaseFactory create(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new UseCaseModule_ProvideActualizarHorasMensualesUseCaseFactory(horasRepositoryProvider, registroRepositoryProvider, empleadoRepositoryProvider);
  }

  public static ActualizarHorasMensualesUseCase provideActualizarHorasMensualesUseCase(
      HorasEmpleadoMesRepository horasRepository, RegistroAsistenciaRepository registroRepository,
      EmpleadoRepository empleadoRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideActualizarHorasMensualesUseCase(horasRepository, registroRepository, empleadoRepository));
  }
}
