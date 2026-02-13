package com.registro.empleados.di;

import com.registro.empleados.data.remote.api.FeriadosApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideFeriadosApiServiceFactory implements Factory<FeriadosApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideFeriadosApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public FeriadosApiService get() {
    return provideFeriadosApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideFeriadosApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideFeriadosApiServiceFactory(retrofitProvider);
  }

  public static FeriadosApiService provideFeriadosApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFeriadosApiService(retrofit));
  }
}
