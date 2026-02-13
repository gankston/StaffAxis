package com.registro.empleados.data.database;

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
import com.registro.empleados.data.database.daos.DiaLaboralDao_AppDatabase_0_Impl;
import com.registro.empleados.data.database.daos.EmpleadoDao;
import com.registro.empleados.data.database.daos.EmpleadoDao_Impl;
import com.registro.empleados.data.database.daos.RegistroAsistenciaDao;
import com.registro.empleados.data.database.daos.RegistroAsistenciaDao_Impl;
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

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `empleados` (`legajo` TEXT NOT NULL, `nombre` TEXT NOT NULL, `apellido` TEXT NOT NULL, `fecha_ingreso` INTEGER NOT NULL, `activo` INTEGER NOT NULL, `fecha_creacion` INTEGER NOT NULL, PRIMARY KEY(`legajo`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `registros_asistencia` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `legajo_empleado` TEXT NOT NULL, `fecha` TEXT NOT NULL, `hora_entrada` INTEGER, `hora_salida` INTEGER, `horas_trabajadas` REAL, `observaciones` TEXT, FOREIGN KEY(`legajo_empleado`) REFERENCES `empleados`(`legajo`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_registros_asistencia_legajo_empleado` ON `registros_asistencia` (`legajo_empleado`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `dias_laborales` (`fecha` TEXT NOT NULL, `es_laboral` INTEGER NOT NULL, `tipo_dia` TEXT NOT NULL, `descripcion` TEXT, `fecha_actualizacion` INTEGER NOT NULL, PRIMARY KEY(`fecha`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '736f01ec7d9b12f17e902838b6397a5b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `empleados`");
        db.execSQL("DROP TABLE IF EXISTS `registros_asistencia`");
        db.execSQL("DROP TABLE IF EXISTS `dias_laborales`");
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
        db.execSQL("PRAGMA foreign_keys = ON");
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
        final HashMap<String, TableInfo.Column> _columnsEmpleados = new HashMap<String, TableInfo.Column>(6);
        _columnsEmpleados.put("legajo", new TableInfo.Column("legajo", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("apellido", new TableInfo.Column("apellido", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("fecha_ingreso", new TableInfo.Column("fecha_ingreso", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("activo", new TableInfo.Column("activo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmpleados.put("fecha_creacion", new TableInfo.Column("fecha_creacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEmpleados = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEmpleados = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEmpleados = new TableInfo("empleados", _columnsEmpleados, _foreignKeysEmpleados, _indicesEmpleados);
        final TableInfo _existingEmpleados = TableInfo.read(db, "empleados");
        if (!_infoEmpleados.equals(_existingEmpleados)) {
          return new RoomOpenHelper.ValidationResult(false, "empleados(com.registro.empleados.data.database.entities.Empleado).\n"
                  + " Expected:\n" + _infoEmpleados + "\n"
                  + " Found:\n" + _existingEmpleados);
        }
        final HashMap<String, TableInfo.Column> _columnsRegistrosAsistencia = new HashMap<String, TableInfo.Column>(7);
        _columnsRegistrosAsistencia.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("legajo_empleado", new TableInfo.Column("legajo_empleado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("fecha", new TableInfo.Column("fecha", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("hora_entrada", new TableInfo.Column("hora_entrada", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("hora_salida", new TableInfo.Column("hora_salida", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("horas_trabajadas", new TableInfo.Column("horas_trabajadas", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosAsistencia.put("observaciones", new TableInfo.Column("observaciones", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRegistrosAsistencia = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRegistrosAsistencia.add(new TableInfo.ForeignKey("empleados", "CASCADE", "NO ACTION", Arrays.asList("legajo_empleado"), Arrays.asList("legajo")));
        final HashSet<TableInfo.Index> _indicesRegistrosAsistencia = new HashSet<TableInfo.Index>(1);
        _indicesRegistrosAsistencia.add(new TableInfo.Index("index_registros_asistencia_legajo_empleado", false, Arrays.asList("legajo_empleado"), Arrays.asList("ASC")));
        final TableInfo _infoRegistrosAsistencia = new TableInfo("registros_asistencia", _columnsRegistrosAsistencia, _foreignKeysRegistrosAsistencia, _indicesRegistrosAsistencia);
        final TableInfo _existingRegistrosAsistencia = TableInfo.read(db, "registros_asistencia");
        if (!_infoRegistrosAsistencia.equals(_existingRegistrosAsistencia)) {
          return new RoomOpenHelper.ValidationResult(false, "registros_asistencia(com.registro.empleados.data.database.entities.RegistroAsistencia).\n"
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
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "736f01ec7d9b12f17e902838b6397a5b", "3a56c5caadd4f1da2acaa21764bbf432");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "empleados","registros_asistencia","dias_laborales");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `empleados`");
      _db.execSQL("DELETE FROM `registros_asistencia`");
      _db.execSQL("DELETE FROM `dias_laborales`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
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
    _typeConvertersMap.put(DiaLaboralDao.class, DiaLaboralDao_AppDatabase_0_Impl.getRequiredConverters());
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
          _diaLaboralDao = new DiaLaboralDao_AppDatabase_0_Impl(this);
        }
        return _diaLaboralDao;
      }
    }
  }
}
