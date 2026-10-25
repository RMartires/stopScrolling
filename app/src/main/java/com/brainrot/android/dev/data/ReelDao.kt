package com.brainrot.android.dev.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query(
        """
        SELECT * FROM daily_app_reels
        WHERE statsDate BETWEEN :startDate AND :endDate
        ORDER BY statsDate ASC, appId ASC
        """,
    )
    fun observeRowsBetween(startDate: String, endDate: String): Flow<List<DailyAppReels>>

    @Query(
        """
        SELECT * FROM daily_tracking_coverage
        WHERE statsDate BETWEEN :startDate AND :endDate
        ORDER BY statsDate ASC
        """,
    )
    fun observeCoverageBetween(
        startDate: String,
        endDate: String,
    ): Flow<List<DailyTrackingCoverage>>

    @Query(
        """
        SELECT appId, SUM(count) AS total
        FROM daily_app_reels
        WHERE statsDate BETWEEN :startDate AND :endDate
        GROUP BY appId
        ORDER BY total DESC, appId ASC
        """,
    )
    fun observeAppTotalsBetween(startDate: String, endDate: String): Flow<List<AppTotal>>

    @Query(
        """
        SELECT
            statsDate,
            SUM(count) AS total,
            SUM(CASE WHEN origin = 'DEMO' THEN count ELSE 0 END) AS demoCount
        FROM daily_app_reels
        WHERE statsDate BETWEEN :startDate AND :endDate
        GROUP BY statsDate
        ORDER BY statsDate ASC
        """,
    )
    fun observeDailyTotalsBetween(startDate: String, endDate: String): Flow<List<DayTotal>>

    @Query(
        """
        SELECT substr(statsDate, 1, 7) AS monthKey, SUM(count) AS total
        FROM daily_app_reels
        WHERE statsDate BETWEEN :startDate AND :endDate
        GROUP BY monthKey
        ORDER BY monthKey ASC
        """,
    )
    fun observeMonthlyTotalsBetween(startDate: String, endDate: String): Flow<List<MonthTotal>>

    @Query(
        """
        SELECT COALESCE(SUM(count), 0) FROM daily_app_reels
        WHERE statsDate BETWEEN :startDate AND :endDate
        """,
    )
    suspend fun totalBetween(startDate: String, endDate: String): Int

    @Query(
        """
        SELECT * FROM daily_app_reels
        WHERE statsDate = :statsDate AND appId = :appId AND origin = :origin
        LIMIT 1
        """,
    )
    suspend fun get(
        statsDate: String,
        appId: String,
        origin: String,
    ): DailyAppReels?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(row: DailyAppReels)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<DailyAppReels>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCoverage(rows: List<DailyTrackingCoverage>)

    @Query("DELETE FROM daily_app_reels WHERE statsDate BETWEEN :startDate AND :endDate")
    suspend fun deleteRowsBetween(startDate: String, endDate: String)

    @Query("DELETE FROM daily_tracking_coverage WHERE statsDate BETWEEN :startDate AND :endDate")
    suspend fun deleteCoverageBetween(startDate: String, endDate: String)

    @Query("DELETE FROM daily_app_reels WHERE origin = 'DEMO'")
    suspend fun deleteDemoRows()

    @Query("DELETE FROM daily_tracking_coverage WHERE origin = 'DEMO'")
    suspend fun deleteDemoCoverage()

    @Query("DELETE FROM daily_app_reels")
    suspend fun deleteAllRows()

    @Query("DELETE FROM daily_tracking_coverage")
    suspend fun deleteAllCoverage()

    @Transaction
    suspend fun increment(statsDate: String, appId: String) {
        val origin = DataOrigin.REAL.name
        val existing = get(statsDate, appId, origin)
        if (existing == null) {
            upsert(
                DailyAppReels(
                    statsDate = statsDate,
                    appId = appId,
                    count = 1,
                    origin = origin,
                ),
            )
        } else {
            upsert(existing.copy(count = existing.count + 1))
        }
        upsertCoverage(
            listOf(
                DailyTrackingCoverage(
                    statsDate = statsDate,
                    origin = origin,
                ),
            ),
        )
    }

    @Transaction
    suspend fun replaceRangeWithDemo(
        startDate: String,
        endDate: String,
        rows: List<DailyAppReels>,
        coverage: List<DailyTrackingCoverage>,
    ) {
        deleteRowsBetween(startDate, endDate)
        deleteCoverageBetween(startDate, endDate)
        upsertAll(rows)
        upsertCoverage(coverage)
    }

    @Transaction
    suspend fun clearDemoData() {
        deleteDemoRows()
        deleteDemoCoverage()
    }

    @Transaction
    suspend fun clearAll() {
        deleteAllRows()
        deleteAllCoverage()
    }
}
