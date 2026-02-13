package com.registro.empleados.di;

import android.content.Context;
import com.registro.empleados.data.export.CsvExportService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ExportModule_ProvideCsvExportServiceFactory implements Factory<CsvExportService> {
  private final Provider<Context> contextProvider;

  public ExportModule_ProvideCsvExportServiceFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CsvExportService get() {
    return provideCsvExportService(contextProvider.get());
  }

  public static ExportModule_ProvideCsvExportServiceFactory create(
      Provider<Context> contextProvider) {
    return new ExportModule_ProvideCsvExportServiceFactory(contextProvider);
  }

  public static CsvExportService provideCsvExportService(Context context) {
    return Preconditions.checkNotNullFromProvides(ExportModule.INSTANCE.provideCsvExportService(context));
  }
}
