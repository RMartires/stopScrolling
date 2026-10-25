package com.brainrot.android.dev.analytics

import com.brainrot.android.dev.TrackedApp
import com.brainrot.android.dev.data.DailyAppReels
import com.brainrot.android.dev.data.DailyTrackingCoverage
import com.brainrot.android.dev.data.DataOrigin
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

internal data class DateBounds(
    val start: LocalDate,
    val end: LocalDate,
)

internal data class DemoFixture(
    val start: LocalDate,
    val end: LocalDate,
    val rows: List<DailyAppReels>,
    val coverage: List<DailyTrackingCoverage>,
)

internal object AnalyticsCalculator {
    private const val DEMO_SEED = 619
    private const val COMPARISON_COVERAGE = 0.7f

    fun boundsFor(today: LocalDate, period: AnalyticsPeriod): DateBounds = when (period) {
        AnalyticsPeriod.TODAY -> DateBounds(today, today)
        AnalyticsPeriod.THIRTY_DAYS -> DateBounds(today.minusDays(29), today)
        AnalyticsPeriod.YEAR -> DateBounds(
            LocalDate.of(today.year, 1, 1),
            LocalDate.of(today.year, 12, 31),
        )
    }

    fun previousBoundsFor(period: AnalyticsPeriod, current: DateBounds): DateBounds =
        when (period) {
            AnalyticsPeriod.TODAY -> DateBounds(
                current.start.minusDays(1),
                current.end.minusDays(1),
            )
            AnalyticsPeriod.THIRTY_DAYS -> DateBounds(
                current.start.minusDays(30),
                current.end.minusDays(30),
            )
            AnalyticsPeriod.YEAR -> DateBounds(
                current.start.minusYears(1),
                current.end.minusYears(1),
            )
        }

    fun allocateShares(appCounts: Map<String, Int>, total: Int): List<AppShare> {
        if (total <= 0) return emptyList()
        data class Draft(
            val app: TrackedApp,
            val count: Int,
            val floorPercent: Int,
            val remainder: Double,
        )

        val drafts = appCounts.mapNotNull { (appId, count) ->
            if (count <= 0) return@mapNotNull null
            val app = TrackedApp.entries.firstOrNull { it.appId == appId } ?: return@mapNotNull null
            val exact = count.toDouble() * 100 / total
            Draft(app, count, floor(exact).toInt(), exact - floor(exact))
        }
        val bonusApps = drafts
            .sortedWith(compareByDescending<Draft> { it.remainder }.thenBy { it.app.ordinal })
            .take((100 - drafts.sumOf { it.floorPercent }).coerceAtLeast(0))
            .map { it.app }
            .toSet()
        return drafts
            .map { draft ->
                AppShare(
                    app = draft.app,
                    count = draft.count,
                    percent = draft.floorPercent + if (draft.app in bonusApps) 1 else 0,
                )
            }
            .sortedByDescending { it.count }
    }

    fun insightFor(
        total: Int,
        appShares: List<AppShare>,
        comparison: PeriodComparison?,
    ): String = when {
        total == 0 -> "No tracked reels in this period."
        comparison != null && comparison.percentChange < 0 ->
            "${abs(comparison.percentChange)}% fewer reels than the previous period."
        comparison != null && comparison.percentChange > 0 ->
            "${comparison.percentChange}% more reels than the previous period."
        comparison != null -> "Reel count is unchanged from the previous period."
        appShares.isNotEmpty() ->
            "${appShares.first().app.label} accounts for " +
                "${appShares.first().percent}% of tracked reels."
        else -> "Not enough history for a comparison yet."
    }

    fun comparisonFor(
        total: Int,
        availableDays: Int,
        expectedDays: Int,
        previousTotal: Int,
        previousAvailableDays: Int,
        previousExpectedDays: Int,
    ): PeriodComparison? {
        val currentCoverage =
            if (expectedDays == 0) 0f else availableDays.toFloat() / expectedDays
        val previousCoverage =
            if (previousExpectedDays == 0) 0f
            else previousAvailableDays.toFloat() / previousExpectedDays
        if (currentCoverage < COMPARISON_COVERAGE ||
            previousCoverage < COMPARISON_COVERAGE
        ) {
            return null
        }
        val change = when {
            previousTotal == 0 && total == 0 -> 0
            previousTotal == 0 -> 100
            else -> (((total - previousTotal).toDouble() / previousTotal) * 100).roundToInt()
        }
        return PeriodComparison(previousTotal = previousTotal, percentChange = change)
    }

    fun buildDemoFixture(end: LocalDate): DemoFixture {
        val start = end.minusDays(29)
        val random = Random(DEMO_SEED)
        val rows = buildList {
            for (offset in 0L..29L) {
                val date = start.plusDays(offset)
                val weekendBoost = if (date.dayOfWeek.value >= 6) 5 else 0
                val trendDrop = (offset / 8L).toInt()
                val youtube = (random.nextInt(4, 15) + weekendBoost - trendDrop)
                    .coerceAtLeast(1)
                val instagram = random.nextInt(1, 9)
                val tiktok = if (offset % 4L == 0L) random.nextInt(0, 5) else 0
                addDemoRow(date, TrackedApp.YOUTUBE, youtube)
                addDemoRow(date, TrackedApp.INSTAGRAM, instagram)
                if (tiktok > 0) addDemoRow(date, TrackedApp.TIKTOK, tiktok)
            }
        }
        val coverage = (0L..29L).map { offset ->
            DailyTrackingCoverage(
                statsDate = start.plusDays(offset).toString(),
                origin = DataOrigin.DEMO.name,
            )
        }
        return DemoFixture(start, end, rows, coverage)
    }

    private fun MutableList<DailyAppReels>.addDemoRow(
        date: LocalDate,
        app: TrackedApp,
        count: Int,
    ) {
        add(
            DailyAppReels(
                statsDate = date.toString(),
                appId = app.appId,
                count = count,
                origin = DataOrigin.DEMO.name,
            ),
        )
    }
}
