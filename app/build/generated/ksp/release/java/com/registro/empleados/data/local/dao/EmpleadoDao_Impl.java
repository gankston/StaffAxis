package com.registro.empleados.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.registro.empleados.data.local.converter.LocalDateConverter;
import com.registro.empleados.data.local.entity.EmpleadoEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EmpleadoDao_Impl implements EmpleadoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EmpleadoEntity> __insertionAdapterOfEmpleadoEntity;

  private final LocalDateConverter __localDateConverter = new LocalDateConverter();

  private final EntityDeletionOrUpdateAdapter<EmpleadoEntity> __deletionAdapterOfEmpleadoEntity;

  private final EntityDeletionOrUpdateAdapter<EmpleadoEntity> __updateAdapterOfEmpleadoEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateEstadoEmpleado;

  private final SharedSQLiteStatement __preparedStmtOfDarDeBajaEmpleado;

  private final SharedSQLiteStatement __preparedStmtOfQuitarLegajoEmpleado;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllEmpleados;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByLegajo;

  private final SharedSQLiteStatement __preparedStmtOfLimpiarLegajosAutomaticos;

  private final SharedSQLiteStatement __preparedStmtOfCorregirEmpleadoPorNombre;

  public EmpleadoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEmpleadoEntity = new EntityInsertionAdapter<EmpleadoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `empleados` (`id`,`legajo`,`nombreCompleto`,`sector`,`fechaIngreso`,`activo`,`fechaCreacion`,`observacion`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmpleadoEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getLegajo() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLegajo());
        }
        statement.bindString(3, entity.getNombreCompleto());
        statement.bindString(4, entity.getSector());
        final String _tmp = __localDateConverter.fromLocalDate(entity.getFechaIngreso());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final int _tmp_1 = entity.getActivo() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        final String _tmp_2 = __localDateConverter.fromLocalDate(entity.getFechaCreacion());
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_2);
        }
        if (entity.getObservacion() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getObservacion());
        }
      }
    };
    this.__deletionAdapterOfEmpleadoEntity = new EntityDeletionOrUpdateAdapter<EmpleadoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `empleados` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmpleadoEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEmpleadoEntity = new EntityDeletionOrUpdateAdapter<EmpleadoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `empleados` SET `id` = ?,`legajo` = ?,`nombreCompleto` = ?,`sector` = ?,`fechaIngreso` = ?,`activo` = ?,`fechaCreacion` = ?,`observacion` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmpleadoEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getLegajo() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLegajo());
        }
        statement.bindString(3, entity.getNombreCompleto());
        statement.bindString(4, entity.getSector());
        final String _tmp = __localDateConverter.fromLocalDate(entity.getFechaIngreso());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final int _tmp_1 = entity.getActivo() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        final String _tmp_2 = __localDateConverter.fromLocalDate(entity.getFechaCreacion());
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_2);
        }
        if (entity.getObservacion() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getObservacion());
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateEstadoEmpleado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE empleados SET activo = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDarDeBajaEmpleado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE empleados SET activo = 0 WHERE legajo = ?";
        return _query;
      }
    };
    this.__preparedStmtOfQuitarLegajoEmpleado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE empleados SET legajo = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllEmpleados = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM empleados";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM empleados WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByLegajo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM empleados WHERE legajo = ?";
        return _query;
      }
    };
    this.__preparedStmtOfLimpiarLegajosAutomaticos = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE empleados SET legajo = NULL WHERE legajo LIKE 'AUTO_%'";
        return _query;
      }
    };
    this.__preparedStmtOfCorregirEmpleadoPorNombre = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE empleados SET nombreCompleto = ?, legajo = ? WHERE sector = ? AND nombreCompleto LIKE '%' || ? || '%' AND nombreCompleto LIKE '%' || ? || '%'";
        return _query;
      }
    };
  }

  @Override
  public Object insertEmpleado(final EmpleadoEntity empleado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEmpleadoEntity.insert(empleado);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEmpleado(final EmpleadoEntity empleado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEmpleadoEntity.handle(empleado);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEmpleado(final EmpleadoEntity empleado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEmpleadoEntity.handle(empleado);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEstadoEmpleado(final long id, final boolean activo,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateEstadoEmpleado.acquire();
        int _argIndex = 1;
        final int _tmp = activo ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateEstadoEmpleado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object darDeBajaEmpleado(final String legajo,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDarDeBajaEmpleado.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, legajo);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDarDeBajaEmpleado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object quitarLegajoEmpleado(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfQuitarLegajoEmpleado.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfQuitarLegajoEmpleado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllEmpleados(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllEmpleados.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllEmpleados.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByLegajo(final String legajo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByLegajo.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, legajo);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByLegajo.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object limpiarLegajosAutomaticos(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLimpiarLegajosAutomaticos.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfLimpiarLegajosAutomaticos.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object corregirEmpleadoPorNombre(final String sector, final String apellidoBusqueda,
      final String nombreBusqueda, final String nuevoNombre, final String nuevoLegajo,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCorregirEmpleadoPorNombre.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, nuevoNombre);
        _argIndex = 2;
        if (nuevoLegajo == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, nuevoLegajo);
        }
        _argIndex = 3;
        _stmt.bindString(_argIndex, sector);
        _argIndex = 4;
        _stmt.bindString(_argIndex, apellidoBusqueda);
        _argIndex = 5;
        _stmt.bindString(_argIndex, nombreBusqueda);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfCorregirEmpleadoPorNombre.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EmpleadoEntity>> getAllEmpleadosActivos() {
    final String _sql = "SELECT * FROM empleados WHERE activo = 1 ORDER BY nombreCompleto";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<EmpleadoEntity>>() {
      @Override
      @NonNull
      public List<EmpleadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final List<EmpleadoEntity> _result = new ArrayList<EmpleadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmpleadoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _item = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EmpleadoEntity>> getAllEmpleados() {
    final String _sql = "SELECT * FROM empleados ORDER BY nombreCompleto";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<EmpleadoEntity>>() {
      @Override
      @NonNull
      public List<EmpleadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final List<EmpleadoEntity> _result = new ArrayList<EmpleadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmpleadoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _item = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getEmpleadoByLegajo(final String legajo,
      final Continuation<? super EmpleadoEntity> $completion) {
    final String _sql = "SELECT * FROM empleados WHERE legajo = ? AND activo = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EmpleadoEntity>() {
      @Override
      @Nullable
      public EmpleadoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final EmpleadoEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _result = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getEmpleadoById(final long id,
      final Continuation<? super EmpleadoEntity> $completion) {
    final String _sql = "SELECT * FROM empleados WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EmpleadoEntity>() {
      @Override
      @Nullable
      public EmpleadoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final EmpleadoEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _result = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EmpleadoEntity>> getEmpleadosBySector(final String sector) {
    final String _sql = "SELECT * FROM empleados WHERE sector = ? AND activo = 1 ORDER BY nombreCompleto";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sector);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<EmpleadoEntity>>() {
      @Override
      @NonNull
      public List<EmpleadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final List<EmpleadoEntity> _result = new ArrayList<EmpleadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmpleadoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _item = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<String>> getSectores() {
    final String _sql = "SELECT DISTINCT sector FROM empleados WHERE activo = 1 ORDER BY sector";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EmpleadoEntity>> buscarEmpleadosPorNombre(final String nombre) {
    final String _sql = "SELECT * FROM empleados WHERE nombreCompleto LIKE '%' || ? || '%' AND activo = 1 ORDER BY nombreCompleto";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, nombre);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<EmpleadoEntity>>() {
      @Override
      @NonNull
      public List<EmpleadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final List<EmpleadoEntity> _result = new ArrayList<EmpleadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmpleadoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _item = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EmpleadoEntity>> buscarEmpleadosPorApellido(final String apellido) {
    final String _sql = "SELECT * FROM empleados WHERE nombreCompleto LIKE '%' || ? || '%' AND activo = 1 ORDER BY nombreCompleto";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, apellido);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<EmpleadoEntity>>() {
      @Override
      @NonNull
      public List<EmpleadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final List<EmpleadoEntity> _result = new ArrayList<EmpleadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmpleadoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _item = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EmpleadoEntity>> buscarEmpleadosPorNombreYApellido(final String nombre) {
    final String _sql = "SELECT * FROM empleados WHERE nombreCompleto LIKE '%' || ? || '%' AND activo = 1 ORDER BY nombreCompleto";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, nombre);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<EmpleadoEntity>>() {
      @Override
      @NonNull
      public List<EmpleadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final List<EmpleadoEntity> _result = new ArrayList<EmpleadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmpleadoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _item = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EmpleadoEntity>> buscarEmpleados(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM empleados \n"
            + "        WHERE activo = 1 \n"
            + "        AND (\n"
            + "            legajo LIKE ? \n"
            + "            OR LOWER(nombreCompleto) LIKE '%' || LOWER(?) || '%'\n"
            + "        )\n"
            + "        ORDER BY nombreCompleto ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<EmpleadoEntity>>() {
      @Override
      @NonNull
      public List<EmpleadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombreCompleto = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCompleto");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaIngreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfObservacion = CursorUtil.getColumnIndexOrThrow(_cursor, "observacion");
          final List<EmpleadoEntity> _result = new ArrayList<EmpleadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmpleadoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajo;
            if (_cursor.isNull(_cursorIndexOfLegajo)) {
              _tmpLegajo = null;
            } else {
              _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            }
            final String _tmpNombreCompleto;
            _tmpNombreCompleto = _cursor.getString(_cursorIndexOfNombreCompleto);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final LocalDate _tmpFechaIngreso;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaIngreso)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaIngreso);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaIngreso = _tmp_1;
            }
            final boolean _tmpActivo;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_2 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_4 = __localDateConverter.toLocalDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_4;
            }
            final String _tmpObservacion;
            if (_cursor.isNull(_cursorIndexOfObservacion)) {
              _tmpObservacion = null;
            } else {
              _tmpObservacion = _cursor.getString(_cursorIndexOfObservacion);
            }
            _item = new EmpleadoEntity(_tmpId,_tmpLegajo,_tmpNombreCompleto,_tmpSector,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion,_tmpObservacion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
