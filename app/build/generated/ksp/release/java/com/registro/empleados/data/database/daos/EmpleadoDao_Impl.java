package com.registro.empleados.data.database.daos;

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
import com.registro.empleados.data.database.entities.Empleado;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
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

  private final EntityInsertionAdapter<Empleado> __insertionAdapterOfEmpleado;

  private final EntityDeletionOrUpdateAdapter<Empleado> __updateAdapterOfEmpleado;

  private final SharedSQLiteStatement __preparedStmtOfDarDeBajaEmpleado;

  public EmpleadoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEmpleado = new EntityInsertionAdapter<Empleado>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `empleados` (`legajo`,`nombre`,`apellido`,`fecha_ingreso`,`activo`,`fecha_creacion`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Empleado entity) {
        statement.bindString(1, entity.getLegajo());
        statement.bindString(2, entity.getNombre());
        statement.bindString(3, entity.getApellido());
        statement.bindLong(4, entity.getFechaIngreso());
        final int _tmp = entity.getActivo() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getFechaCreacion());
      }
    };
    this.__updateAdapterOfEmpleado = new EntityDeletionOrUpdateAdapter<Empleado>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `empleados` SET `legajo` = ?,`nombre` = ?,`apellido` = ?,`fecha_ingreso` = ?,`activo` = ?,`fecha_creacion` = ? WHERE `legajo` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Empleado entity) {
        statement.bindString(1, entity.getLegajo());
        statement.bindString(2, entity.getNombre());
        statement.bindString(3, entity.getApellido());
        statement.bindLong(4, entity.getFechaIngreso());
        final int _tmp = entity.getActivo() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getFechaCreacion());
        statement.bindString(7, entity.getLegajo());
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
  }

  @Override
  public Object insertEmpleado(final Empleado empleado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEmpleado.insert(empleado);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEmpleado(final Empleado empleado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEmpleado.handle(empleado);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Object getEmpleadoByLegajo(final String legajo,
      final Continuation<? super Empleado> $completion) {
    final String _sql = "SELECT * FROM empleados WHERE legajo = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Empleado>() {
      @Override
      @Nullable
      public Empleado call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfApellido = CursorUtil.getColumnIndexOrThrow(_cursor, "apellido");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_ingreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
          final Empleado _result;
          if (_cursor.moveToFirst()) {
            final String _tmpLegajo;
            _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpApellido;
            _tmpApellido = _cursor.getString(_cursorIndexOfApellido);
            final long _tmpFechaIngreso;
            _tmpFechaIngreso = _cursor.getLong(_cursorIndexOfFechaIngreso);
            final boolean _tmpActivo;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _result = new Empleado(_tmpLegajo,_tmpNombre,_tmpApellido,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion);
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
  public Flow<List<Empleado>> getAllEmpleadosActivos() {
    final String _sql = "SELECT * FROM empleados WHERE activo = 1 ORDER BY apellido ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<Empleado>>() {
      @Override
      @NonNull
      public List<Empleado> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfApellido = CursorUtil.getColumnIndexOrThrow(_cursor, "apellido");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_ingreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
          final List<Empleado> _result = new ArrayList<Empleado>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Empleado _item;
            final String _tmpLegajo;
            _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpApellido;
            _tmpApellido = _cursor.getString(_cursorIndexOfApellido);
            final long _tmpFechaIngreso;
            _tmpFechaIngreso = _cursor.getLong(_cursorIndexOfFechaIngreso);
            final boolean _tmpActivo;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _item = new Empleado(_tmpLegajo,_tmpNombre,_tmpApellido,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion);
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
  public Flow<List<Empleado>> getAllEmpleados() {
    final String _sql = "SELECT * FROM empleados ORDER BY apellido ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"empleados"}, new Callable<List<Empleado>>() {
      @Override
      @NonNull
      public List<Empleado> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLegajo = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfApellido = CursorUtil.getColumnIndexOrThrow(_cursor, "apellido");
          final int _cursorIndexOfFechaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_ingreso");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
          final List<Empleado> _result = new ArrayList<Empleado>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Empleado _item;
            final String _tmpLegajo;
            _tmpLegajo = _cursor.getString(_cursorIndexOfLegajo);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpApellido;
            _tmpApellido = _cursor.getString(_cursorIndexOfApellido);
            final long _tmpFechaIngreso;
            _tmpFechaIngreso = _cursor.getLong(_cursorIndexOfFechaIngreso);
            final boolean _tmpActivo;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _item = new Empleado(_tmpLegajo,_tmpNombre,_tmpApellido,_tmpFechaIngreso,_tmpActivo,_tmpFechaCreacion);
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
