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
import com.registro.empleados.data.local.entity.HorasEmpleadoMesEntity;
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
public final class HorasEmpleadoMesDao_Impl implements HorasEmpleadoMesDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HorasEmpleadoMesEntity> __insertionAdapterOfHorasEmpleadoMesEntity;

  private final LocalDateConverter __localDateConverter = new LocalDateConverter();

  private final EntityDeletionOrUpdateAdapter<HorasEmpleadoMesEntity> __deletionAdapterOfHorasEmpleadoMesEntity;

  private final EntityDeletionOrUpdateAdapter<HorasEmpleadoMesEntity> __updateAdapterOfHorasEmpleadoMesEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteHorasByLegajoAndMes;

  public HorasEmpleadoMesDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHorasEmpleadoMesEntity = new EntityInsertionAdapter<HorasEmpleadoMesEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `horas_empleado_mes` (`id`,`legajoEmpleado`,`año`,`mes`,`totalHoras`,`diasTrabajados`,`promedioDiario`,`ultimaActualizacion`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HorasEmpleadoMesEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLegajoEmpleado());
        statement.bindLong(3, entity.getAño());
        statement.bindLong(4, entity.getMes());
        statement.bindDouble(5, entity.getTotalHoras());
        statement.bindLong(6, entity.getDiasTrabajados());
        statement.bindDouble(7, entity.getPromedioDiario());
        final String _tmp = __localDateConverter.fromLocalDate(entity.getUltimaActualizacion());
        if (_tmp == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp);
        }
      }
    };
    this.__deletionAdapterOfHorasEmpleadoMesEntity = new EntityDeletionOrUpdateAdapter<HorasEmpleadoMesEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `horas_empleado_mes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HorasEmpleadoMesEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfHorasEmpleadoMesEntity = new EntityDeletionOrUpdateAdapter<HorasEmpleadoMesEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `horas_empleado_mes` SET `id` = ?,`legajoEmpleado` = ?,`año` = ?,`mes` = ?,`totalHoras` = ?,`diasTrabajados` = ?,`promedioDiario` = ?,`ultimaActualizacion` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HorasEmpleadoMesEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLegajoEmpleado());
        statement.bindLong(3, entity.getAño());
        statement.bindLong(4, entity.getMes());
        statement.bindDouble(5, entity.getTotalHoras());
        statement.bindLong(6, entity.getDiasTrabajados());
        statement.bindDouble(7, entity.getPromedioDiario());
        final String _tmp = __localDateConverter.fromLocalDate(entity.getUltimaActualizacion());
        if (_tmp == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp);
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteHorasByLegajoAndMes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM horas_empleado_mes WHERE legajoEmpleado = ? AND año = ? AND mes = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertHorasEmpleadoMes(final HorasEmpleadoMesEntity horas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHorasEmpleadoMesEntity.insert(horas);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteHorasEmpleadoMes(final HorasEmpleadoMesEntity horas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfHorasEmpleadoMesEntity.handle(horas);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateHorasEmpleadoMes(final HorasEmpleadoMesEntity horas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfHorasEmpleadoMesEntity.handle(horas);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteHorasByLegajoAndMes(final String legajo, final int año, final int mes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteHorasByLegajoAndMes.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, legajo);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, año);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, mes);
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
          __preparedStmtOfDeleteHorasByLegajoAndMes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<HorasEmpleadoMesEntity>> getHorasByLegajo(final String legajo) {
    final String _sql = "SELECT * FROM horas_empleado_mes WHERE legajoEmpleado = ? ORDER BY año DESC, mes DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"horas_empleado_mes"}, new Callable<List<HorasEmpleadoMesEntity>>() {
      @Override
      @NonNull
      public List<HorasEmpleadoMesEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfAO = CursorUtil.getColumnIndexOrThrow(_cursor, "año");
          final int _cursorIndexOfMes = CursorUtil.getColumnIndexOrThrow(_cursor, "mes");
          final int _cursorIndexOfTotalHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHoras");
          final int _cursorIndexOfDiasTrabajados = CursorUtil.getColumnIndexOrThrow(_cursor, "diasTrabajados");
          final int _cursorIndexOfPromedioDiario = CursorUtil.getColumnIndexOrThrow(_cursor, "promedioDiario");
          final int _cursorIndexOfUltimaActualizacion = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimaActualizacion");
          final List<HorasEmpleadoMesEntity> _result = new ArrayList<HorasEmpleadoMesEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HorasEmpleadoMesEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final int _tmpAño;
            _tmpAño = _cursor.getInt(_cursorIndexOfAO);
            final int _tmpMes;
            _tmpMes = _cursor.getInt(_cursorIndexOfMes);
            final double _tmpTotalHoras;
            _tmpTotalHoras = _cursor.getDouble(_cursorIndexOfTotalHoras);
            final int _tmpDiasTrabajados;
            _tmpDiasTrabajados = _cursor.getInt(_cursorIndexOfDiasTrabajados);
            final double _tmpPromedioDiario;
            _tmpPromedioDiario = _cursor.getDouble(_cursorIndexOfPromedioDiario);
            final LocalDate _tmpUltimaActualizacion;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfUltimaActualizacion)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfUltimaActualizacion);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpUltimaActualizacion = _tmp_1;
            }
            _item = new HorasEmpleadoMesEntity(_tmpId,_tmpLegajoEmpleado,_tmpAño,_tmpMes,_tmpTotalHoras,_tmpDiasTrabajados,_tmpPromedioDiario,_tmpUltimaActualizacion);
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
  public Object getHorasByLegajoAndMes(final String legajo, final int año, final int mes,
      final Continuation<? super HorasEmpleadoMesEntity> $completion) {
    final String _sql = "SELECT * FROM horas_empleado_mes WHERE legajoEmpleado = ? AND año = ? AND mes = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, legajo);
    _argIndex = 2;
    _statement.bindLong(_argIndex, año);
    _argIndex = 3;
    _statement.bindLong(_argIndex, mes);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<HorasEmpleadoMesEntity>() {
      @Override
      @Nullable
      public HorasEmpleadoMesEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfAO = CursorUtil.getColumnIndexOrThrow(_cursor, "año");
          final int _cursorIndexOfMes = CursorUtil.getColumnIndexOrThrow(_cursor, "mes");
          final int _cursorIndexOfTotalHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHoras");
          final int _cursorIndexOfDiasTrabajados = CursorUtil.getColumnIndexOrThrow(_cursor, "diasTrabajados");
          final int _cursorIndexOfPromedioDiario = CursorUtil.getColumnIndexOrThrow(_cursor, "promedioDiario");
          final int _cursorIndexOfUltimaActualizacion = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimaActualizacion");
          final HorasEmpleadoMesEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final int _tmpAño;
            _tmpAño = _cursor.getInt(_cursorIndexOfAO);
            final int _tmpMes;
            _tmpMes = _cursor.getInt(_cursorIndexOfMes);
            final double _tmpTotalHoras;
            _tmpTotalHoras = _cursor.getDouble(_cursorIndexOfTotalHoras);
            final int _tmpDiasTrabajados;
            _tmpDiasTrabajados = _cursor.getInt(_cursorIndexOfDiasTrabajados);
            final double _tmpPromedioDiario;
            _tmpPromedioDiario = _cursor.getDouble(_cursorIndexOfPromedioDiario);
            final LocalDate _tmpUltimaActualizacion;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfUltimaActualizacion)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfUltimaActualizacion);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpUltimaActualizacion = _tmp_1;
            }
            _result = new HorasEmpleadoMesEntity(_tmpId,_tmpLegajoEmpleado,_tmpAño,_tmpMes,_tmpTotalHoras,_tmpDiasTrabajados,_tmpPromedioDiario,_tmpUltimaActualizacion);
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
  public Flow<List<HorasEmpleadoMesEntity>> getHorasByMes(final int año, final int mes) {
    final String _sql = "SELECT * FROM horas_empleado_mes WHERE año = ? AND mes = ? ORDER BY totalHoras DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, año);
    _argIndex = 2;
    _statement.bindLong(_argIndex, mes);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"horas_empleado_mes"}, new Callable<List<HorasEmpleadoMesEntity>>() {
      @Override
      @NonNull
      public List<HorasEmpleadoMesEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfAO = CursorUtil.getColumnIndexOrThrow(_cursor, "año");
          final int _cursorIndexOfMes = CursorUtil.getColumnIndexOrThrow(_cursor, "mes");
          final int _cursorIndexOfTotalHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHoras");
          final int _cursorIndexOfDiasTrabajados = CursorUtil.getColumnIndexOrThrow(_cursor, "diasTrabajados");
          final int _cursorIndexOfPromedioDiario = CursorUtil.getColumnIndexOrThrow(_cursor, "promedioDiario");
          final int _cursorIndexOfUltimaActualizacion = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimaActualizacion");
          final List<HorasEmpleadoMesEntity> _result = new ArrayList<HorasEmpleadoMesEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HorasEmpleadoMesEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final int _tmpAño;
            _tmpAño = _cursor.getInt(_cursorIndexOfAO);
            final int _tmpMes;
            _tmpMes = _cursor.getInt(_cursorIndexOfMes);
            final double _tmpTotalHoras;
            _tmpTotalHoras = _cursor.getDouble(_cursorIndexOfTotalHoras);
            final int _tmpDiasTrabajados;
            _tmpDiasTrabajados = _cursor.getInt(_cursorIndexOfDiasTrabajados);
            final double _tmpPromedioDiario;
            _tmpPromedioDiario = _cursor.getDouble(_cursorIndexOfPromedioDiario);
            final LocalDate _tmpUltimaActualizacion;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfUltimaActualizacion)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfUltimaActualizacion);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpUltimaActualizacion = _tmp_1;
            }
            _item = new HorasEmpleadoMesEntity(_tmpId,_tmpLegajoEmpleado,_tmpAño,_tmpMes,_tmpTotalHoras,_tmpDiasTrabajados,_tmpPromedioDiario,_tmpUltimaActualizacion);
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
  public Flow<List<HorasEmpleadoMesEntity>> getHorasByAño(final int año) {
    final String _sql = "SELECT * FROM horas_empleado_mes WHERE año = ? ORDER BY mes, totalHoras DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, año);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"horas_empleado_mes"}, new Callable<List<HorasEmpleadoMesEntity>>() {
      @Override
      @NonNull
      public List<HorasEmpleadoMesEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLegajoEmpleado = CursorUtil.getColumnIndexOrThrow(_cursor, "legajoEmpleado");
          final int _cursorIndexOfAO = CursorUtil.getColumnIndexOrThrow(_cursor, "año");
          final int _cursorIndexOfMes = CursorUtil.getColumnIndexOrThrow(_cursor, "mes");
          final int _cursorIndexOfTotalHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHoras");
          final int _cursorIndexOfDiasTrabajados = CursorUtil.getColumnIndexOrThrow(_cursor, "diasTrabajados");
          final int _cursorIndexOfPromedioDiario = CursorUtil.getColumnIndexOrThrow(_cursor, "promedioDiario");
          final int _cursorIndexOfUltimaActualizacion = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimaActualizacion");
          final List<HorasEmpleadoMesEntity> _result = new ArrayList<HorasEmpleadoMesEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HorasEmpleadoMesEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLegajoEmpleado;
            _tmpLegajoEmpleado = _cursor.getString(_cursorIndexOfLegajoEmpleado);
            final int _tmpAño;
            _tmpAño = _cursor.getInt(_cursorIndexOfAO);
            final int _tmpMes;
            _tmpMes = _cursor.getInt(_cursorIndexOfMes);
            final double _tmpTotalHoras;
            _tmpTotalHoras = _cursor.getDouble(_cursorIndexOfTotalHoras);
            final int _tmpDiasTrabajados;
            _tmpDiasTrabajados = _cursor.getInt(_cursorIndexOfDiasTrabajados);
            final double _tmpPromedioDiario;
            _tmpPromedioDiario = _cursor.getDouble(_cursorIndexOfPromedioDiario);
            final LocalDate _tmpUltimaActualizacion;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfUltimaActualizacion)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfUltimaActualizacion);
            }
            final LocalDate _tmp_1 = __localDateConverter.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpUltimaActualizacion = _tmp_1;
            }
            _item = new HorasEmpleadoMesEntity(_tmpId,_tmpLegajoEmpleado,_tmpAño,_tmpMes,_tmpTotalHoras,_tmpDiasTrabajados,_tmpPromedioDiario,_tmpUltimaActualizacion);
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
