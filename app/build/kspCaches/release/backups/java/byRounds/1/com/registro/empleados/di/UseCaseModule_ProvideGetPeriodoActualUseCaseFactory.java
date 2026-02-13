package com.registro.empleados.di;

import com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class UseCaseModule_ProvideGetPeriodoActualUseCaseFactory implements Factory<GetPeriodoActualUseCase> {
  @Override
  public GetPeriodoActualUseCase get() {
    return provideGetPeriodoActualUseCase();
  }

  public static UseCaseModule_ProvideGetPeriodoActualUseCaseFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GetPeriodoActualUseCase provideGetPeriodoActualUseCase() {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetPeriodoActualUseCase());
  }

  private static final class InstanceHolder {
    private static final UseCaseModule_ProvideGetPeriodoActualUseCaseFactory INSTANCE = new UseCaseModule_ProvideGetPeriodoActualUseCaseFactory();
  }
}
