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
import com.registro.empleados.data.database.entities.RegistroAsistencia;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
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
public final class RegistroAsistenciaDao_Impl implements RegistroAsistenciaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RegistroAsistencia> __insertionAdapterOfRegistroAsistencia;

  private final EntityDeletionOrUpdateAdapter<RegistroAsistencia> __updateAdapterOfRegistroAsistencia;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRegistro;

  public RegistroAsistenciaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRegistroAsistencia = new EntityInsertionAdapter<RegistroAsistencia>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `registros_asistencia` (`id`,`legajo_empleado`,`fecha`,`hora_entrada`,`hora_salida`,`horas_trabajadas`,`observaciones`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroAsistencia entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLegajoEmpleado());
        statement.bindString(3, entity.getFecha());
        if (entity.getHoraEntrada() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getHoraEntrada());
        }
        if (entity.getHoraSalida() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getHoraSalida());
        }
        if (entity.getHorasTrabajadas() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getHorasTrabajadas());
        }
        if (entity.getObservaciones() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getObservaciones());
        }
      }
    };
    this.__updateAdapterOfRegistroAsistencia = new EntityDeletionOrUpdateAdapter<RegistroAsistencia>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `registros_asistencia` SET `id` = ?,`legajo_empleado` = ?,`fecha` = ?,`hora_entrada` = ?,`hora_salida` = ?,`horas_trabajadas` = ?,`observaciones` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroAsistencia entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLegajoEmpleado());
        statement.bindString(3, entity.getFecha());
        if (entity.getHoraEntrada() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getHoraEntrada());
        }
        if (entity.getHoraSalida() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getHoraSalida());
        }
        if (entity.getHorasTrabajadas() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getHorasTrabajadas());
        }
        if (entity.getObservaciones() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getObservaciones());
        }
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteRegistro = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM registros_asistencia WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRegistro(final RegistroAsistencia registro,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRegistroAsistencia.insertAndReturnId(registro);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRegistro(final RegistroAsistencia registro,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRegistroAsistencia.handle(registro);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRegistro(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRegistro.acquire();
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
          __preparedStmtOfDeleteRegistro.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getRegistrosByLegajoYRango(final String legajo, final String fechaInicio,
      final String fechaFin, final Continuation<? super List<RegistroAsistencia>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM registros_asistencia \n"
            + "        WHERE legajo_empleado = ? \n"
            + "        AND fecha BETWEEN ? AND ? \n"
            + "        ORDER BY fecha DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaInicio);
    _argIndex = 3;
    _statement.bindString(_argIndex, fechaFin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RegistroAsistencia>>() {
      @Override
      @NonNull
      public List<RegistroAsistencia> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHoraEntrada = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_entrada");
          final int _cursorIndexOfHoraSalida = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_salida");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final List<RegistroAsistencia> _result = new ArrayList<RegistroAsistencia>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistencia _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final Long _tmpHoraEntrada;
            if (_cursor.isNull(_cursorIndexOfHoraEntrada)) {
              _tmpHoraEntrada = null;
            } else {
              _tmpHoraEntrada = _cursor.getLong(_cursorIndexOfHoraEntrada);
            }
            final Long _tmpHoraSalida;
            if (_cursor.isNull(_cursorIndexOfHoraSalida)) {
              _tmpHoraSalida = null;
            } else {
              _tmpHoraSalida = _cursor.getLong(_cursorIndexOfHoraSalida);
            }
            final Double _tmpHorasTrabajadas;
            if (_cursor.isNull(_cursorIndexOfHorasTrabajadas)) {
              _tmpHorasTrabajadas = null;
            } else {
              _tmpHorasTrabajadas = _cursor.getDouble(_cursorIndexOfHorasTrabajadas);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            _item = new RegistroAsistencia(_tmpId,_tmpLegajoEmpleado,_tmpFecha,_tmpHoraEntrada,_tmpHoraSalida,_tmpHorasTrabajadas,_tmpObservaciones);
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
  public Object getRegistrosByRango(final String fechaInicio, final String fechaFin,
      final Continuation<? super List<RegistroAsistencia>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM registros_asistencia \n"
            + "        WHERE fecha BETWEEN ? AND ? \n"
            + "        ORDER BY fecha DESC, legajo_empleado ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fechaInicio);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaFin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RegistroAsistencia>>() {
      @Override
      @NonNull
      public List<RegistroAsistencia> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHoraEntrada = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_entrada");
          final int _cursorIndexOfHoraSalida = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_salida");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final List<RegistroAsistencia> _result = new ArrayList<RegistroAsistencia>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistencia _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final Long _tmpHoraEntrada;
            if (_cursor.isNull(_cursorIndexOfHoraEntrada)) {
              _tmpHoraEntrada = null;
            } else {
              _tmpHoraEntrada = _cursor.getLong(_cursorIndexOfHoraEntrada);
            }
            final Long _tmpHoraSalida;
            if (_cursor.isNull(_cursorIndexOfHoraSalida)) {
              _tmpHoraSalida = null;
            } else {
              _tmpHoraSalida = _cursor.getLong(_cursorIndexOfHoraSalida);
            }
            final Double _tmpHorasTrabajadas;
            if (_cursor.isNull(_cursorIndexOfHorasTrabajadas)) {
              _tmpHorasTrabajadas = null;
            } else {
              _tmpHorasTrabajadas = _cursor.getDouble(_cursorIndexOfHorasTrabajadas);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            _item = new RegistroAsistencia(_tmpId,_tmpLegajoEmpleado,_tmpFecha,_tmpHoraEntrada,_tmpHoraSalida,_tmpHorasTrabajadas,_tmpObservaciones);
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
  public Object getRegistroByLegajoYFecha(final String legajo, final String fecha,
      final Continuation<? super RegistroAsistencia> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM registros_asistencia \n"
            + "        WHERE legajo_empleado = ? AND fecha = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    _statement.bindString(_argIndex, fecha);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RegistroAsistencia>() {
      @Override
      @Nullable
      public RegistroAsistencia call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHoraEntrada = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_entrada");
          final int _cursorIndexOfHoraSalida = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_salida");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final RegistroAsistencia _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final Long _tmpHoraEntrada;
            if (_cursor.isNull(_cursorIndexOfHoraEntrada)) {
              _tmpHoraEntrada = null;
            } else {
              _tmpHoraEntrada = _cursor.getLong(_cursorIndexOfHoraEntrada);
            }
            final Long _tmpHoraSalida;
            if (_cursor.isNull(_cursorIndexOfHoraSalida)) {
              _tmpHoraSalida = null;
            } else {
              _tmpHoraSalida = _cursor.getLong(_cursorIndexOfHoraSalida);
            }
            final Double _tmpHorasTrabajadas;
            if (_cursor.isNull(_cursorIndexOfHorasTrabajadas)) {
              _tmpHorasTrabajadas = null;
            } else {
              _tmpHorasTrabajadas = _cursor.getDouble(_cursorIndexOfHorasTrabajadas);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            _result = new RegistroAsistencia(_tmpId,_tmpLegajoEmpleado,_tmpFecha,_tmpHoraEntrada,_tmpHoraSalida,_tmpHorasTrabajadas,_tmpObservaciones);
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
  public Object getRegistroHoy(final String legajo, final String fecha,
      final Continuation<? super RegistroAsistencia> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM registros_asistencia \n"
            + "        WHERE legajo_empleado = ? AND fecha = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    _statement.bindString(_argIndex, fecha);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RegistroAsistencia>() {
      @Override
      @Nullable
      public RegistroAsistencia call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHoraEntrada = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_entrada");
          final int _cursorIndexOfHoraSalida = CursorUtil.getColumnIndexOrThrow(_cursor, "hora_salida");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final RegistroAsistencia _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final Long _tmpHoraEntrada;
            if (_cursor.isNull(_cursorIndexOfHoraEntrada)) {
              _tmpHoraEntrada = null;
            } else {
              _tmpHoraEntrada = _cursor.getLong(_cursorIndexOfHoraEntrada);
            }
            final Long _tmpHoraSalida;
            if (_cursor.isNull(_cursorIndexOfHoraSalida)) {
              _tmpHoraSalida = null;
            } else {
              _tmpHoraSalida = _cursor.getLong(_cursorIndexOfHoraSalida);
            }
            final Double _tmpHorasTrabajadas;
            if (_cursor.isNull(_cursorIndexOfHorasTrabajadas)) {
              _tmpHorasTrabajadas = null;
            } else {
              _tmpHorasTrabajadas = _cursor.getDouble(_cursorIndexOfHorasTrabajadas);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            _result = new RegistroAsistencia(_tmpId,_tmpLegajoEmpleado,_tmpFecha,_tmpHoraEntrada,_tmpHoraSalida,_tmpHorasTrabajadas,_tmpObservaciones);
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
