package com.registro.empleados.data.repository;

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

  public RegistroAsistenciaRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RegistroAsistenciaRepositoryImpl get() {
    return newInstance(databaseProvider.get());
  }

  public static RegistroAsistenciaRepositoryImpl_Factory create(
      Provider<AppDatabase> databaseProvider) {
    return new RegistroAsistenciaRepositoryImpl_Factory(databaseProvider);
  }

  public static RegistroAsistenciaRepositoryImpl newInstance(AppDatabase database) {
    return new RegistroAsistenciaRepositoryImpl(database);
  }
}
