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
public final class DiaLaboralRepositoryImpl_Factory implements Factory<DiaLaboralRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  public DiaLaboralRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DiaLaboralRepositoryImpl get() {
    return newInstance(databaseProvider.get());
  }

  public static DiaLaboralRepositoryImpl_Factory create(Provider<AppDatabase> databaseProvider) {
    return new DiaLaboralRepositoryImpl_Factory(databaseProvider);
  }

  public static DiaLaboralRepositoryImpl newInstance(AppDatabase database) {
    return new DiaLaboralRepositoryImpl(database);
  }
}
