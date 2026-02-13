package com.registro.empleados.di;

import android.content.Context;
import com.registro.empleados.data.export.ExcelExportService;
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
public final class ExportModule_ProvideExcelExportServiceFactory implements Factory<ExcelExportService> {
  private final Provider<Context> contextProvider;

  public ExportModule_ProvideExcelExportServiceFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ExcelExportService get() {
    return provideExcelExportService(contextProvider.get());
  }

  public static ExportModule_ProvideExcelExportServiceFactory create(
      Provider<Context> contextProvider) {
    return new ExportModule_ProvideExcelExportServiceFactory(contextProvider);
  }

  public static ExcelExportService provideExcelExportService(Context context) {
    return Preconditions.checkNotNullFromProvides(ExportModule.INSTANCE.provideExcelExportService(context));
  }
}
