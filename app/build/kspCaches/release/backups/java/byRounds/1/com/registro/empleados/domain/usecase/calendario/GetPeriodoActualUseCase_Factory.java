package com.registro.empleados.domain.usecase.calendario;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class GetPeriodoActualUseCase_Factory implements Factory<GetPeriodoActualUseCase> {
  @Override
  public GetPeriodoActualUseCase get() {
    return newInstance();
  }

  public static GetPeriodoActualUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GetPeriodoActualUseCase newInstance() {
    return new GetPeriodoActualUseCase();
  }

  private static final class InstanceHolder {
    private static final GetPeriodoActualUseCase_Factory INSTANCE = new GetPeriodoActualUseCase_Factory();
  }
}
