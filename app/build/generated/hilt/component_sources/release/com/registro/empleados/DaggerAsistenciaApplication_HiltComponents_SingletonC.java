package com.registro.empleados;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWrapper_WorkerFactoryModule;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.registro.empleados.data.export.CsvExportService;
import com.registro.empleados.data.export.ExcelExportService;
import com.registro.empleados.data.local.dao.AusenciaDao;
import com.registro.empleados.data.local.dao.EmpleadoDao;
import com.registro.empleados.data.local.database.AppDatabase;
import com.registro.empleados.data.local.preferences.AppPreferences;
import com.registro.empleados.data.repository.AusenciaRepositoryImpl;
import com.registro.empleados.data.repository.DiaLaboralRepositoryImpl;
import com.registro.empleados.data.repository.EmpleadoRepositoryImpl;
import com.registro.empleados.data.repository.HorasEmpleadoMesRepositoryImpl;
import com.registro.empleados.data.repository.RegistroAsistenciaRepositoryImpl;
import com.registro.empleados.di.AppModule;
import com.registro.empleados.di.AppModule_ProvideAppPreferencesFactory;
import com.registro.empleados.di.DatabaseModule;
import com.registro.empleados.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.registro.empleados.di.DatabaseModule_ProvideAusenciaDaoFactory;
import com.registro.empleados.di.DatabaseModule_ProvideEmpleadoDaoFactory;
import com.registro.empleados.di.ExportModule;
import com.registro.empleados.di.ExportModule_ProvideCsvExportServiceFactory;
import com.registro.empleados.di.ExportModule_ProvideExcelExportServiceFactory;
import com.registro.empleados.di.NetworkModule;
import com.registro.empleados.di.UseCaseModule;
import com.registro.empleados.di.UseCaseModule_ProvideBuscarEmpleadoSimpleUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideCargarEmpleadosPorSectorUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideCrearAusenciaUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideDarDeBajaEmpleadoUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideEmpleadoTieneAusenciaEnFechaUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideExportarRegistrosAsistenciaUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideGenerarDiasLaboralesBasicosUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideGetAllEmpleadosActivosUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideGetAusenciasByFechaUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideGetAusenciasByRangoUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideGetDiasLaboralesUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideGetPeriodoActualUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideGetRegistrosByRangoUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideInsertEmpleadoUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideLimpiarBaseDatosUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideTieneHorasCargadasHoyUseCaseFactory;
import com.registro.empleados.di.UseCaseModule_ProvideUpdateEmpleadoUseCaseFactory;
import com.registro.empleados.di.WorkerModule;
import com.registro.empleados.domain.repository.AusenciaRepository;
import com.registro.empleados.domain.usecase.LimpiarTodosLosRegistrosUseCase;
import com.registro.empleados.domain.usecase.asistencia.GetRegistrosByRangoUseCase;
import com.registro.empleados.domain.usecase.ausencia.ActualizarAusenciaUseCase;
import com.registro.empleados.domain.usecase.ausencia.CrearAusenciaUseCase;
import com.registro.empleados.domain.usecase.ausencia.EliminarAusenciaUseCase;
import com.registro.empleados.domain.usecase.ausencia.EmpleadoTieneAusenciaEnFechaUseCase;
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase;
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByRangoUseCase;
import com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase;
import com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase;
import com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase;
import com.registro.empleados.domain.usecase.database.CorregirEmpleadosDBDirectoUseCase;
import com.registro.empleados.domain.usecase.database.LimpiarBaseDatosUseCase;
import com.registro.empleados.domain.usecase.empleado.BuscarEmpleadoSimpleUseCase;
import com.registro.empleados.domain.usecase.empleado.CargarEmpleadosPorSectorUseCase;
import com.registro.empleados.domain.usecase.empleado.DarDeBajaEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase;
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase;
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase;
import com.registro.empleados.domain.usecase.empleado.UpdateEstadoEmpleadoUseCase;
import com.registro.empleados.domain.usecase.export.ExportarRegistrosAsistenciaUseCase;
import com.registro.empleados.presentation.MainActivity;
import com.registro.empleados.presentation.MainActivity_MembersInjector;
import com.registro.empleados.presentation.viewmodel.AusenciasViewModel;
import com.registro.empleados.presentation.viewmodel.AusenciasViewModel_HiltModules_KeyModule_ProvideFactory;
import com.registro.empleados.presentation.viewmodel.BienvenidaViewModel;
import com.registro.empleados.presentation.viewmodel.BienvenidaViewModel_HiltModules_KeyModule_ProvideFactory;
import com.registro.empleados.presentation.viewmodel.CalendarioViewModel;
import com.registro.empleados.presentation.viewmodel.CalendarioViewModel_HiltModules_KeyModule_ProvideFactory;
import com.registro.empleados.presentation.viewmodel.DashboardViewModel;
import com.registro.empleados.presentation.viewmodel.DashboardViewModel_HiltModules_KeyModule_ProvideFactory;
import com.registro.empleados.presentation.viewmodel.EmpleadosViewModel;
import com.registro.empleados.presentation.viewmodel.EmpleadosViewModel_HiltModules_KeyModule_ProvideFactory;
import com.registro.empleados.presentation.viewmodel.ReportesViewModel;
import com.registro.empleados.presentation.viewmodel.ReportesViewModel_HiltModules_KeyModule_ProvideFactory;
import com.registro.empleados.worker.WorkManagerInitializer;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.SetBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DaggerAsistenciaApplication_HiltComponents_SingletonC {
  private DaggerAsistenciaApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder appModule(AppModule appModule) {
      Preconditions.checkNotNull(appModule);
      return this;
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder databaseModule(DatabaseModule databaseModule) {
      Preconditions.checkNotNull(databaseModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder exportModule(ExportModule exportModule) {
      Preconditions.checkNotNull(exportModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder hiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule(
        HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule hiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule) {
      Preconditions.checkNotNull(hiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder hiltWrapper_WorkerFactoryModule(
        HiltWrapper_WorkerFactoryModule hiltWrapper_WorkerFactoryModule) {
      Preconditions.checkNotNull(hiltWrapper_WorkerFactoryModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder networkModule(NetworkModule networkModule) {
      Preconditions.checkNotNull(networkModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder useCaseModule(UseCaseModule useCaseModule) {
      Preconditions.checkNotNull(useCaseModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder workerModule(WorkerModule workerModule) {
      Preconditions.checkNotNull(workerModule);
      return this;
    }

    public AsistenciaApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements AsistenciaApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public AsistenciaApplication_HiltComponents.ActivityRetainedC build() {
      return new ActivityRetainedCImpl(singletonCImpl);
    }
  }

  private static final class ActivityCBuilder implements AsistenciaApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public AsistenciaApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements AsistenciaApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public AsistenciaApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements AsistenciaApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AsistenciaApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements AsistenciaApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AsistenciaApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements AsistenciaApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public AsistenciaApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements AsistenciaApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public AsistenciaApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends AsistenciaApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends AsistenciaApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends AsistenciaApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends AsistenciaApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Set<String> getViewModelKeys() {
      return SetBuilder.<String>newSetBuilder(6).add(AusenciasViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(BienvenidaViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(CalendarioViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(DashboardViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(EmpleadosViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ReportesViewModel_HiltModules_KeyModule_ProvideFactory.provide()).build();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectAppPreferences(instance, singletonCImpl.provideAppPreferencesProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends AsistenciaApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AusenciasViewModel> ausenciasViewModelProvider;

    private Provider<BienvenidaViewModel> bienvenidaViewModelProvider;

    private Provider<CalendarioViewModel> calendarioViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<EmpleadosViewModel> empleadosViewModelProvider;

    private Provider<ReportesViewModel> reportesViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private ActualizarAusenciaUseCase actualizarAusenciaUseCase() {
      return new ActualizarAusenciaUseCase(singletonCImpl.bindAusenciaRepositoryProvider.get());
    }

    private EliminarAusenciaUseCase eliminarAusenciaUseCase() {
      return new EliminarAusenciaUseCase(singletonCImpl.bindAusenciaRepositoryProvider.get());
    }

    private LimpiarTodosLosRegistrosUseCase limpiarTodosLosRegistrosUseCase() {
      return new LimpiarTodosLosRegistrosUseCase(singletonCImpl.registroAsistenciaRepositoryImplProvider.get());
    }

    private CorregirEmpleadosDBDirectoUseCase corregirEmpleadosDBDirectoUseCase() {
      return new CorregirEmpleadosDBDirectoUseCase(singletonCImpl.empleadoDao());
    }

    private UpdateEstadoEmpleadoUseCase updateEstadoEmpleadoUseCase() {
      return new UpdateEstadoEmpleadoUseCase(singletonCImpl.empleadoRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.ausenciasViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.bienvenidaViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.calendarioViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.empleadosViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.reportesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<String, Provider<ViewModel>> getHiltViewModelMap() {
      return MapBuilder.<String, Provider<ViewModel>>newMapBuilder(6).put("com.registro.empleados.presentation.viewmodel.AusenciasViewModel", ((Provider) ausenciasViewModelProvider)).put("com.registro.empleados.presentation.viewmodel.BienvenidaViewModel", ((Provider) bienvenidaViewModelProvider)).put("com.registro.empleados.presentation.viewmodel.CalendarioViewModel", ((Provider) calendarioViewModelProvider)).put("com.registro.empleados.presentation.viewmodel.DashboardViewModel", ((Provider) dashboardViewModelProvider)).put("com.registro.empleados.presentation.viewmodel.EmpleadosViewModel", ((Provider) empleadosViewModelProvider)).put("com.registro.empleados.presentation.viewmodel.ReportesViewModel", ((Provider) reportesViewModelProvider)).build();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.registro.empleados.presentation.viewmodel.AusenciasViewModel 
          return (T) new AusenciasViewModel(singletonCImpl.provideGetDiasLaboralesUseCaseProvider.get(), singletonCImpl.provideGetPeriodoActualUseCaseProvider.get(), singletonCImpl.provideGenerarDiasLaboralesBasicosUseCaseProvider.get(), singletonCImpl.provideGetAusenciasByRangoUseCaseProvider.get(), singletonCImpl.provideGetAusenciasByFechaUseCaseProvider.get(), singletonCImpl.provideCrearAusenciaUseCaseProvider.get(), viewModelCImpl.actualizarAusenciaUseCase(), viewModelCImpl.eliminarAusenciaUseCase(), singletonCImpl.provideGetAllEmpleadosActivosUseCaseProvider.get(), singletonCImpl.provideAppPreferencesProvider.get());

          case 1: // com.registro.empleados.presentation.viewmodel.BienvenidaViewModel 
          return (T) new BienvenidaViewModel(singletonCImpl.provideAppPreferencesProvider.get(), singletonCImpl.provideCargarEmpleadosPorSectorUseCaseProvider.get());

          case 2: // com.registro.empleados.presentation.viewmodel.CalendarioViewModel 
          return (T) new CalendarioViewModel(singletonCImpl.provideGetDiasLaboralesUseCaseProvider.get(), singletonCImpl.provideGetPeriodoActualUseCaseProvider.get(), singletonCImpl.provideGenerarDiasLaboralesBasicosUseCaseProvider.get(), singletonCImpl.registroAsistenciaRepositoryImplProvider.get());

          case 3: // com.registro.empleados.presentation.viewmodel.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.provideBuscarEmpleadoSimpleUseCaseProvider.get(), singletonCImpl.provideGetAllEmpleadosActivosUseCaseProvider.get(), singletonCImpl.provideInsertEmpleadoUseCaseProvider.get(), singletonCImpl.provideTieneHorasCargadasHoyUseCaseProvider.get(), singletonCImpl.provideEmpleadoTieneAusenciaEnFechaUseCaseProvider.get(), viewModelCImpl.limpiarTodosLosRegistrosUseCase(), viewModelCImpl.corregirEmpleadosDBDirectoUseCase(), singletonCImpl.provideUpdateEmpleadoUseCaseProvider.get(), singletonCImpl.provideDarDeBajaEmpleadoUseCaseProvider.get(), viewModelCImpl.updateEstadoEmpleadoUseCase(), singletonCImpl.registroAsistenciaRepositoryImplProvider.get(), singletonCImpl.horasEmpleadoMesRepositoryImplProvider.get(), singletonCImpl.provideAppPreferencesProvider.get());

          case 4: // com.registro.empleados.presentation.viewmodel.EmpleadosViewModel 
          return (T) new EmpleadosViewModel(singletonCImpl.provideGetAllEmpleadosActivosUseCaseProvider.get(), singletonCImpl.provideInsertEmpleadoUseCaseProvider.get(), singletonCImpl.provideTieneHorasCargadasHoyUseCaseProvider.get(), singletonCImpl.provideEmpleadoTieneAusenciaEnFechaUseCaseProvider.get(), singletonCImpl.provideAppPreferencesProvider.get());

          case 5: // com.registro.empleados.presentation.viewmodel.ReportesViewModel 
          return (T) new ReportesViewModel(singletonCImpl.provideGetRegistrosByRangoUseCaseProvider.get(), singletonCImpl.provideGetAllEmpleadosActivosUseCaseProvider.get(), singletonCImpl.provideExportarRegistrosAsistenciaUseCaseProvider.get(), singletonCImpl.provideLimpiarBaseDatosUseCaseProvider.get(), singletonCImpl.provideAppPreferencesProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends AsistenciaApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;

      initialize();

    }

    @SuppressWarnings("unchecked")
    private void initialize() {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends AsistenciaApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends AsistenciaApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<WorkManagerInitializer> workManagerInitializerProvider;

    private Provider<AppPreferences> provideAppPreferencesProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<DiaLaboralRepositoryImpl> diaLaboralRepositoryImplProvider;

    private Provider<GetDiasLaboralesUseCase> provideGetDiasLaboralesUseCaseProvider;

    private Provider<GetPeriodoActualUseCase> provideGetPeriodoActualUseCaseProvider;

    private Provider<GenerarDiasLaboralesBasicosUseCase> provideGenerarDiasLaboralesBasicosUseCaseProvider;

    private Provider<AusenciaRepositoryImpl> ausenciaRepositoryImplProvider;

    private Provider<AusenciaRepository> bindAusenciaRepositoryProvider;

    private Provider<GetAusenciasByRangoUseCase> provideGetAusenciasByRangoUseCaseProvider;

    private Provider<GetAusenciasByFechaUseCase> provideGetAusenciasByFechaUseCaseProvider;

    private Provider<CrearAusenciaUseCase> provideCrearAusenciaUseCaseProvider;

    private Provider<EmpleadoRepositoryImpl> empleadoRepositoryImplProvider;

    private Provider<GetAllEmpleadosActivosUseCase> provideGetAllEmpleadosActivosUseCaseProvider;

    private Provider<CargarEmpleadosPorSectorUseCase> provideCargarEmpleadosPorSectorUseCaseProvider;

    private Provider<RegistroAsistenciaRepositoryImpl> registroAsistenciaRepositoryImplProvider;

    private Provider<BuscarEmpleadoSimpleUseCase> provideBuscarEmpleadoSimpleUseCaseProvider;

    private Provider<InsertEmpleadoUseCase> provideInsertEmpleadoUseCaseProvider;

    private Provider<TieneHorasCargadasHoyUseCase> provideTieneHorasCargadasHoyUseCaseProvider;

    private Provider<EmpleadoTieneAusenciaEnFechaUseCase> provideEmpleadoTieneAusenciaEnFechaUseCaseProvider;

    private Provider<UpdateEmpleadoUseCase> provideUpdateEmpleadoUseCaseProvider;

    private Provider<DarDeBajaEmpleadoUseCase> provideDarDeBajaEmpleadoUseCaseProvider;

    private Provider<HorasEmpleadoMesRepositoryImpl> horasEmpleadoMesRepositoryImplProvider;

    private Provider<GetRegistrosByRangoUseCase> provideGetRegistrosByRangoUseCaseProvider;

    private Provider<ExcelExportService> provideExcelExportServiceProvider;

    private Provider<CsvExportService> provideCsvExportServiceProvider;

    private Provider<ExportarRegistrosAsistenciaUseCase> provideExportarRegistrosAsistenciaUseCaseProvider;

    private Provider<LimpiarBaseDatosUseCase> provideLimpiarBaseDatosUseCaseProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private AusenciaDao ausenciaDao() {
      return DatabaseModule_ProvideAusenciaDaoFactory.provideAusenciaDao(provideAppDatabaseProvider.get());
    }

    private EmpleadoDao empleadoDao() {
      return DatabaseModule_ProvideEmpleadoDaoFactory.provideEmpleadoDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.workManagerInitializerProvider = DoubleCheck.provider(new SwitchingProvider<WorkManagerInitializer>(singletonCImpl, 0));
      this.provideAppPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<AppPreferences>(singletonCImpl, 1));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 4));
      this.diaLaboralRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<DiaLaboralRepositoryImpl>(singletonCImpl, 3));
      this.provideGetDiasLaboralesUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<GetDiasLaboralesUseCase>(singletonCImpl, 2));
      this.provideGetPeriodoActualUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<GetPeriodoActualUseCase>(singletonCImpl, 5));
      this.provideGenerarDiasLaboralesBasicosUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<GenerarDiasLaboralesBasicosUseCase>(singletonCImpl, 6));
      this.ausenciaRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.bindAusenciaRepositoryProvider = DoubleCheck.provider((Provider) ausenciaRepositoryImplProvider);
      this.provideGetAusenciasByRangoUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<GetAusenciasByRangoUseCase>(singletonCImpl, 7));
      this.provideGetAusenciasByFechaUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<GetAusenciasByFechaUseCase>(singletonCImpl, 9));
      this.provideCrearAusenciaUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<CrearAusenciaUseCase>(singletonCImpl, 10));
      this.empleadoRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<EmpleadoRepositoryImpl>(singletonCImpl, 12));
      this.provideGetAllEmpleadosActivosUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<GetAllEmpleadosActivosUseCase>(singletonCImpl, 11));
      this.provideCargarEmpleadosPorSectorUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<CargarEmpleadosPorSectorUseCase>(singletonCImpl, 13));
      this.registroAsistenciaRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<RegistroAsistenciaRepositoryImpl>(singletonCImpl, 14));
      this.provideBuscarEmpleadoSimpleUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<BuscarEmpleadoSimpleUseCase>(singletonCImpl, 15));
      this.provideInsertEmpleadoUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<InsertEmpleadoUseCase>(singletonCImpl, 16));
      this.provideTieneHorasCargadasHoyUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<TieneHorasCargadasHoyUseCase>(singletonCImpl, 17));
      this.provideEmpleadoTieneAusenciaEnFechaUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<EmpleadoTieneAusenciaEnFechaUseCase>(singletonCImpl, 18));
      this.provideUpdateEmpleadoUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<UpdateEmpleadoUseCase>(singletonCImpl, 19));
      this.provideDarDeBajaEmpleadoUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<DarDeBajaEmpleadoUseCase>(singletonCImpl, 20));
      this.horasEmpleadoMesRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<HorasEmpleadoMesRepositoryImpl>(singletonCImpl, 21));
      this.provideGetRegistrosByRangoUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<GetRegistrosByRangoUseCase>(singletonCImpl, 22));
      this.provideExcelExportServiceProvider = DoubleCheck.provider(new SwitchingProvider<ExcelExportService>(singletonCImpl, 24));
      this.provideCsvExportServiceProvider = DoubleCheck.provider(new SwitchingProvider<CsvExportService>(singletonCImpl, 25));
      this.provideExportarRegistrosAsistenciaUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<ExportarRegistrosAsistenciaUseCase>(singletonCImpl, 23));
      this.provideLimpiarBaseDatosUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<LimpiarBaseDatosUseCase>(singletonCImpl, 26));
    }

    @Override
    public void injectAsistenciaApplication(AsistenciaApplication asistenciaApplication) {
      injectAsistenciaApplication2(asistenciaApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private AsistenciaApplication injectAsistenciaApplication2(AsistenciaApplication instance) {
      AsistenciaApplication_MembersInjector.injectWorkManagerInitializer(instance, workManagerInitializerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.registro.empleados.worker.WorkManagerInitializer 
          return (T) new WorkManagerInitializer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.registro.empleados.data.local.preferences.AppPreferences 
          return (T) AppModule_ProvideAppPreferencesFactory.provideAppPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase 
          return (T) UseCaseModule_ProvideGetDiasLaboralesUseCaseFactory.provideGetDiasLaboralesUseCase(singletonCImpl.diaLaboralRepositoryImplProvider.get());

          case 3: // com.registro.empleados.data.repository.DiaLaboralRepositoryImpl 
          return (T) new DiaLaboralRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get());

          case 4: // com.registro.empleados.data.local.database.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase 
          return (T) UseCaseModule_ProvideGetPeriodoActualUseCaseFactory.provideGetPeriodoActualUseCase();

          case 6: // com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase 
          return (T) UseCaseModule_ProvideGenerarDiasLaboralesBasicosUseCaseFactory.provideGenerarDiasLaboralesBasicosUseCase(singletonCImpl.diaLaboralRepositoryImplProvider.get());

          case 7: // com.registro.empleados.domain.usecase.ausencia.GetAusenciasByRangoUseCase 
          return (T) UseCaseModule_ProvideGetAusenciasByRangoUseCaseFactory.provideGetAusenciasByRangoUseCase(singletonCImpl.bindAusenciaRepositoryProvider.get());

          case 8: // com.registro.empleados.data.repository.AusenciaRepositoryImpl 
          return (T) new AusenciaRepositoryImpl(singletonCImpl.ausenciaDao());

          case 9: // com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase 
          return (T) UseCaseModule_ProvideGetAusenciasByFechaUseCaseFactory.provideGetAusenciasByFechaUseCase(singletonCImpl.bindAusenciaRepositoryProvider.get());

          case 10: // com.registro.empleados.domain.usecase.ausencia.CrearAusenciaUseCase 
          return (T) UseCaseModule_ProvideCrearAusenciaUseCaseFactory.provideCrearAusenciaUseCase(singletonCImpl.bindAusenciaRepositoryProvider.get());

          case 11: // com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase 
          return (T) UseCaseModule_ProvideGetAllEmpleadosActivosUseCaseFactory.provideGetAllEmpleadosActivosUseCase(singletonCImpl.empleadoRepositoryImplProvider.get());

          case 12: // com.registro.empleados.data.repository.EmpleadoRepositoryImpl 
          return (T) new EmpleadoRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get());

          case 13: // com.registro.empleados.domain.usecase.empleado.CargarEmpleadosPorSectorUseCase 
          return (T) UseCaseModule_ProvideCargarEmpleadosPorSectorUseCaseFactory.provideCargarEmpleadosPorSectorUseCase(singletonCImpl.empleadoRepositoryImplProvider.get());

          case 14: // com.registro.empleados.data.repository.RegistroAsistenciaRepositoryImpl 
          return (T) new RegistroAsistenciaRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get());

          case 15: // com.registro.empleados.domain.usecase.empleado.BuscarEmpleadoSimpleUseCase 
          return (T) UseCaseModule_ProvideBuscarEmpleadoSimpleUseCaseFactory.provideBuscarEmpleadoSimpleUseCase(singletonCImpl.empleadoRepositoryImplProvider.get());

          case 16: // com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase 
          return (T) UseCaseModule_ProvideInsertEmpleadoUseCaseFactory.provideInsertEmpleadoUseCase(singletonCImpl.empleadoRepositoryImplProvider.get());

          case 17: // com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase 
          return (T) UseCaseModule_ProvideTieneHorasCargadasHoyUseCaseFactory.provideTieneHorasCargadasHoyUseCase(singletonCImpl.registroAsistenciaRepositoryImplProvider.get());

          case 18: // com.registro.empleados.domain.usecase.ausencia.EmpleadoTieneAusenciaEnFechaUseCase 
          return (T) UseCaseModule_ProvideEmpleadoTieneAusenciaEnFechaUseCaseFactory.provideEmpleadoTieneAusenciaEnFechaUseCase(singletonCImpl.bindAusenciaRepositoryProvider.get());

          case 19: // com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase 
          return (T) UseCaseModule_ProvideUpdateEmpleadoUseCaseFactory.provideUpdateEmpleadoUseCase(singletonCImpl.empleadoRepositoryImplProvider.get());

          case 20: // com.registro.empleados.domain.usecase.empleado.DarDeBajaEmpleadoUseCase 
          return (T) UseCaseModule_ProvideDarDeBajaEmpleadoUseCaseFactory.provideDarDeBajaEmpleadoUseCase(singletonCImpl.empleadoRepositoryImplProvider.get());

          case 21: // com.registro.empleados.data.repository.HorasEmpleadoMesRepositoryImpl 
          return (T) new HorasEmpleadoMesRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get());

          case 22: // com.registro.empleados.domain.usecase.asistencia.GetRegistrosByRangoUseCase 
          return (T) UseCaseModule_ProvideGetRegistrosByRangoUseCaseFactory.provideGetRegistrosByRangoUseCase(singletonCImpl.registroAsistenciaRepositoryImplProvider.get());

          case 23: // com.registro.empleados.domain.usecase.export.ExportarRegistrosAsistenciaUseCase 
          return (T) UseCaseModule_ProvideExportarRegistrosAsistenciaUseCaseFactory.provideExportarRegistrosAsistenciaUseCase(singletonCImpl.provideExcelExportServiceProvider.get(), singletonCImpl.provideCsvExportServiceProvider.get(), singletonCImpl.empleadoRepositoryImplProvider.get(), singletonCImpl.registroAsistenciaRepositoryImplProvider.get(), singletonCImpl.bindAusenciaRepositoryProvider.get());

          case 24: // com.registro.empleados.data.export.ExcelExportService 
          return (T) ExportModule_ProvideExcelExportServiceFactory.provideExcelExportService(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 25: // com.registro.empleados.data.export.CsvExportService 
          return (T) ExportModule_ProvideCsvExportServiceFactory.provideCsvExportService(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 26: // com.registro.empleados.domain.usecase.database.LimpiarBaseDatosUseCase 
          return (T) UseCaseModule_ProvideLimpiarBaseDatosUseCaseFactory.provideLimpiarBaseDatosUseCase(singletonCImpl.empleadoRepositoryImplProvider.get(), singletonCImpl.registroAsistenciaRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
