package com.registro.empleados;

import androidx.hilt.work.HiltWorkerFactory;
import com.registro.empleados.data.device.DeviceIdentityManager;
import com.registro.empleados.data.remote.api.SectorsApiService;
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
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<WorkManagerInitializer> workManagerInitializerProvider;

  private final Provider<DeviceIdentityManager> deviceIdentityManagerProvider;

  private final Provider<SectorsApiService> sectorsApiServiceProvider;

  public AsistenciaApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<WorkManagerInitializer> workManagerInitializerProvider,
      Provider<DeviceIdentityManager> deviceIdentityManagerProvider,
      Provider<SectorsApiService> sectorsApiServiceProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.workManagerInitializerProvider = workManagerInitializerProvider;
    this.deviceIdentityManagerProvider = deviceIdentityManagerProvider;
    this.sectorsApiServiceProvider = sectorsApiServiceProvider;
  }

  public static MembersInjector<AsistenciaApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<WorkManagerInitializer> workManagerInitializerProvider,
      Provider<DeviceIdentityManager> deviceIdentityManagerProvider,
      Provider<SectorsApiService> sectorsApiServiceProvider) {
    return new AsistenciaApplication_MembersInjector(workerFactoryProvider, workManagerInitializerProvider, deviceIdentityManagerProvider, sectorsApiServiceProvider);
  }

  @Override
  public void injectMembers(AsistenciaApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectWorkManagerInitializer(instance, workManagerInitializerProvider.get());
    injectDeviceIdentityManager(instance, deviceIdentityManagerProvider.get());
    injectSectorsApiService(instance, sectorsApiServiceProvider.get());
  }

  @InjectedFieldSignature("com.registro.empleados.AsistenciaApplication.workerFactory")
  public static void injectWorkerFactory(AsistenciaApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.registro.empleados.AsistenciaApplication.workManagerInitializer")
  public static void injectWorkManagerInitializer(AsistenciaApplication instance,
      WorkManagerInitializer workManagerInitializer) {
    instance.workManagerInitializer = workManagerInitializer;
  }

  @InjectedFieldSignature("com.registro.empleados.AsistenciaApplication.deviceIdentityManager")
  public static void injectDeviceIdentityManager(AsistenciaApplication instance,
      DeviceIdentityManager deviceIdentityManager) {
    instance.deviceIdentityManager = deviceIdentityManager;
  }

  @InjectedFieldSignature("com.registro.empleados.AsistenciaApplication.sectorsApiService")
  public static void injectSectorsApiService(AsistenciaApplication instance,
      SectorsApiService sectorsApiService) {
    instance.sectorsApiService = sectorsApiService;
  }
}
