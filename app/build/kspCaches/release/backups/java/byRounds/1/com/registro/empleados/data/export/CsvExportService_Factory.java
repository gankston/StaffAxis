package com.registro.empleados.data.export;

import android.content.Context;
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
public final class CsvExportService_Factory implements Factory<CsvExportService> {
  private final Provider<Context> contextProvider;

  public CsvExportService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CsvExportService get() {
    return newInstance(contextProvider.get());
  }

  public static CsvExportService_Factory create(Provider<Context> contextProvider) {
    return new CsvExportService_Factory(contextProvider);
  }

  public static CsvExportService newInstance(Context context) {
    return new CsvExportService(context);
  }
}
