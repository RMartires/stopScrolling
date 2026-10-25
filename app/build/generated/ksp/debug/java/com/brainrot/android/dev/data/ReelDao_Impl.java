package com.brainrot.android.dev.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReelDao_Impl implements ReelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyAppReels> __insertionAdapterOfDailyAppReels;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ReelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyAppReels = new EntityInsertionAdapter<DailyAppReels>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_app_reels` (`statsDate`,`appId`,`count`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyAppReels entity) {
        statement.bindString(1, entity.getStatsDate());
        statement.bindString(2, entity.getAppId());
        statement.bindLong(3, entity.getCount());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_app_reels";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final DailyAppReels row, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyAppReels.insert(row);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object increment(final String statsDate, final String appId,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ReelDao.DefaultImpls.increment(ReelDao_Impl.this, statsDate, appId, __cont), $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
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
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyAppReels>> observeForDate(final String statsDate) {
    final String _sql = "SELECT * FROM daily_app_reels WHERE statsDate = ? ORDER BY count DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, statsDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_app_reels"}, new Callable<List<DailyAppReels>>() {
      @Override
      @NonNull
      public List<DailyAppReels> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStatsDate = CursorUtil.getColumnIndexOrThrow(_cursor, "statsDate");
          final int _cursorIndexOfAppId = CursorUtil.getColumnIndexOrThrow(_cursor, "appId");
          final int _cursorIndexOfCount = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
          final List<DailyAppReels> _result = new ArrayList<DailyAppReels>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyAppReels _item;
            final String _tmpStatsDate;
            _tmpStatsDate = _cursor.getString(_cursorIndexOfStatsDate);
            final String _tmpAppId;
            _tmpAppId = _cursor.getString(_cursorIndexOfAppId);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new DailyAppReels(_tmpStatsDate,_tmpAppId,_tmpCount);
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
  public Flow<Integer> observeTotalForDate(final String statsDate) {
    final String _sql = "SELECT COALESCE(SUM(count), 0) FROM daily_app_reels WHERE statsDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, statsDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_app_reels"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object totalForDate(final String statsDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COALESCE(SUM(count), 0) FROM daily_app_reels WHERE statsDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, statsDate);
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
  public Flow<List<DayTotal>> observeDailyTotals(final int limit) {
    final String _sql = "\n"
            + "        SELECT statsDate, SUM(count) AS total\n"
            + "        FROM daily_app_reels\n"
            + "        GROUP BY statsDate\n"
            + "        ORDER BY statsDate DESC\n"
            + "        LIMIT ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_app_reels"}, new Callable<List<DayTotal>>() {
      @Override
      @NonNull
      public List<DayTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStatsDate = 0;
          final int _cursorIndexOfTotal = 1;
          final List<DayTotal> _result = new ArrayList<DayTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayTotal _item;
            final String _tmpStatsDate;
            _tmpStatsDate = _cursor.getString(_cursorIndexOfStatsDate);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            _item = new DayTotal(_tmpStatsDate,_tmpTotal);
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
  public Object get(final String statsDate, final String appId,
      final Continuation<? super DailyAppReels> $completion) {
    final String _sql = "SELECT * FROM daily_app_reels WHERE statsDate = ? AND appId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, statsDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, appId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyAppReels>() {
      @Override
      @Nullable
      public DailyAppReels call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStatsDate = CursorUtil.getColumnIndexOrThrow(_cursor, "statsDate");
          final int _cursorIndexOfAppId = CursorUtil.getColumnIndexOrThrow(_cursor, "appId");
          final int _cursorIndexOfCount = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
          final DailyAppReels _result;
          if (_cursor.moveToFirst()) {
            final String _tmpStatsDate;
            _tmpStatsDate = _cursor.getString(_cursorIndexOfStatsDate);
            final String _tmpAppId;
            _tmpAppId = _cursor.getString(_cursorIndexOfAppId);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _result = new DailyAppReels(_tmpStatsDate,_tmpAppId,_tmpCount);
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
