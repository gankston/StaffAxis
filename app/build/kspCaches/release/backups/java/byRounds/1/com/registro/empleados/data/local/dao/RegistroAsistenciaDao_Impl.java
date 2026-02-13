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
import com.registro.empleados.data.local.entity.RegistroAsistenciaEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class RegistroAsistenciaDao_Impl implements RegistroAsistenciaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RegistroAsistenciaEntity> __insertionAdapterOfRegistroAsistenciaEntity;

  private final EntityDeletionOrUpdateAdapter<RegistroAsistenciaEntity> __updateAdapterOfRegistroAsistenciaEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRegistro;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllRegistros;

  private final LocalDateConverter __localDateConverter = new LocalDateConverter();

  public RegistroAsistenciaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRegistroAsistenciaEntity = new EntityInsertionAdapter<RegistroAsistenciaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `registros_asistencia` (`id`,`id_empleado`,`legajo_empleado`,`fecha`,`horas_trabajadas`,`observaciones`,`fecha_registro`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroAsistenciaEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getIdEmpleado() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getIdEmpleado());
        }
        if (entity.getLegajoEmpleado() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLegajoEmpleado());
        }
        statement.bindString(4, entity.getFecha());
        statement.bindLong(5, entity.getHorasTrabajadas());
        if (entity.getObservaciones() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getObservaciones());
        }
        statement.bindLong(7, entity.getFechaRegistro());
      }
    };
    this.__updateAdapterOfRegistroAsistenciaEntity = new EntityDeletionOrUpdateAdapter<RegistroAsistenciaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `registros_asistencia` SET `id` = ?,`id_empleado` = ?,`legajo_empleado` = ?,`fecha` = ?,`horas_trabajadas` = ?,`observaciones` = ?,`fecha_registro` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroAsistenciaEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getIdEmpleado() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getIdEmpleado());
        }
        if (entity.getLegajoEmpleado() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLegajoEmpleado());
        }
        statement.bindString(4, entity.getFecha());
        statement.bindLong(5, entity.getHorasTrabajadas());
        if (entity.getObservaciones() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getObservaciones());
        }
        statement.bindLong(7, entity.getFechaRegistro());
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
    this.__preparedStmtOfDeleteAllRegistros = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM registros_asistencia";
        return _query;
      }
    };
  }

  @Override
  public Object insertRegistro(final RegistroAsistenciaEntity registro,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRegistroAsistenciaEntity.insertAndReturnId(registro);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRegistro(final RegistroAsistenciaEntity registro,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRegistroAsistenciaEntity.handle(registro);
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
  public Object deleteAllRegistros(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllRegistros.acquire();
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
          __preparedStmtOfDeleteAllRegistros.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getRegistrosByLegajoYRango(final String legajo, final String fechaInicio,
      final String fechaFin,
      final Continuation<? super List<RegistroAsistenciaEntity>> $completion) {
    final String _sql = "SELECT * FROM registros_asistencia WHERE legajo_empleado = ? AND fecha BETWEEN ? AND ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaInicio);
    _argIndex = 3;
    _statement.bindString(_argIndex, fechaFin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RegistroAsistenciaEntity>>() {
      @Override
      @NonNull
      public List<RegistroAsistenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIdEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_empleado");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final List<RegistroAsistenciaEntity> _result = new ArrayList<RegistroAsistenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpIdEmpleado;
            if (_cursor.isNull(_cursorIndexOfIdEmpleado)) {
              _tmpIdEmpleado = null;
            } else {
              _tmpIdEmpleado = _cursor.getLong(_cursorIndexOfIdEmpleado);
            }
            final String _tmpLegajoEmpleado;
            if (_cursor.isNull(_cursorIndexOfLegajoEmpleado)) {
              _tmpLegajoEmpleado = null;
            } else {
              _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            }
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final int _tmpHorasTrabajadas;
            _tmpHorasTrabajadas = _cursor.getInt(_cursorIndexOfHorasTrabajadas);
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final long _tmpFechaRegistro;
            _tmpFechaRegistro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            _item = new RegistroAsistenciaEntity(_tmpId,_tmpIdEmpleado,_tmpLegajoEmpleado,_tmpFecha,_tmpHorasTrabajadas,_tmpObservaciones,_tmpFechaRegistro);
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
      final Continuation<? super List<RegistroAsistenciaEntity>> $completion) {
    final String _sql = "SELECT * FROM registros_asistencia WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC, legajo_empleado ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fechaInicio);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaFin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RegistroAsistenciaEntity>>() {
      @Override
      @NonNull
      public List<RegistroAsistenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIdEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_empleado");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final List<RegistroAsistenciaEntity> _result = new ArrayList<RegistroAsistenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpIdEmpleado;
            if (_cursor.isNull(_cursorIndexOfIdEmpleado)) {
              _tmpIdEmpleado = null;
            } else {
              _tmpIdEmpleado = _cursor.getLong(_cursorIndexOfIdEmpleado);
            }
            final String _tmpLegajoEmpleado;
            if (_cursor.isNull(_cursorIndexOfLegajoEmpleado)) {
              _tmpLegajoEmpleado = null;
            } else {
              _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            }
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final int _tmpHorasTrabajadas;
            _tmpHorasTrabajadas = _cursor.getInt(_cursorIndexOfHorasTrabajadas);
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final long _tmpFechaRegistro;
            _tmpFechaRegistro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            _item = new RegistroAsistenciaEntity(_tmpId,_tmpIdEmpleado,_tmpLegajoEmpleado,_tmpFecha,_tmpHorasTrabajadas,_tmpObservaciones,_tmpFechaRegistro);
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
      final Continuation<? super RegistroAsistenciaEntity> $completion) {
    final String _sql = "SELECT * FROM registros_asistencia WHERE legajo_empleado = ? AND fecha = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    _statement.bindString(_argIndex, fecha);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RegistroAsistenciaEntity>() {
      @Override
      @Nullable
      public RegistroAsistenciaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIdEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_empleado");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final RegistroAsistenciaEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpIdEmpleado;
            if (_cursor.isNull(_cursorIndexOfIdEmpleado)) {
              _tmpIdEmpleado = null;
            } else {
              _tmpIdEmpleado = _cursor.getLong(_cursorIndexOfIdEmpleado);
            }
            final String _tmpLegajoEmpleado;
            if (_cursor.isNull(_cursorIndexOfLegajoEmpleado)) {
              _tmpLegajoEmpleado = null;
            } else {
              _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            }
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final int _tmpHorasTrabajadas;
            _tmpHorasTrabajadas = _cursor.getInt(_cursorIndexOfHorasTrabajadas);
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final long _tmpFechaRegistro;
            _tmpFechaRegistro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            _result = new RegistroAsistenciaEntity(_tmpId,_tmpIdEmpleado,_tmpLegajoEmpleado,_tmpFecha,_tmpHorasTrabajadas,_tmpObservaciones,_tmpFechaRegistro);
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
  public Object getTotalHorasByLegajoYPeriodo(final String legajo, final String fechaInicio,
      final String fechaFin, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(horas_trabajadas) FROM registros_asistencia WHERE legajo_empleado = ? AND fecha BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaInicio);
    _argIndex = 3;
    _statement.bindString(_argIndex, fechaFin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public Flow<List<RegistroAsistenciaEntity>> getRegistrosByLegajo(final String legajo) {
    final String _sql = "SELECT * FROM registros_asistencia WHERE legajo_empleado = ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"registros_asistencia"}, new Callable<List<RegistroAsistenciaEntity>>() {
      @Override
      @NonNull
      public List<RegistroAsistenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIdEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_empleado");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final List<RegistroAsistenciaEntity> _result = new ArrayList<RegistroAsistenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpIdEmpleado;
            if (_cursor.isNull(_cursorIndexOfIdEmpleado)) {
              _tmpIdEmpleado = null;
            } else {
              _tmpIdEmpleado = _cursor.getLong(_cursorIndexOfIdEmpleado);
            }
            final String _tmpLegajoEmpleado;
            if (_cursor.isNull(_cursorIndexOfLegajoEmpleado)) {
              _tmpLegajoEmpleado = null;
            } else {
              _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            }
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final int _tmpHorasTrabajadas;
            _tmpHorasTrabajadas = _cursor.getInt(_cursorIndexOfHorasTrabajadas);
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final long _tmpFechaRegistro;
            _tmpFechaRegistro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            _item = new RegistroAsistenciaEntity(_tmpId,_tmpIdEmpleado,_tmpLegajoEmpleado,_tmpFecha,_tmpHorasTrabajadas,_tmpObservaciones,_tmpFechaRegistro);
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
  public Flow<List<RegistroAsistenciaEntity>> getRegistrosByFecha(final String fecha) {
    final String _sql = "SELECT * FROM registros_asistencia WHERE fecha = ? ORDER BY legajo_empleado ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fecha);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"registros_asistencia"}, new Callable<List<RegistroAsistenciaEntity>>() {
      @Override
      @NonNull
      public List<RegistroAsistenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIdEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_empleado");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final List<RegistroAsistenciaEntity> _result = new ArrayList<RegistroAsistenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpIdEmpleado;
            if (_cursor.isNull(_cursorIndexOfIdEmpleado)) {
              _tmpIdEmpleado = null;
            } else {
              _tmpIdEmpleado = _cursor.getLong(_cursorIndexOfIdEmpleado);
            }
            final String _tmpLegajoEmpleado;
            if (_cursor.isNull(_cursorIndexOfLegajoEmpleado)) {
              _tmpLegajoEmpleado = null;
            } else {
              _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            }
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final int _tmpHorasTrabajadas;
            _tmpHorasTrabajadas = _cursor.getInt(_cursorIndexOfHorasTrabajadas);
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final long _tmpFechaRegistro;
            _tmpFechaRegistro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            _item = new RegistroAsistenciaEntity(_tmpId,_tmpIdEmpleado,_tmpLegajoEmpleado,_tmpFecha,_tmpHorasTrabajadas,_tmpObservaciones,_tmpFechaRegistro);
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
  public Flow<List<RegistroAsistenciaEntity>> getAllRegistros() {
    final String _sql = "SELECT * FROM registros_asistencia ORDER BY fecha DESC, legajo_empleado ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"registros_asistencia"}, new Callable<List<RegistroAsistenciaEntity>>() {
      @Override
      @NonNull
      public List<RegistroAsistenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIdEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_empleado");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final List<RegistroAsistenciaEntity> _result = new ArrayList<RegistroAsistenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpIdEmpleado;
            if (_cursor.isNull(_cursorIndexOfIdEmpleado)) {
              _tmpIdEmpleado = null;
            } else {
              _tmpIdEmpleado = _cursor.getLong(_cursorIndexOfIdEmpleado);
            }
            final String _tmpLegajoEmpleado;
            if (_cursor.isNull(_cursorIndexOfLegajoEmpleado)) {
              _tmpLegajoEmpleado = null;
            } else {
              _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            }
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final int _tmpHorasTrabajadas;
            _tmpHorasTrabajadas = _cursor.getInt(_cursorIndexOfHorasTrabajadas);
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final long _tmpFechaRegistro;
            _tmpFechaRegistro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            _item = new RegistroAsistenciaEntity(_tmpId,_tmpIdEmpleado,_tmpLegajoEmpleado,_tmpFecha,_tmpHorasTrabajadas,_tmpObservaciones,_tmpFechaRegistro);
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
  public Object getRegistrosByLegajoAndFecha(final String legajo, final LocalDate fecha,
      final Continuation<? super List<RegistroAsistenciaEntity>> $completion) {
    final String _sql = "SELECT * FROM registros_asistencia WHERE legajo_empleado = ? AND fecha = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    final String _tmp = __localDateConverter.fromLocalDate(fecha);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RegistroAsistenciaEntity>>() {
      @Override
      @NonNull
      public List<RegistroAsistenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIdEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_empleado");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajo_empleado");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfHorasTrabajadas = CursorUtil.getColumnIndexOrThrow(_cursor, "horas_trabajadas");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final List<RegistroAsistenciaEntity> _result = new ArrayList<RegistroAsistenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroAsistenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpIdEmpleado;
            if (_cursor.isNull(_cursorIndexOfIdEmpleado)) {
              _tmpIdEmpleado = null;
            } else {
              _tmpIdEmpleado = _cursor.getLong(_cursorIndexOfIdEmpleado);
            }
            final String _tmpLegajoEmpleado;
            if (_cursor.isNull(_cursorIndexOfLegajoEmpleado)) {
              _tmpLegajoEmpleado = null;
            } else {
              _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            }
            final String _tmpFecha;
            _tmpFecha = _cursor.getString(_cursorIndexOfFecha);
            final int _tmpHorasTrabajadas;
            _tmpHorasTrabajadas = _cursor.getInt(_cursorIndexOfHorasTrabajadas);
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final long _tmpFechaRegistro;
            _tmpFechaRegistro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            _item = new RegistroAsistenciaEntity(_tmpId,_tmpIdEmpleado,_tmpLegajoEmpleado,_tmpFecha,_tmpHorasTrabajadas,_tmpObservaciones,_tmpFechaRegistro);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
