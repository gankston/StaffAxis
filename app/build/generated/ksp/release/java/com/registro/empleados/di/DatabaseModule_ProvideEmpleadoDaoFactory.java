package com.registro.empleados.di;

import com.registro.empleados.data.local.dao.EmpleadoDao;
import com.registro.empleados.data.local.database.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideEmpleadoDaoFactory implements Factory<EmpleadoDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideEmpleadoDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public EmpleadoDao get() {
    return provideEmpleadoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideEmpleadoDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideEmpleadoDaoFactory(databaseProvider);
  }

  public static EmpleadoDao provideEmpleadoDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideEmpleadoDao(database));
  }
}
