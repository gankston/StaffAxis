package com.registro.empleados.di;

import com.registro.empleados.data.local.dao.AusenciaDao;
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
public final class DatabaseModule_ProvideAusenciaDaoFactory implements Factory<AusenciaDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideAusenciaDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AusenciaDao get() {
    return provideAusenciaDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideAusenciaDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideAusenciaDaoFactory(databaseProvider);
  }

  public static AusenciaDao provideAusenciaDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAusenciaDao(database));
  }
}
