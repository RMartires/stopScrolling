package com.brainrot.android.dev.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM daily_app_reels WHERE statsDate = :statsDate ORDER BY count DESC")
    fun observeForDate(statsDate: String): Flow<List<DailyAppReels>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM daily_app_reels WHERE statsDate = :statsDate")
    fun observeTotalForDate(statsDate: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(count), 0) FROM daily_app_reels WHERE statsDate = :statsDate")
    suspend fun totalForDate(statsDate: String): Int

    @Query(
        """
        SELECT statsDate, SUM(count) AS total
        FROM daily_app_reels
        GROUP BY statsDate
        ORDER BY statsDate DESC
        LIMIT :limit
        """,
    )
    fun observeDailyTotals(limit: Int): Flow<List<DayTotal>>

    @Query("SELECT * FROM daily_app_reels WHERE statsDate = :statsDate AND appId = :appId LIMIT 1")
    suspend fun get(statsDate: String, appId: String): DailyAppReels?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(row: DailyAppReels)

    @Query("DELETE FROM daily_app_reels")
    suspend fun clearAll()

    @Transaction
    suspend fun increment(statsDate: String, appId: String) {
        val existing = get(statsDate, appId)
        if (existing == null) {
            upsert(DailyAppReels(statsDate = statsDate, appId = appId, count = 1))
        } else {
            upsert(existing.copy(count = existing.count + 1))
        }
    }
}
