package com.registro.empleados.domain.usecase.horas;

import com.registro.empleados.domain.repository.EmpleadoRepository;
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository;
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
public final class ActualizarHorasMensualesUseCase_Factory implements Factory<ActualizarHorasMensualesUseCase> {
  private final Provider<HorasEmpleadoMesRepository> horasRepositoryProvider;

  private final Provider<RegistroAsistenciaRepository> registroRepositoryProvider;

  private final Provider<EmpleadoRepository> empleadoRepositoryProvider;

  public ActualizarHorasMensualesUseCase_Factory(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    this.horasRepositoryProvider = horasRepositoryProvider;
    this.registroRepositoryProvider = registroRepositoryProvider;
    this.empleadoRepositoryProvider = empleadoRepositoryProvider;
  }

  @Override
  public ActualizarHorasMensualesUseCase get() {
    return newInstance(horasRepositoryProvider.get(), registroRepositoryProvider.get(), empleadoRepositoryProvider.get());
  }

  public static ActualizarHorasMensualesUseCase_Factory create(
      Provider<HorasEmpleadoMesRepository> horasRepositoryProvider,
      Provider<RegistroAsistenciaRepository> registroRepositoryProvider,
      Provider<EmpleadoRepository> empleadoRepositoryProvider) {
    return new ActualizarHorasMensualesUseCase_Factory(horasRepositoryProvider, registroRepositoryProvider, empleadoRepositoryProvider);
  }

  public static ActualizarHorasMensualesUseCase newInstance(
      HorasEmpleadoMesRepository horasRepository, RegistroAsistenciaRepository registroRepository,
      EmpleadoRepository empleadoRepository) {
    return new ActualizarHorasMensualesUseCase(horasRepository, registroRepository, empleadoRepository);
  }
}
