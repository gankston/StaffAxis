package com.registro.empleados.di;

import com.registro.empleados.data.remote.api.FeriadosApiService;
import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource;
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
public final class NetworkModule_ProvideFeriadosRemoteDataSourceFactory implements Factory<FeriadosRemoteDataSource> {
  private final Provider<FeriadosApiService> feriadosApiServiceProvider;

  public NetworkModule_ProvideFeriadosRemoteDataSourceFactory(
      Provider<FeriadosApiService> feriadosApiServiceProvider) {
    this.feriadosApiServiceProvider = feriadosApiServiceProvider;
  }

  @Override
  public FeriadosRemoteDataSource get() {
    return provideFeriadosRemoteDataSource(feriadosApiServiceProvider.get());
  }

  public static NetworkModule_ProvideFeriadosRemoteDataSourceFactory create(
      Provider<FeriadosApiService> feriadosApiServiceProvider) {
    return new NetworkModule_ProvideFeriadosRemoteDataSourceFactory(feriadosApiServiceProvider);
  }

  public static FeriadosRemoteDataSource provideFeriadosRemoteDataSource(
      FeriadosApiService feriadosApiService) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFeriadosRemoteDataSource(feriadosApiService));
  }
}
