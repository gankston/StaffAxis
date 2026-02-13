package com.registro.empleados.di;

import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource;
import com.registro.empleados.domain.repository.DiaLaboralRepository;
import com.registro.empleados.domain.usecase.feriados.SincronizarFeriadosUseCase;
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
public final class UseCaseModule_ProvideSincronizarFeriadosUseCaseFactory implements Factory<SincronizarFeriadosUseCase> {
  private final Provider<FeriadosRemoteDataSource> feriadosRemoteDataSourceProvider;

  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  public UseCaseModule_ProvideSincronizarFeriadosUseCaseFactory(
      Provider<FeriadosRemoteDataSource> feriadosRemoteDataSourceProvider,
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    this.feriadosRemoteDataSourceProvider = feriadosRemoteDataSourceProvider;
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
  }

  @Override
  public SincronizarFeriadosUseCase get() {
    return provideSincronizarFeriadosUseCase(feriadosRemoteDataSourceProvider.get(), diaLaboralRepositoryProvider.get());
  }

  public static UseCaseModule_ProvideSincronizarFeriadosUseCaseFactory create(
      Provider<FeriadosRemoteDataSource> feriadosRemoteDataSourceProvider,
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    return new UseCaseModule_ProvideSincronizarFeriadosUseCaseFactory(feriadosRemoteDataSourceProvider, diaLaboralRepositoryProvider);
  }

  public static SincronizarFeriadosUseCase provideSincronizarFeriadosUseCase(
      FeriadosRemoteDataSource feriadosRemoteDataSource,
      DiaLaboralRepository diaLaboralRepository) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideSincronizarFeriadosUseCase(feriadosRemoteDataSource, diaLaboralRepository));
  }
}
