package com.registro.empleados.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.registro.empleados.domain.usecase.feriados.SincronizarFeriadosUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class FeriadosSyncWorker_Factory {
  private final Provider<SincronizarFeriadosUseCase> sincronizarFeriadosUseCaseProvider;

  public FeriadosSyncWorker_Factory(
      Provider<SincronizarFeriadosUseCase> sincronizarFeriadosUseCaseProvider) {
    this.sincronizarFeriadosUseCaseProvider = sincronizarFeriadosUseCaseProvider;
  }

  public FeriadosSyncWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, sincronizarFeriadosUseCaseProvider.get());
  }

  public static FeriadosSyncWorker_Factory create(
      Provider<SincronizarFeriadosUseCase> sincronizarFeriadosUseCaseProvider) {
    return new FeriadosSyncWorker_Factory(sincronizarFeriadosUseCaseProvider);
  }

  public static FeriadosSyncWorker newInstance(Context context, WorkerParameters workerParams,
      SincronizarFeriadosUseCase sincronizarFeriadosUseCase) {
    return new FeriadosSyncWorker(context, workerParams, sincronizarFeriadosUseCase);
  }
}
