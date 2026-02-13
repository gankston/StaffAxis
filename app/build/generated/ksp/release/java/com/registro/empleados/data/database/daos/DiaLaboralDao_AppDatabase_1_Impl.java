package com.registro.empleados.data.database.daos;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.registro.empleados.data.database.entities.DiaLaboral;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DiaLaboralDao_AppDatabase_1_Impl implements DiaLaboralDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DiaLaboral> __insertionAdapterOfDiaLaboral;

  private final SharedSQLiteStatement __preparedStmtOfEliminarDiasAntiguos;

  public DiaLaboralDao_AppDatabase_1_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDiaLaboral = new EntityInsertionAdapter<DiaLaboral>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `dias_laborales` (`fecha`,`es_laboral`,`tipo_dia`,`descripcion`,`fecha_actualizacion`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiaLaboral entity) {
        statement.bindString(1, entity.getFecha());
        final int _tmp = entity.getEsLaboral() ? 1 : 0;
        statement.bindLong(2, _tmp);
        statement.bindString(3, entity.getTipoDia());
        if (entity.getDescripcion() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescripcion());
        }
        statement.bindLong(5, entity.getFechaActualizacion());
      }
    };
    this.__preparedStmtOfEliminarDiasAntiguos = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM dias_laborales WHERE fecha < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDias(final List<DiaLaboral> dias,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDiaLaboral.insert(dias);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object eliminarDiasAntiguos(final String fechaLimite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEliminarDiasAntiguos.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, fechaLimite);
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
          __preparedStmtOfEliminarDiasAntiguos.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getDiasByRango(final String fechaInicio, final String fechaFin,
      final Continuation<? super List<DiaLaboral>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM dias_laborales \n"
            + "        WHERE fecha BETWEEN ? AND ? \n"
            + "        ORDER BY fecha ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fechaInicio);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaFin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DiaLaboral>>() {
      @Override
      @NonNull
      public List<DiaLaboral> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfEsLaboral = CursorUtil.getColumnIndexOrThrow(_cursor, "es_laboral");
          final int _cursorIndexOfTipoDia = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo_dia");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfFechaActualizacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_actualizacion");
          final List<DiaLaboral> _result = new ArrayList<DiaLaboral>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiaLaboral _item;
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final boolean _tmpEsLaboral;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEsLaboral);
            _tmpEsLaboral = _tmp != 0;
            final String _tmpTipoDia;
            _tmpTipoDia = _cursor.getString(_cursorIndexOfTipoDia);
            final String _tmpDescripcion;
            if (_cursor.isNull(_cursorIndexOfDescripcion)) {
              _tmpDescripcion = null;
            } else {
              _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            }
            final long _tmpFechaActualizacion;
            _tmpFechaActualizacion = _cursor.getLong(_cursorIndexOfFechaActualizacion);
            _item = new DiaLaboral(_tmpFecha,_tmpEsLaboral,_tmpTipoDia,_tmpDescripcion,_tmpFechaActualizacion);
            _result.add(_item);
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
  public Object getDiaByFecha(final String fecha,
      final Continuation<? super DiaLaboral> $completion) {
    final String _sql = "SELECT * FROM dias_laborales WHERE fecha = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fecha);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DiaLaboral>() {
      @Override
      @Nullable
      public DiaLaboral call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfEsLaboral = CursorUtil.getColumnIndexOrThrow(_cursor, "es_laboral");
          final int _cursorIndexOfTipoDia = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo_dia");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfFechaActualizacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_actualizacion");
          final DiaLaboral _result;
          if (_cursor.moveToFirst()) {
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final boolean _tmpEsLaboral;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEsLaboral);
            _tmpEsLaboral = _tmp != 0;
            final String _tmpTipoDia;
            _tmpTipoDia = _cursor.getString(_cursorIndexOfTipoDia);
            final String _tmpDescripcion;
            if (_cursor.isNull(_cursorIndexOfDescripcion)) {
              _tmpDescripcion = null;
            } else {
              _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            }
            final long _tmpFechaActualizacion;
            _tmpFechaActualizacion = _cursor.getLong(_cursorIndexOfFechaActualizacion);
            _result = new DiaLaboral(_tmpFecha,_tmpEsLaboral,_tmpTipoDia,_tmpDescripcion,_tmpFechaActualizacion);
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
  public Object contarDiasLaborales(final String fechaInicio, final String fechaFin,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) FROM dias_laborales \n"
            + "        WHERE fecha BETWEEN ? AND ? \n"
            + "        AND es_laboral = 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fechaInicio);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaFin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object esDiaLaboral(final String fecha, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT es_laboral FROM dias_laborales WHERE fecha = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fecha);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @Nullable
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp == null ? null : _tmp != 0;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
