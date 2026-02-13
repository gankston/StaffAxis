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
public final class ExcelExportService_Factory implements Factory<ExcelExportService> {
  private final Provider<Context> contextProvider;

  public ExcelExportService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ExcelExportService get() {
    return newInstance(contextProvider.get());
  }

  public static ExcelExportService_Factory create(Provider<Context> contextProvider) {
    return new ExcelExportService_Factory(contextProvider);
  }

  public static ExcelExportService newInstance(Context context) {
    return new ExcelExportService(context);
  }
}
