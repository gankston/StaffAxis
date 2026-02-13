package com.registro.empleados.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.registro.empleados.data.database.daos.DiaLaboralDao;
import com.registro.empleados.data.database.daos.DiaLaboralDao_AppDatabase_1_Impl;
import com.registro.empleados.data.local.dao.AusenciaDao;
import com.registro.empleados.data.local.dao.AusenciaDao_Impl;
import com.registro.empleados.data.local.dao.EmpleadoDao;
import com.registro.empleados.data.local.dao.EmpleadoDao_Impl;
import com.registro.empleados.data.local.dao.HorasEmpleadoMesDao;
import com.registro.empleados.data.local.dao.HorasEmpleadoMesDao_Impl;
import com.registro.empleados.data.local.dao.RegistroAsistenciaDao;
import com.registro.empleados.data.local.dao.RegistroAsistenciaDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile EmpleadoDao _empleadoDao;

  private volatile RegistroAsistenciaDao _registroAsistenciaDao;

  private volatile DiaLaboralDao _diaLaboralDao;

  private volatile HorasEmpleadoMesDao _horasEmpleadoMesDao;

  private volatile AusenciaDao _ausenciaDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(15) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `empleados` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `legajo` TEXT, `nombreCompleto` TEXT NOT NULL, `sector` TEXT NOT NULL, `fechaIngreso` TEXT NOT NULL, `activo` INTEGER NOT NULL, `fechaCreacion` TEXT NOT NULL, `observacion` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `registros_asistencia` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id_empleado` INTEGER, `legajo_empleado` TEXT, `fecha` TEXT NOT NULL, `horas_trabajadas` INTEGER NOT NULL, `observaciones` TEXT, `fecha_registro` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `dias_laborales` (`fecha` TEXT NOT NULL, `es_laboral` INTEGER NOT NULL, `tipo_dia` TEXT NOT NULL, `descripcion` TEXT, `fecha_actualizacion` INTEGER NOT NULL, PRIMARY KEY(`fecha`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `horas_empleado_mes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `legajoEmpleado` TEXT NOT NULL, `año` INTEGER NOT NULL, `mes` INTEGER NOT NULL, `totalHoras` REAL NOT NULL, `diasTrabajados` INTEGER NOT NULL, `promedioDiario` REAL NOT NULL, `ultimaActualizacion` TEXT NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_horas_empleado_mes_legajoEmpleado` ON `horas_empleado_mes` (`legajoEmpleado`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ausencia_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `legajoEmpleado` TEXT NOT NULL, `nombreEmpleado` TEXT NOT NULL, `fechaInicio` TEXT NOT NULL, `fechaFin` TEXT NOT NULL, `motivo` TEXT, `fechaCreacion` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7dfb6053404d4f999c9cf703b62c6ce0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `empleados`");
        db.execSQL("DROP TABLE IF EXISTS `registros_asistencia`");
        db.execSQL("DROP TABLE IF EXISTS `dias_laborales`");
        db.execSQL("DROP TABLE IF EXISTS `horas_empleado_mes`");
        db.execSQL("DROP TABLE IF EXISTS `ausencia_table`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsEmpleados = new HashMap<String, TableInfo.Column>(8);
        _columnsEmpleados.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("legajo", new TableInfo.Column("legajo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("nombreCompleto", new TableInfo.Column("nombreCompleto", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("sector", new TableInfo.Column("sector", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("fechaIngreso", new TableInfo.Column("fechaIngreso", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("activo", new TableInfo.Column("activo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("fechaCreacion", new TableInfo.Column("fechaCreacion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("observacion", new TableInfo.Column("observacion", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEmpleados = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEmpleados = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEmpleados = new TableInfo("empleados", _columnsEmpleados, _foreignKeysEmpleados, _indicesEmpleados);
        final TableInfo _existingEmpleados = TableInfo.read(db, "empleados");
        if (!_infoEmpleados.equals(_existingEmpleados)) {
          return new RoomOpenHelper.ValidationResult(false, "empleados(com.registro.empleados.data.local.entity.EmpleadoEntity).\n"
                  + " Expected:\n" + _infoEmpleados + "\n"
                  + " Found:\n" + _existingEmpleados);
        }
        final HashMap<String, TableInfo.Column> _columnsRegistrosAsistencia = new HashMap<String, TableInfo.Column>(7);
        _columnsRegistrosAsistencia.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("id_empleado", new TableInfo.Column("id_empleado", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("legajo_empleado", new TableInfo.Column("legajo_empleado", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("fecha", new TableInfo.Column("fecha", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("horas_trabajadas", new TableInfo.Column("horas_trabajadas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("observaciones", new TableInfo.Column("observaciones", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("fecha_registro", new TableInfo.Column("fecha_registro", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRegistrosAsistencia = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRegistrosAsistencia = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRegistrosAsistencia = new TableInfo("registros_asistencia", _columnsRegistrosAsistencia, _foreignKeysRegistrosAsistencia, _indicesRegistrosAsistencia);
        final TableInfo _existingRegistrosAsistencia = TableInfo.read(db, "registros_asistencia");
        if (!_infoRegistrosAsistencia.equals(_existingRegistrosAsistencia)) {
          return new RoomOpenHelper.ValidationResult(false, "registros_asistencia(com.registro.empleados.data.local.entity.RegistroAsistenciaEntity).\n"
                  + " Expected:\n" + _infoRegistrosAsistencia + "\n"
                  + " Found:\n" + _existingRegistrosAsistencia);
        }
        final HashMap<String, TableInfo.Column> _columnsDiasLaborales = new HashMap<String, TableInfo.Column>(5);
        _columnsDiasLaborales.put("fecha", new TableInfo.Column("fecha", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiasLaborales.put("es_laboral", new TableInfo.Column("es_laboral", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiasLaborales.put("tipo_dia", new TableInfo.Column("tipo_dia", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiasLaborales.put("descripcion", new TableInfo.Column("descripcion", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiasLaborales.put("fecha_actualizacion", new TableInfo.Column("fecha_actualizacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDiasLaborales = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDiasLaborales = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDiasLaborales = new TableInfo("dias_laborales", _columnsDiasLaborales, _foreignKeysDiasLaborales, _indicesDiasLaborales);
        final TableInfo _existingDiasLaborales = TableInfo.read(db, "dias_laborales");
        if (!_infoDiasLaborales.equals(_existingDiasLaborales)) {
          return new RoomOpenHelper.ValidationResult(false, "dias_laborales(com.registro.empleados.data.database.entities.DiaLaboral).\n"
                  + " Expected:\n" + _infoDiasLaborales + "\n"
                  + " Found:\n" + _existingDiasLaborales);
        }
        final HashMap<String, TableInfo.Column> _columnsHorasEmpleadoMes = new HashMap<String, TableInfo.Column>(8);
        _columnsHorasEmpleadoMes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHorasEmpleadoMes.put("legajoEmpleado", new TableInfo.Column("legajoEmpleado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHorasEmpleadoMes.put("año", new TableInfo.Column("año", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHorasEmpleadoMes.put("mes", new TableInfo.Column("mes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHorasEmpleadoMes.put("totalHoras", new TableInfo.Column("totalHoras", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHorasEmpleadoMes.put("diasTrabajados", new TableInfo.Column("diasTrabajados", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHorasEmpleadoMes.put("promedioDiario", new TableInfo.Column("promedioDiario", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHorasEmpleadoMes.put("ultimaActualizacion", new TableInfo.Column("ultimaActualizacion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHorasEmpleadoMes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHorasEmpleadoMes = new HashSet<TableInfo.Index>(1);
        _indicesHorasEmpleadoMes.add(new TableInfo.Index("index_horas_empleado_mes_legajoEmpleado", false, Arrays.asList("legajoEmpleado"), Arrays.asList("ASC")));
        final TableInfo _infoHorasEmpleadoMes = new TableInfo("horas_empleado_mes", _columnsHorasEmpleadoMes, _foreignKeysHorasEmpleadoMes, _indicesHorasEmpleadoMes);
        final TableInfo _existingHorasEmpleadoMes = TableInfo.read(db, "horas_empleado_mes");
        if (!_infoHorasEmpleadoMes.equals(_existingHorasEmpleadoMes)) {
          return new RoomOpenHelper.ValidationResult(false, "horas_empleado_mes(com.registro.empleados.data.local.entity.HorasEmpleadoMesEntity).\n"
                  + " Expected:\n" + _infoHorasEmpleadoMes + "\n"
                  + " Found:\n" + _existingHorasEmpleadoMes);
        }
        final HashMap<String, TableInfo.Column> _columnsAusenciaTable = new HashMap<String, TableInfo.Column>(7);
        _columnsAusenciaTable.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAusenciaTable.put("legajoEmpleado", new TableInfo.Column("legajoEmpleado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAusenciaTable.put("nombreEmpleado", new TableInfo.Column("nombreEmpleado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAusenciaTable.put("fechaInicio", new TableInfo.Column("fechaInicio", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAusenciaTable.put("fechaFin", new TableInfo.Column("fechaFin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAusenciaTable.put("motivo", new TableInfo.Column("motivo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAusenciaTable.put("fechaCreacion", new TableInfo.Column("fechaCreacion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAusenciaTable = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAusenciaTable = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAusenciaTable = new TableInfo("ausencia_table", _columnsAusenciaTable, _foreignKeysAusenciaTable, _indicesAusenciaTable);
        final TableInfo _existingAusenciaTable = TableInfo.read(db, "ausencia_table");
        if (!_infoAusenciaTable.equals(_existingAusenciaTable)) {
          return new RoomOpenHelper.ValidationResult(false, "ausencia_table(com.registro.empleados.data.local.entity.AusenciaEntity).\n"
                  + " Expected:\n" + _infoAusenciaTable + "\n"
                  + " Found:\n" + _existingAusenciaTable);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "7dfb6053404d4f999c9cf703b62c6ce0", "8901f71be4c05c7a54fc53479c5a9390");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "empleados","registros_asistencia","dias_laborales","horas_empleado_mes","ausencia_table");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `empleados`");
      _db.execSQL("DELETE FROM `registros_asistencia`");
      _db.execSQL("DELETE FROM `dias_laborales`");
      _db.execSQL("DELETE FROM `horas_empleado_mes`");
      _db.execSQL("DELETE FROM `ausencia_table`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(EmpleadoDao.class, EmpleadoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RegistroAsistenciaDao.class, RegistroAsistenciaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DiaLaboralDao.class, DiaLaboralDao_AppDatabase_1_Impl.getRequiredConverters());
    _typeConvertersMap.put(HorasEmpleadoMesDao.class, HorasEmpleadoMesDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AusenciaDao.class, AusenciaDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public EmpleadoDao empleadoDao() {
    if (_empleadoDao != null) {
      return _empleadoDao;
    } else {
      synchronized(this) {
        if(_empleadoDao == null) {
          _empleadoDao = new EmpleadoDao_Impl(this);
        }
        return _empleadoDao;
      }
    }
  }

  @Override
  public RegistroAsistenciaDao registroAsistenciaDao() {
    if (_registroAsistenciaDao != null) {
      return _registroAsistenciaDao;
    } else {
      synchronized(this) {
        if(_registroAsistenciaDao == null) {
          _registroAsistenciaDao = new RegistroAsistenciaDao_Impl(this);
        }
        return _registroAsistenciaDao;
      }
    }
  }

  @Override
  public DiaLaboralDao diaLaboralDao() {
    if (_diaLaboralDao != null) {
      return _diaLaboralDao;
    } else {
      synchronized(this) {
        if(_diaLaboralDao == null) {
          _diaLaboralDao = new DiaLaboralDao_AppDatabase_1_Impl(this);
        }
        return _diaLaboralDao;
      }
    }
  }

  @Override
  public HorasEmpleadoMesDao horasEmpleadoMesDao() {
    if (_horasEmpleadoMesDao != null) {
      return _horasEmpleadoMesDao;
    } else {
      synchronized(this) {
        if(_horasEmpleadoMesDao == null) {
          _horasEmpleadoMesDao = new HorasEmpleadoMesDao_Impl(this);
        }
        return _horasEmpleadoMesDao;
      }
    }
  }

  @Override
  public AusenciaDao ausenciaDao() {
    if (_ausenciaDao != null) {
      return _ausenciaDao;
    } else {
      synchronized(this) {
        if(_ausenciaDao == null) {
          _ausenciaDao = new AusenciaDao_Impl(this);
        }
        return _ausenciaDao;
      }
    }
  }
}
