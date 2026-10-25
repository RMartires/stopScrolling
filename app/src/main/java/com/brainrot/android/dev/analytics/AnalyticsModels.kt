package com.brainrot.android.dev.analytics

import com.brainrot.android.dev.TrackedApp
import java.time.LocalDate

enum class AnalyticsPeriod {
    TODAY,
    THIRTY_DAYS,
    YEAR,
}

enum class DayAvailability {
    TRACKED,
    TRACKED_ZERO,
    UNAVAILABLE,
    FUTURE,
}

data class CalendarDay(
    val date: LocalDate,
    val total: Int,
    val appCounts: Map<String, Int>,
    val availability: DayAvailability,
    val isDemo: Boolean,
)

data class AppShare(
    val app: TrackedApp,
    val count: Int,
    val percent: Int,
)

data class MonthSummary(
    val monthKey: String,
    val total: Int,
    val availableDays: Int,
)

data class PeriodComparison(
    val previousTotal: Int,
    val percentChange: Int,
)

data class AnalyticsSnapshot(
    val period: AnalyticsPeriod,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val total: Int,
    val days: List<CalendarDay>,
    val appShares: List<AppShare>,
    val months: List<MonthSummary>,
    val availableDays: Int,
    val expectedDays: Int,
    val isDemo: Boolean,
    val comparison: PeriodComparison?,
    val insight: String,
)

sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState

    data class PermissionBlocked(
        val missingAccessibility: Boolean,
        val missingOverlay: Boolean,
    ) : AnalyticsUiState

    data class Empty(
        val period: AnalyticsPeriod,
        val trackingReady: Boolean,
    ) : AnalyticsUiState

    data class Content(
        val snapshot: AnalyticsSnapshot,
    ) : AnalyticsUiState

    data class Error(
        val message: String,
        val previous: AnalyticsSnapshot? = null,
    ) : AnalyticsUiState
}

sealed interface DemoOperation {
    data object Idle : DemoOperation
    data object Running : DemoOperation
    data class Success(val message: String) : DemoOperation
    data class Error(val message: String) : DemoOperation
}
