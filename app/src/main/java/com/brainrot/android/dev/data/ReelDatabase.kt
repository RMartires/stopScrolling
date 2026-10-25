package com.brainrot.android.dev.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailyAppReels::class], version = 1, exportSchema = false)
abstract class ReelDatabase : RoomDatabase() {
    abstract fun reelDao(): ReelDao

    companion object {
        fun build(context: Context): ReelDatabase =
            Room.databaseBuilder(context, ReelDatabase::class.java, "reel_analytics.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
