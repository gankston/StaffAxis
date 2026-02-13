package com.registro.empleados.data.remote.datasource;

import com.registro.empleados.data.remote.api.FeriadosApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FeriadosRemoteDataSource_Factory implements Factory<FeriadosRemoteDataSource> {
  private final Provider<FeriadosApiService> feriadosApiServiceProvider;

  public FeriadosRemoteDataSource_Factory(Provider<FeriadosApiService> feriadosApiServiceProvider) {
    this.feriadosApiServiceProvider = feriadosApiServiceProvider;
  }

  @Override
  public FeriadosRemoteDataSource get() {
    return newInstance(feriadosApiServiceProvider.get());
  }

  public static FeriadosRemoteDataSource_Factory create(
      Provider<FeriadosApiService> feriadosApiServiceProvider) {
    return new FeriadosRemoteDataSource_Factory(feriadosApiServiceProvider);
  }

  public static FeriadosRemoteDataSource newInstance(FeriadosApiService feriadosApiService) {
    return new FeriadosRemoteDataSource(feriadosApiService);
  }
}
