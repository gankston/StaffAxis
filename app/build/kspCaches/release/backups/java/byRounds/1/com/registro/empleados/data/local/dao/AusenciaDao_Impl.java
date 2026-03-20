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
import com.registro.empleados.data.local.entity.AusenciaEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AusenciaDao_Impl implements AusenciaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AusenciaEntity> __insertionAdapterOfAusenciaEntity;

  private final LocalDateConverter __localDateConverter = new LocalDateConverter();

  private final EntityDeletionOrUpdateAdapter<AusenciaEntity> __deletionAdapterOfAusenciaEntity;

  private final EntityDeletionOrUpdateAdapter<AusenciaEntity> __updateAdapterOfAusenciaEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsSent;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsFailed;

  public AusenciaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAusenciaEntity = new EntityInsertionAdapter<AusenciaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ausencia_table` (`id`,`legajoEmpleado`,`nombreEmpleado`,`fechaInicio`,`fechaFin`,`motivo`,`observaciones`,`esJustificada`,`fechaCreacion`,`syncStatus`,`attempts`,`lastError`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AusenciaEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLegajoEmpleado());
        statement.bindString(3, entity.getNombreEmpleado());
        final String _tmp = __localDateConverter.fromLocalDate(entity.getFechaInicio());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __localDateConverter.fromLocalDate(entity.getFechaFin());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        if (entity.getMotivo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMotivo());
        }
        if (entity.getObservaciones() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getObservaciones());
        }
        final int _tmp_2 = entity.getEsJustificada() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        final String _tmp_3 = __localDateConverter.fromLocalDate(entity.getFechaCreacion());
        if (_tmp_3 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_3);
        }
        statement.bindString(10, entity.getSyncStatus());
        statement.bindLong(11, entity.getAttempts());
        if (entity.getLastError() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getLastError());
        }
      }
    };
    this.__deletionAdapterOfAusenciaEntity = new EntityDeletionOrUpdateAdapter<AusenciaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ausencia_table` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AusenciaEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAusenciaEntity = new EntityDeletionOrUpdateAdapter<AusenciaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `ausencia_table` SET `id` = ?,`legajoEmpleado` = ?,`nombreEmpleado` = ?,`fechaInicio` = ?,`fechaFin` = ?,`motivo` = ?,`observaciones` = ?,`esJustificada` = ?,`fechaCreacion` = ?,`syncStatus` = ?,`attempts` = ?,`lastError` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AusenciaEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLegajoEmpleado());
        statement.bindString(3, entity.getNombreEmpleado());
        final String _tmp = __localDateConverter.fromLocalDate(entity.getFechaInicio());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __localDateConverter.fromLocalDate(entity.getFechaFin());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        if (entity.getMotivo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMotivo());
        }
        if (entity.getObservaciones() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getObservaciones());
        }
        final int _tmp_2 = entity.getEsJustificada() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        final String _tmp_3 = __localDateConverter.fromLocalDate(entity.getFechaCreacion());
        if (_tmp_3 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_3);
        }
        statement.bindString(10, entity.getSyncStatus());
        statement.bindLong(11, entity.getAttempts());
        if (entity.getLastError() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getLastError());
        }
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfMarkAsSent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE ausencia_table SET syncStatus = 'sent', attempts = attempts + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsFailed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE ausencia_table SET syncStatus = 'failed', attempts = attempts + 1, lastError = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAusencia(final AusenciaEntity ausencia,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAusenciaEntity.insertAndReturnId(ausencia);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAusencia(final AusenciaEntity ausencia,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAusenciaEntity.handle(ausencia);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAusencia(final AusenciaEntity ausencia,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAusenciaEntity.handle(ausencia);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsSent(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsSent.acquire();
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
          __preparedStmtOfMarkAsSent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsFailed(final long id, final String error,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsFailed.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, error);
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
          __preparedStmtOfMarkAsFailed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAusenciasByLegajo(final String legajo,
      final Continuation<? super List<AusenciaEntity>> $completion) {
    final String _sql = "SELECT * FROM ausencia_table WHERE legajoEmpleado = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AusenciaEntity>>() {
      @Override
      @NonNull
      public List<AusenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfNombreEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreEmpleado");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaInicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaFin");
          final int _cursorIndexOfMotivo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfEsJustificada = CursorUtil.getColumnIndexOrThrow(_cursor, "esJustificada");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final List<AusenciaEntity> _result = new ArrayList<AusenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AusenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpNombreEmpleado;
            _tmpNombreEmpleado = _cursor.getString(_cursorIndexOfNombreEmpleado);
            final LocalDate _tmpFechaInicio;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaInicio)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaInicio);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaInicio = _tmp_1;
            }
            final LocalDate _tmpFechaFin;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfFechaFin)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfFechaFin);
            }
            final LocalDate _tmp_3 = __localDateConverter.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaFin = _tmp_3;
            }
            final String _tmpMotivo;
            if (_cursor.isNull(_cursorIndexOfMotivo)) {
              _tmpMotivo = null;
            } else {
              _tmpMotivo = _cursor.getString(_cursorIndexOfMotivo);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final boolean _tmpEsJustificada;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfEsJustificada);
            _tmpEsJustificada = _tmp_4 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_6 = __localDateConverter.toLocalDate(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_6;
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _item = new AusenciaEntity(_tmpId,_tmpLegajoEmpleado,_tmpNombreEmpleado,_tmpFechaInicio,_tmpFechaFin,_tmpMotivo,_tmpObservaciones,_tmpEsJustificada,_tmpFechaCreacion,_tmpSyncStatus,_tmpAttempts,_tmpLastError);
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
  public Object getAusenciasByFecha(final LocalDate fecha,
      final Continuation<? super List<AusenciaEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM ausencia_table \n"
            + "        WHERE fechaInicio <= ? AND fechaFin >= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __localDateConverter.fromLocalDate(fecha);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __localDateConverter.fromLocalDate(fecha);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AusenciaEntity>>() {
      @Override
      @NonNull
      public List<AusenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfNombreEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreEmpleado");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaInicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaFin");
          final int _cursorIndexOfMotivo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfEsJustificada = CursorUtil.getColumnIndexOrThrow(_cursor, "esJustificada");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final List<AusenciaEntity> _result = new ArrayList<AusenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AusenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpNombreEmpleado;
            _tmpNombreEmpleado = _cursor.getString(_cursorIndexOfNombreEmpleado);
            final LocalDate _tmpFechaInicio;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfFechaInicio)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfFechaInicio);
            }
            final LocalDate _tmp_3 = __localDateConverter.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaInicio = _tmp_3;
            }
            final LocalDate _tmpFechaFin;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfFechaFin)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfFechaFin);
            }
            final LocalDate _tmp_5 = __localDateConverter.toLocalDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaFin = _tmp_5;
            }
            final String _tmpMotivo;
            if (_cursor.isNull(_cursorIndexOfMotivo)) {
              _tmpMotivo = null;
            } else {
              _tmpMotivo = _cursor.getString(_cursorIndexOfMotivo);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final boolean _tmpEsJustificada;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfEsJustificada);
            _tmpEsJustificada = _tmp_6 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_8 = __localDateConverter.toLocalDate(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_8;
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _item = new AusenciaEntity(_tmpId,_tmpLegajoEmpleado,_tmpNombreEmpleado,_tmpFechaInicio,_tmpFechaFin,_tmpMotivo,_tmpObservaciones,_tmpEsJustificada,_tmpFechaCreacion,_tmpSyncStatus,_tmpAttempts,_tmpLastError);
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
  public Object getAusenciasByRango(final LocalDate fechaInicio, final LocalDate fechaFin,
      final Continuation<? super List<AusenciaEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM ausencia_table \n"
            + "        WHERE fechaInicio <= ? AND fechaFin >= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __localDateConverter.fromLocalDate(fechaFin);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __localDateConverter.fromLocalDate(fechaInicio);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AusenciaEntity>>() {
      @Override
      @NonNull
      public List<AusenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfNombreEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreEmpleado");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaInicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaFin");
          final int _cursorIndexOfMotivo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfEsJustificada = CursorUtil.getColumnIndexOrThrow(_cursor, "esJustificada");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final List<AusenciaEntity> _result = new ArrayList<AusenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AusenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpNombreEmpleado;
            _tmpNombreEmpleado = _cursor.getString(_cursorIndexOfNombreEmpleado);
            final LocalDate _tmpFechaInicio;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfFechaInicio)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfFechaInicio);
            }
            final LocalDate _tmp_3 = __localDateConverter.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaInicio = _tmp_3;
            }
            final LocalDate _tmpFechaFin;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfFechaFin)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfFechaFin);
            }
            final LocalDate _tmp_5 = __localDateConverter.toLocalDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaFin = _tmp_5;
            }
            final String _tmpMotivo;
            if (_cursor.isNull(_cursorIndexOfMotivo)) {
              _tmpMotivo = null;
            } else {
              _tmpMotivo = _cursor.getString(_cursorIndexOfMotivo);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final boolean _tmpEsJustificada;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfEsJustificada);
            _tmpEsJustificada = _tmp_6 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_8 = __localDateConverter.toLocalDate(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_8;
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _item = new AusenciaEntity(_tmpId,_tmpLegajoEmpleado,_tmpNombreEmpleado,_tmpFechaInicio,_tmpFechaFin,_tmpMotivo,_tmpObservaciones,_tmpEsJustificada,_tmpFechaCreacion,_tmpSyncStatus,_tmpAttempts,_tmpLastError);
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
  public Object getAusenciaByLegajoYFecha(final String legajo, final LocalDate fecha,
      final Continuation<? super AusenciaEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM ausencia_table \n"
            + "        WHERE legajoEmpleado = ? \n"
            + "        AND fechaInicio <= ? \n"
            + "        AND fechaFin >= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    final String _tmp = __localDateConverter.fromLocalDate(fecha);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 3;
    final String _tmp_1 = __localDateConverter.fromLocalDate(fecha);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AusenciaEntity>() {
      @Override
      @Nullable
      public AusenciaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfNombreEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreEmpleado");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaInicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaFin");
          final int _cursorIndexOfMotivo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfEsJustificada = CursorUtil.getColumnIndexOrThrow(_cursor, "esJustificada");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final AusenciaEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpNombreEmpleado;
            _tmpNombreEmpleado = _cursor.getString(_cursorIndexOfNombreEmpleado);
            final LocalDate _tmpFechaInicio;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfFechaInicio)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfFechaInicio);
            }
            final LocalDate _tmp_3 = __localDateConverter.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaInicio = _tmp_3;
            }
            final LocalDate _tmpFechaFin;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfFechaFin)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfFechaFin);
            }
            final LocalDate _tmp_5 = __localDateConverter.toLocalDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaFin = _tmp_5;
            }
            final String _tmpMotivo;
            if (_cursor.isNull(_cursorIndexOfMotivo)) {
              _tmpMotivo = null;
            } else {
              _tmpMotivo = _cursor.getString(_cursorIndexOfMotivo);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final boolean _tmpEsJustificada;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfEsJustificada);
            _tmpEsJustificada = _tmp_6 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_8 = __localDateConverter.toLocalDate(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_8;
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _result = new AusenciaEntity(_tmpId,_tmpLegajoEmpleado,_tmpNombreEmpleado,_tmpFechaInicio,_tmpFechaFin,_tmpMotivo,_tmpObservaciones,_tmpEsJustificada,_tmpFechaCreacion,_tmpSyncStatus,_tmpAttempts,_tmpLastError);
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
  public Object getAllAusencias(final Continuation<? super List<AusenciaEntity>> $completion) {
    final String _sql = "SELECT * FROM ausencia_table ORDER BY fechaInicio DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AusenciaEntity>>() {
      @Override
      @NonNull
      public List<AusenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfNombreEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreEmpleado");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaInicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaFin");
          final int _cursorIndexOfMotivo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfEsJustificada = CursorUtil.getColumnIndexOrThrow(_cursor, "esJustificada");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final List<AusenciaEntity> _result = new ArrayList<AusenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AusenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpNombreEmpleado;
            _tmpNombreEmpleado = _cursor.getString(_cursorIndexOfNombreEmpleado);
            final LocalDate _tmpFechaInicio;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaInicio)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaInicio);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaInicio = _tmp_1;
            }
            final LocalDate _tmpFechaFin;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfFechaFin)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfFechaFin);
            }
            final LocalDate _tmp_3 = __localDateConverter.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaFin = _tmp_3;
            }
            final String _tmpMotivo;
            if (_cursor.isNull(_cursorIndexOfMotivo)) {
              _tmpMotivo = null;
            } else {
              _tmpMotivo = _cursor.getString(_cursorIndexOfMotivo);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final boolean _tmpEsJustificada;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfEsJustificada);
            _tmpEsJustificada = _tmp_4 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_6 = __localDateConverter.toLocalDate(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_6;
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _item = new AusenciaEntity(_tmpId,_tmpLegajoEmpleado,_tmpNombreEmpleado,_tmpFechaInicio,_tmpFechaFin,_tmpMotivo,_tmpObservaciones,_tmpEsJustificada,_tmpFechaCreacion,_tmpSyncStatus,_tmpAttempts,_tmpLastError);
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
  public Object getPendingAusencias(final int limit,
      final Continuation<? super List<AusenciaEntity>> $completion) {
    final String _sql = "SELECT * FROM ausencia_table WHERE syncStatus = 'pending' OR syncStatus = 'failed' LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AusenciaEntity>>() {
      @Override
      @NonNull
      public List<AusenciaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfNombreEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreEmpleado");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaInicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaFin");
          final int _cursorIndexOfMotivo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo");
          final int _cursorIndexOfObservaciones = CursorUtil.getColumnIndexOrThrow(_cursor, "observaciones");
          final int _cursorIndexOfEsJustificada = CursorUtil.getColumnIndexOrThrow(_cursor, "esJustificada");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final List<AusenciaEntity> _result = new ArrayList<AusenciaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AusenciaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final String _tmpNombreEmpleado;
            _tmpNombreEmpleado = _cursor.getString(_cursorIndexOfNombreEmpleado);
            final LocalDate _tmpFechaInicio;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFechaInicio)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFechaInicio);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaInicio = _tmp_1;
            }
            final LocalDate _tmpFechaFin;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfFechaFin)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfFechaFin);
            }
            final LocalDate _tmp_3 = __localDateConverter.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaFin = _tmp_3;
            }
            final String _tmpMotivo;
            if (_cursor.isNull(_cursorIndexOfMotivo)) {
              _tmpMotivo = null;
            } else {
              _tmpMotivo = _cursor.getString(_cursorIndexOfMotivo);
            }
            final String _tmpObservaciones;
            if (_cursor.isNull(_cursorIndexOfObservaciones)) {
              _tmpObservaciones = null;
            } else {
              _tmpObservaciones = _cursor.getString(_cursorIndexOfObservaciones);
            }
            final boolean _tmpEsJustificada;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfEsJustificada);
            _tmpEsJustificada = _tmp_4 != 0;
            final LocalDate _tmpFechaCreacion;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfFechaCreacion)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfFechaCreacion);
            }
            final LocalDate _tmp_6 = __localDateConverter.toLocalDate(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpFechaCreacion = _tmp_6;
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _item = new AusenciaEntity(_tmpId,_tmpLegajoEmpleado,_tmpNombreEmpleado,_tmpFechaInicio,_tmpFechaFin,_tmpMotivo,_tmpObservaciones,_tmpEsJustificada,_tmpFechaCreacion,_tmpSyncStatus,_tmpAttempts,_tmpLastError);
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
