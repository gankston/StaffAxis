package com.registro.empleados;

import com.registro.empleados.worker.WorkManagerInitializer;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AsistenciaApplication_MembersInjector implements MembersInjector<AsistenciaApplication> {
  private final Provider<WorkManagerInitializer> workManagerInitializerProvider;

  public AsistenciaApplication_MembersInjector(
      Provider<WorkManagerInitializer> workManagerInitializerProvider) {
    this.workManagerInitializerProvider = workManagerInitializerProvider;
  }

  public static MembersInjector<AsistenciaApplication> create(
      Provider<WorkManagerInitializer> workManagerInitializerProvider) {
    return new AsistenciaApplication_MembersInjector(workManagerInitializerProvider);
  }

  @Override
  public void injectMembers(AsistenciaApplication instance) {
    injectWorkManagerInitializer(instance, workManagerInitializerProvider.get());
  }

  @InjectedFieldSignature("com.registro.empleados.AsistenciaApplication.workManagerInitializer")
  public static void injectWorkManagerInitializer(AsistenciaApplication instance,
      WorkManagerInitializer workManagerInitializer) {
    instance.workManagerInitializer = workManagerInitializer;
  }
}
