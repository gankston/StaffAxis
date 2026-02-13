package com.registro.empleados.domain.usecase.database;

import com.registro.empleados.data.local.dao.EmpleadoDao;
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
public final class CorregirEmpleadosDBDirectoUseCase_Factory implements Factory<CorregirEmpleadosDBDirectoUseCase> {
  private final Provider<EmpleadoDao> empleadoDaoProvider;

  public CorregirEmpleadosDBDirectoUseCase_Factory(Provider<EmpleadoDao> empleadoDaoProvider) {
    this.empleadoDaoProvider = empleadoDaoProvider;
  }

  @Override
  public CorregirEmpleadosDBDirectoUseCase get() {
    return newInstance(empleadoDaoProvider.get());
  }

  public static CorregirEmpleadosDBDirectoUseCase_Factory create(
      Provider<EmpleadoDao> empleadoDaoProvider) {
    return new CorregirEmpleadosDBDirectoUseCase_Factory(empleadoDaoProvider);
  }

  public static CorregirEmpleadosDBDirectoUseCase newInstance(EmpleadoDao empleadoDao) {
    return new CorregirEmpleadosDBDirectoUseCase(empleadoDao);
  }
}
