package com.brainrot.android.dev

import android.content.Context
import com.brainrot.android.dev.analytics.AnalyticsCalculator
import com.brainrot.android.dev.analytics.AnalyticsPeriod
import com.brainrot.android.dev.analytics.AnalyticsSnapshot
import com.brainrot.android.dev.analytics.AnalyticsUiState
import com.brainrot.android.dev.analytics.CalendarDay
import com.brainrot.android.dev.analytics.DateBounds
import com.brainrot.android.dev.analytics.DayAvailability
import com.brainrot.android.dev.analytics.DemoOperation
import com.brainrot.android.dev.analytics.MonthSummary
import com.brainrot.android.dev.analytics.PeriodComparison
import com.brainrot.android.dev.data.DailyAppReels
import com.brainrot.android.dev.data.DailyTrackingCoverage
import com.brainrot.android.dev.data.DataOrigin
import com.brainrot.android.dev.data.ReelDatabase
import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ReelRepository(
    context: Context,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    private val db = ReelDatabase.build(context.applicationContext)
    private val dao = db.reelDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _foregroundApp = MutableStateFlow<String?>(null)
    val foregroundApp: StateFlow<String?> = _foregroundApp.asStateFlow()

    private val _demoOperation = MutableStateFlow<DemoOperation>(DemoOperation.Idle)
    val demoOperation: StateFlow<DemoOperation> = _demoOperation.asStateFlow()

    private val trackingReadiness = MutableStateFlow(
        TrackingReadiness(
            accessibilityEnabled = false,
            overlayEnabled = false,
        ),
    )

    private var lastCountAtMs = 0L
    private var lastSource: String? = null

    fun today(): LocalDate = LocalDate.now(clock)

    fun observeAnalytics(period: AnalyticsPeriod): Flow<AnalyticsSnapshot> {
        val bounds = AnalyticsCalculator.boundsFor(today(), period)
        val previous = AnalyticsCalculator.previousBoundsFor(period, bounds)

        return combine(
            dao.observeRowsBetween(bounds.start.toString(), bounds.end.toString()),
            dao.observeCoverageBetween(bounds.start.toString(), bounds.end.toString()),
            dao.observeRowsBetween(previous.start.toString(), previous.end.toString()),
            dao.observeCoverageBetween(previous.start.toString(), previous.end.toString()),
        ) { rows, coverage, previousRows, previousCoverage ->
            buildSnapshot(
                period = period,
                bounds = bounds,
                rows = rows,
                coverage = coverage,
                previousRows = previousRows,
                previousCoverage = previousCoverage,
                previousBounds = previous,
            )
        }
    }

    fun observeUiState(period: AnalyticsPeriod): Flow<AnalyticsUiState> =
        combine(
            observeAnalytics(period),
            trackingReadiness,
        ) { snapshot, readiness ->
            val state: AnalyticsUiState = when {
                snapshot.availableDays > 0 -> AnalyticsUiState.Content(snapshot)
                !readiness.accessibilityEnabled -> AnalyticsUiState.PermissionBlocked(
                    missingAccessibility = true,
                    missingOverlay = !readiness.overlayEnabled,
                )
                else -> AnalyticsUiState.Empty(
                    period = period,
                    trackingReady = true,
                )
            }
            state
        }.onStart {
            emit(AnalyticsUiState.Loading)
        }
        .catch { error ->
            emit(
                AnalyticsUiState.Error(
                    message = error.message ?: "Analytics could not be loaded",
                ),
            )
        }

    fun observeTodayTotal(): Flow<Int> =
        observeAnalytics(AnalyticsPeriod.TODAY).map { it.total }

    fun recordReel(context: Context, app: TrackedApp, source: String) {
        val now = System.currentTimeMillis()
        val minGapMs = when (source) {
            "scroll" -> 700L
            "content" -> 1800L
            else -> 1200L
        }
        if (now - lastCountAtMs < minGapMs && source == lastSource) return
        lastCountAtMs = now
        lastSource = source

        scope.launch {
            val today = today().toString()
            dao.increment(today, app.appId)
            val total = dao.totalBetween(today, today)
            OverlayManager.refresh(context, total)
        }
    }

    fun setForegroundApp(label: String?) {
        _foregroundApp.value = label
    }

    fun updateTrackingReadiness(
        accessibilityEnabled: Boolean,
        overlayEnabled: Boolean,
    ) {
        trackingReadiness.value = TrackingReadiness(
            accessibilityEnabled = accessibilityEnabled,
            overlayEnabled = overlayEnabled,
        )
    }

    fun clearAll(context: Context) {
        scope.launch {
            dao.clearAll()
            OverlayManager.refresh(context, 0)
        }
    }

    fun generateThirtyDayDemo(context: Context) {
        if (_demoOperation.value == DemoOperation.Running) return
        scope.launch {
            _demoOperation.value = DemoOperation.Running
            runCatching {
                val fixture = AnalyticsCalculator.buildDemoFixture(today())
                dao.replaceRangeWithDemo(
                    startDate = fixture.start.toString(),
                    endDate = fixture.end.toString(),
                    rows = fixture.rows,
                    coverage = fixture.coverage,
                )
                OverlayManager.refresh(
                    context,
                    dao.totalBetween(fixture.end.toString(), fixture.end.toString()),
                )
            }.onSuccess {
                _demoOperation.value = DemoOperation.Success("30 days of demo data generated")
            }.onFailure { error ->
                _demoOperation.value = DemoOperation.Error(
                    error.message ?: "Could not generate demo data",
                )
            }
        }
    }

    fun clearDemoData(context: Context) {
        if (_demoOperation.value == DemoOperation.Running) return
        scope.launch {
            _demoOperation.value = DemoOperation.Running
            runCatching {
                dao.clearDemoData()
                val today = today().toString()
                OverlayManager.refresh(context, dao.totalBetween(today, today))
            }.onSuccess {
                _demoOperation.value = DemoOperation.Success("Demo data cleared")
            }.onFailure { error ->
                _demoOperation.value = DemoOperation.Error(
                    error.message ?: "Could not clear demo data",
                )
            }
        }
    }

    fun dismissDemoMessage() {
        _demoOperation.value = DemoOperation.Idle
    }

    fun refreshOverlay(context: Context) {
        scope.launch {
            val today = today().toString()
            OverlayManager.refresh(context, dao.totalBetween(today, today))
        }
    }

    private fun buildSnapshot(
        period: AnalyticsPeriod,
        bounds: DateBounds,
        rows: List<DailyAppReels>,
        coverage: List<DailyTrackingCoverage>,
        previousRows: List<DailyAppReels>,
        previousCoverage: List<DailyTrackingCoverage>,
        previousBounds: DateBounds,
    ): AnalyticsSnapshot {
        val today = today()
        val rowsByDate = rows.groupBy { LocalDate.parse(it.statsDate) }
        val coverageByDate = coverage.groupBy { LocalDate.parse(it.statsDate) }
        val days = generateSequence(bounds.start) { date ->
            date.plusDays(1).takeIf { !it.isAfter(bounds.end) }
        }.map { date ->
            val dateRows = rowsByDate[date].orEmpty()
            val appCounts = dateRows
                .groupBy { it.appId }
                .mapValues { (_, appRows) -> appRows.sumOf { it.count } }
            val hasCoverage = coverageByDate[date].orEmpty().isNotEmpty()
            val total = appCounts.values.sum()
            val availability = when {
                date.isAfter(today) -> DayAvailability.FUTURE
                hasCoverage && total == 0 -> DayAvailability.TRACKED_ZERO
                hasCoverage -> DayAvailability.TRACKED
                else -> DayAvailability.UNAVAILABLE
            }
            CalendarDay(
                date = date,
                total = total,
                appCounts = appCounts,
                availability = availability,
                isDemo = dateRows.any { it.origin == DataOrigin.DEMO.name } ||
                    coverageByDate[date].orEmpty().any { it.origin == DataOrigin.DEMO.name },
            )
        }.toList()

        val total = rows.sumOf { it.count }
        val appCounts = rows
            .groupBy { it.appId }
            .mapValues { (_, appRows) -> appRows.sumOf { it.count } }
        val appShares = AnalyticsCalculator.allocateShares(appCounts, total)
        val availableDays = days.count {
            it.availability == DayAvailability.TRACKED ||
                it.availability == DayAvailability.TRACKED_ZERO
        }
        val expectedDays = days.count { !it.date.isAfter(today) }
        val previousExpectedDays = ChronoUnit.DAYS.between(
            previousBounds.start,
            previousBounds.end,
        ).toInt() + 1
        val previousAvailableDays = previousCoverage.map { it.statsDate }.distinct().size
        val previousTotal = previousRows.sumOf { it.count }
        val comparison = AnalyticsCalculator.comparisonFor(
            total = total,
            availableDays = availableDays,
            expectedDays = expectedDays,
            previousTotal = previousTotal,
            previousAvailableDays = previousAvailableDays,
            previousExpectedDays = previousExpectedDays,
        )
        val months = days
            .groupBy { it.date.toString().take(7) }
            .map { (monthKey, monthDays) ->
                MonthSummary(
                    monthKey = monthKey,
                    total = monthDays.sumOf { it.total },
                    availableDays = monthDays.count {
                        it.availability == DayAvailability.TRACKED ||
                            it.availability == DayAvailability.TRACKED_ZERO
                    },
                )
            }

        return AnalyticsSnapshot(
            period = period,
            startDate = bounds.start,
            endDate = bounds.end,
            total = total,
            days = days,
            appShares = appShares,
            months = months,
            availableDays = availableDays,
            expectedDays = expectedDays,
            isDemo = days.any { it.isDemo },
            comparison = comparison,
            insight = AnalyticsCalculator.insightFor(total, appShares, comparison),
        )
    }

    private data class TrackingReadiness(
        val accessibilityEnabled: Boolean,
        val overlayEnabled: Boolean,
    )

}
