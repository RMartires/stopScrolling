package com.brainrot.android.dev.data

import androidx.room.Entity

@Entity(tableName = "daily_app_reels", primaryKeys = ["statsDate", "appId"])
data class DailyAppReels(
    val statsDate: String,
    val appId: String,
    val count: Int,
)
