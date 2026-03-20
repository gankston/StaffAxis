package com.registro.empleados.data.repository;

import com.registro.empleados.data.local.database.AppDatabase;
import com.registro.empleados.data.remote.api.EmployeesApiService;
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
public final class EmpleadoRepositoryImpl_Factory implements Factory<EmpleadoRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<EmployeesApiService> employeesApiServiceProvider;

  public EmpleadoRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider,
      Provider<EmployeesApiService> employeesApiServiceProvider) {
    this.databaseProvider = databaseProvider;
    this.employeesApiServiceProvider = employeesApiServiceProvider;
  }

  @Override
  public EmpleadoRepositoryImpl get() {
    return newInstance(databaseProvider.get(), employeesApiServiceProvider.get());
  }

  public static EmpleadoRepositoryImpl_Factory create(Provider<AppDatabase> databaseProvider,
      Provider<EmployeesApiService> employeesApiServiceProvider) {
    return new EmpleadoRepositoryImpl_Factory(databaseProvider, employeesApiServiceProvider);
  }

  public static EmpleadoRepositoryImpl newInstance(AppDatabase database,
      EmployeesApiService employeesApiService) {
    return new EmpleadoRepositoryImpl(database, employeesApiService);
  }
}
