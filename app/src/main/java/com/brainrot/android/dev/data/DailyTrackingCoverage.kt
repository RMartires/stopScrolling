package com.brainrot.android.dev.data

import androidx.room.Entity

@Entity(
    tableName = "daily_tracking_coverage",
    primaryKeys = ["statsDate", "origin"],
)
data class DailyTrackingCoverage(
    val statsDate: String,
    val origin: String = DataOrigin.REAL.name,
)
