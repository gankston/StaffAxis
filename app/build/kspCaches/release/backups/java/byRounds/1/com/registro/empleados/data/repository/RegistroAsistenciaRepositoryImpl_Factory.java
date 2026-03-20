package com.registro.empleados.data.repository;

import androidx.work.WorkManager;
import com.registro.empleados.data.local.database.AppDatabase;
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
public final class RegistroAsistenciaRepositoryImpl_Factory implements Factory<RegistroAsistenciaRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<WorkManager> workManagerProvider;

  public RegistroAsistenciaRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider,
      Provider<WorkManager> workManagerProvider) {
    this.databaseProvider = databaseProvider;
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public RegistroAsistenciaRepositoryImpl get() {
    return newInstance(databaseProvider.get(), workManagerProvider.get());
  }

  public static RegistroAsistenciaRepositoryImpl_Factory create(
      Provider<AppDatabase> databaseProvider, Provider<WorkManager> workManagerProvider) {
    return new RegistroAsistenciaRepositoryImpl_Factory(databaseProvider, workManagerProvider);
  }

  public static RegistroAsistenciaRepositoryImpl newInstance(AppDatabase database,
      WorkManager workManager) {
    return new RegistroAsistenciaRepositoryImpl(database, workManager);
  }
}
