package com.registro.empleados.domain.usecase.feriados;

import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource;
import com.registro.empleados.domain.repository.DiaLaboralRepository;
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
public final class SincronizarFeriadosUseCase_Factory implements Factory<SincronizarFeriadosUseCase> {
  private final Provider<FeriadosRemoteDataSource> feriadosRemoteDataSourceProvider;

  private final Provider<DiaLaboralRepository> diaLaboralRepositoryProvider;

  public SincronizarFeriadosUseCase_Factory(
      Provider<FeriadosRemoteDataSource> feriadosRemoteDataSourceProvider,
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    this.feriadosRemoteDataSourceProvider = feriadosRemoteDataSourceProvider;
    this.diaLaboralRepositoryProvider = diaLaboralRepositoryProvider;
  }

  @Override
  public SincronizarFeriadosUseCase get() {
    return newInstance(feriadosRemoteDataSourceProvider.get(), diaLaboralRepositoryProvider.get());
  }

  public static SincronizarFeriadosUseCase_Factory create(
      Provider<FeriadosRemoteDataSource> feriadosRemoteDataSourceProvider,
      Provider<DiaLaboralRepository> diaLaboralRepositoryProvider) {
    return new SincronizarFeriadosUseCase_Factory(feriadosRemoteDataSourceProvider, diaLaboralRepositoryProvider);
  }

  public static SincronizarFeriadosUseCase newInstance(
      FeriadosRemoteDataSource feriadosRemoteDataSource,
      DiaLaboralRepository diaLaboralRepository) {
    return new SincronizarFeriadosUseCase(feriadosRemoteDataSource, diaLaboralRepository);
  }
}
