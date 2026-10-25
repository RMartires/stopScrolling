package com.brainrot.android.dev.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [DailyAppReels::class, DailyTrackingCoverage::class],
    version = 2,
    exportSchema = false,
)
abstract class ReelDatabase : RoomDatabase() {
    abstract fun reelDao(): ReelDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_app_reels_new (
                        statsDate TEXT NOT NULL,
                        appId TEXT NOT NULL,
                        count INTEGER NOT NULL,
                        origin TEXT NOT NULL DEFAULT 'REAL',
                        PRIMARY KEY(statsDate, appId, origin)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    INSERT INTO daily_app_reels_new(statsDate, appId, count, origin)
                    SELECT statsDate, appId, count, 'REAL' FROM daily_app_reels
                    """.trimIndent(),
                )
                db.execSQL("DROP TABLE daily_app_reels")
                db.execSQL("ALTER TABLE daily_app_reels_new RENAME TO daily_app_reels")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_tracking_coverage (
                        statsDate TEXT NOT NULL,
                        origin TEXT NOT NULL DEFAULT 'REAL',
                        PRIMARY KEY(statsDate, origin)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO daily_tracking_coverage(statsDate, origin)
                    SELECT DISTINCT statsDate, 'REAL' FROM daily_app_reels
                    """.trimIndent(),
                )
            }
        }

        fun build(context: Context): ReelDatabase =
            Room.databaseBuilder(context, ReelDatabase::class.java, "reel_analytics.db")
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
