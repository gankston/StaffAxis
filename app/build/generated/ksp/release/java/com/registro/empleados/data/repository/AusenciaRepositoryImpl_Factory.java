package com.registro.empleados.data.repository;

import com.registro.empleados.data.local.dao.AusenciaDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AusenciaRepositoryImpl_Factory implements Factory<AusenciaRepositoryImpl> {
  private final Provider<AusenciaDao> ausenciaDaoProvider;

  public AusenciaRepositoryImpl_Factory(Provider<AusenciaDao> ausenciaDaoProvider) {
    this.ausenciaDaoProvider = ausenciaDaoProvider;
  }

  @Override
  public AusenciaRepositoryImpl get() {
    return newInstance(ausenciaDaoProvider.get());
  }

  public static AusenciaRepositoryImpl_Factory create(Provider<AusenciaDao> ausenciaDaoProvider) {
    return new AusenciaRepositoryImpl_Factory(ausenciaDaoProvider);
  }

  public static AusenciaRepositoryImpl newInstance(AusenciaDao ausenciaDao) {
    return new AusenciaRepositoryImpl(ausenciaDao);
  }
}
