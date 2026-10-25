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

  private final EntityInsertionAdapter<DailyTrackingCoverage> __insertionAdapterOfDailyTrackingCoverage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRowsBetween;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCoverageBetween;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDemoRows;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDemoCoverage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllRows;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllCoverage;

  public ReelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyAppReels = new EntityInsertionAdapter<DailyAppReels>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_app_reels` (`statsDate`,`appId`,`count`,`origin`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyAppReels entity) {
        statement.bindString(1, entity.getStatsDate());
        statement.bindString(2, entity.getAppId());
        statement.bindLong(3, entity.getCount());
        statement.bindString(4, entity.getOrigin());
      }
    };
    this.__insertionAdapterOfDailyTrackingCoverage = new EntityInsertionAdapter<DailyTrackingCoverage>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_tracking_coverage` (`statsDate`,`origin`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyTrackingCoverage entity) {
        statement.bindString(1, entity.getStatsDate());
        statement.bindString(2, entity.getOrigin());
      }
    };
    this.__preparedStmtOfDeleteRowsBetween = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_app_reels WHERE statsDate BETWEEN ? AND ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteCoverageBetween = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_tracking_coverage WHERE statsDate BETWEEN ? AND ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDemoRows = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_app_reels WHERE origin = 'DEMO'";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDemoCoverage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_tracking_coverage WHERE origin = 'DEMO'";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllRows = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_app_reels";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllCoverage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_tracking_coverage";
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
  public Object upsertAll(final List<DailyAppReels> rows,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyAppReels.insert(rows);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertCoverage(final List<DailyTrackingCoverage> rows,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyTrackingCoverage.insert(rows);
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
  public Object replaceRangeWithDemo(final String startDate, final String endDate,
      final List<DailyAppReels> rows, final List<DailyTrackingCoverage> coverage,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ReelDao.DefaultImpls.replaceRangeWithDemo(ReelDao_Impl.this, startDate, endDate, rows, coverage, __cont), $completion);
  }

  @Override
  public Object clearDemoData(final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ReelDao.DefaultImpls.clearDemoData(ReelDao_Impl.this, __cont), $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ReelDao.DefaultImpls.clearAll(ReelDao_Impl.this, __cont), $completion);
  }

  @Override
  public Object deleteRowsBetween(final String startDate, final String endDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRowsBetween.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, startDate);
        _argIndex = 2;
        _stmt.bindString(_argIndex, endDate);
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
          __preparedStmtOfDeleteRowsBetween.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCoverageBetween(final String startDate, final String endDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCoverageBetween.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, startDate);
        _argIndex = 2;
        _stmt.bindString(_argIndex, endDate);
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
          __preparedStmtOfDeleteCoverageBetween.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDemoRows(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDemoRows.acquire();
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
          __preparedStmtOfDeleteDemoRows.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDemoCoverage(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDemoCoverage.acquire();
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
          __preparedStmtOfDeleteDemoCoverage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllRows(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllRows.acquire();
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
          __preparedStmtOfDeleteAllRows.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllCoverage(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllCoverage.acquire();
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
          __preparedStmtOfDeleteAllCoverage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyAppReels>> observeRowsBetween(final String startDate,
      final String endDate) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_app_reels\n"
            + "        WHERE statsDate BETWEEN ? AND ?\n"
            + "        ORDER BY statsDate ASC, appId ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_app_reels"}, new Callable<List<DailyAppReels>>() {
      @Override
      @NonNull
      public List<DailyAppReels> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStatsDate = CursorUtil.getColumnIndexOrThrow(_cursor, "statsDate");
          final int _cursorIndexOfAppId = CursorUtil.getColumnIndexOrThrow(_cursor, "appId");
          final int _cursorIndexOfCount = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final List<DailyAppReels> _result = new ArrayList<DailyAppReels>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyAppReels _item;
            final String _tmpStatsDate;
            _tmpStatsDate = _cursor.getString(_cursorIndexOfStatsDate);
            final String _tmpAppId;
            _tmpAppId = _cursor.getString(_cursorIndexOfAppId);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            _item = new DailyAppReels(_tmpStatsDate,_tmpAppId,_tmpCount,_tmpOrigin);
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
  public Flow<List<DailyTrackingCoverage>> observeCoverageBetween(final String startDate,
      final String endDate) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_tracking_coverage\n"
            + "        WHERE statsDate BETWEEN ? AND ?\n"
            + "        ORDER BY statsDate ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_tracking_coverage"}, new Callable<List<DailyTrackingCoverage>>() {
      @Override
      @NonNull
      public List<DailyTrackingCoverage> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStatsDate = CursorUtil.getColumnIndexOrThrow(_cursor, "statsDate");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final List<DailyTrackingCoverage> _result = new ArrayList<DailyTrackingCoverage>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTrackingCoverage _item;
            final String _tmpStatsDate;
            _tmpStatsDate = _cursor.getString(_cursorIndexOfStatsDate);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            _item = new DailyTrackingCoverage(_tmpStatsDate,_tmpOrigin);
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
  public Flow<List<AppTotal>> observeAppTotalsBetween(final String startDate,
      final String endDate) {
    final String _sql = "\n"
            + "        SELECT appId, SUM(count) AS total\n"
            + "        FROM daily_app_reels\n"
            + "        WHERE statsDate BETWEEN ? AND ?\n"
            + "        GROUP BY appId\n"
            + "        ORDER BY total DESC, appId ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_app_reels"}, new Callable<List<AppTotal>>() {
      @Override
      @NonNull
      public List<AppTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAppId = 0;
          final int _cursorIndexOfTotal = 1;
          final List<AppTotal> _result = new ArrayList<AppTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppTotal _item;
            final String _tmpAppId;
            _tmpAppId = _cursor.getString(_cursorIndexOfAppId);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            _item = new AppTotal(_tmpAppId,_tmpTotal);
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
  public Flow<List<DayTotal>> observeDailyTotalsBetween(final String startDate,
      final String endDate) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            statsDate,\n"
            + "            SUM(count) AS total,\n"
            + "            SUM(CASE WHEN origin = 'DEMO' THEN count ELSE 0 END) AS demoCount\n"
            + "        FROM daily_app_reels\n"
            + "        WHERE statsDate BETWEEN ? AND ?\n"
            + "        GROUP BY statsDate\n"
            + "        ORDER BY statsDate ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_app_reels"}, new Callable<List<DayTotal>>() {
      @Override
      @NonNull
      public List<DayTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStatsDate = 0;
          final int _cursorIndexOfTotal = 1;
          final int _cursorIndexOfDemoCount = 2;
          final List<DayTotal> _result = new ArrayList<DayTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayTotal _item;
            final String _tmpStatsDate;
            _tmpStatsDate = _cursor.getString(_cursorIndexOfStatsDate);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final int _tmpDemoCount;
            _tmpDemoCount = _cursor.getInt(_cursorIndexOfDemoCount);
            _item = new DayTotal(_tmpStatsDate,_tmpTotal,_tmpDemoCount);
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
  public Flow<List<MonthTotal>> observeMonthlyTotalsBetween(final String startDate,
      final String endDate) {
    final String _sql = "\n"
            + "        SELECT substr(statsDate, 1, 7) AS monthKey, SUM(count) AS total\n"
            + "        FROM daily_app_reels\n"
            + "        WHERE statsDate BETWEEN ? AND ?\n"
            + "        GROUP BY monthKey\n"
            + "        ORDER BY monthKey ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_app_reels"}, new Callable<List<MonthTotal>>() {
      @Override
      @NonNull
      public List<MonthTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMonthKey = 0;
          final int _cursorIndexOfTotal = 1;
          final List<MonthTotal> _result = new ArrayList<MonthTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonthTotal _item;
            final String _tmpMonthKey;
            _tmpMonthKey = _cursor.getString(_cursorIndexOfMonthKey);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            _item = new MonthTotal(_tmpMonthKey,_tmpTotal);
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
  public Object totalBetween(final String startDate, final String endDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(count), 0) FROM daily_app_reels\n"
            + "        WHERE statsDate BETWEEN ? AND ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
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
  public Object get(final String statsDate, final String appId, final String origin,
      final Continuation<? super DailyAppReels> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_app_reels\n"
            + "        WHERE statsDate = ? AND appId = ? AND origin = ?\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, statsDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, appId);
    _argIndex = 3;
    _statement.bindString(_argIndex, origin);
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
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final DailyAppReels _result;
          if (_cursor.moveToFirst()) {
            final String _tmpStatsDate;
            _tmpStatsDate = _cursor.getString(_cursorIndexOfStatsDate);
            final String _tmpAppId;
            _tmpAppId = _cursor.getString(_cursorIndexOfAppId);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            _result = new DailyAppReels(_tmpStatsDate,_tmpAppId,_tmpCount,_tmpOrigin);
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
