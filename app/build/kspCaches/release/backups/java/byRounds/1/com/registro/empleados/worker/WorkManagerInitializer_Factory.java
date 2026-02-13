package com.registro.empleados.worker;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class WorkManagerInitializer_Factory implements Factory<WorkManagerInitializer> {
  private final Provider<Context> contextProvider;

  public WorkManagerInitializer_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WorkManagerInitializer get() {
    return newInstance(contextProvider.get());
  }

  public static WorkManagerInitializer_Factory create(Provider<Context> contextProvider) {
    return new WorkManagerInitializer_Factory(contextProvider);
  }

  public static WorkManagerInitializer newInstance(Context context) {
    return new WorkManagerInitializer(context);
  }
}
