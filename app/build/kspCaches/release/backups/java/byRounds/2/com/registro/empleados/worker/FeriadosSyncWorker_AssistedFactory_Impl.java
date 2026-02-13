package com.registro.empleados.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class FeriadosSyncWorker_AssistedFactory_Impl implements FeriadosSyncWorker_AssistedFactory {
  private final FeriadosSyncWorker_Factory delegateFactory;

  FeriadosSyncWorker_AssistedFactory_Impl(FeriadosSyncWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public FeriadosSyncWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<FeriadosSyncWorker_AssistedFactory> create(
      FeriadosSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new FeriadosSyncWorker_AssistedFactory_Impl(delegateFactory));
  }
}
