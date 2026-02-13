package com.registro.empleados.worker;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = FeriadosSyncWorker.class
)
public interface FeriadosSyncWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.registro.empleados.worker.FeriadosSyncWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      FeriadosSyncWorker_AssistedFactory factory);
}
