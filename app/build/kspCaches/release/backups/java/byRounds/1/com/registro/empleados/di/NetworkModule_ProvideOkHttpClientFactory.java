package com.registro.empleados.di;

import android.content.Context;
import com.registro.empleados.data.local.preferences.DevicePrefs;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<Context> contextProvider;

  private final Provider<DevicePrefs> devicePrefsProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<Context> contextProvider,
      Provider<DevicePrefs> devicePrefsProvider) {
    this.contextProvider = contextProvider;
    this.devicePrefsProvider = devicePrefsProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(contextProvider.get(), devicePrefsProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(Provider<Context> contextProvider,
      Provider<DevicePrefs> devicePrefsProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(contextProvider, devicePrefsProvider);
  }

  public static OkHttpClient provideOkHttpClient(Context context, DevicePrefs devicePrefs) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(context, devicePrefs));
  }
}
