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
public final class HorasEmpleadoMesRepositoryImpl_Factory implements Factory<HorasEmpleadoMesRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  public HorasEmpleadoMesRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public HorasEmpleadoMesRepositoryImpl get() {
    return newInstance(databaseProvider.get());
  }

  public static HorasEmpleadoMesRepositoryImpl_Factory create(
      Provider<AppDatabase> databaseProvider) {
    return new HorasEmpleadoMesRepositoryImpl_Factory(databaseProvider);
  }

  public static HorasEmpleadoMesRepositoryImpl newInstance(AppDatabase database) {
    return new HorasEmpleadoMesRepositoryImpl(database);
  }
}
