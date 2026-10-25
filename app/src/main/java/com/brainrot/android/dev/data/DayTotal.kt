package com.brainrot.android.dev.data

data class DayTotal(
    val statsDate: String,
    val total: Int,
    val demoCount: Int = 0,
)

data class AppTotal(
    val appId: String,
    val total: Int,
)

data class MonthTotal(
    val monthKey: String,
    val total: Int,
)
