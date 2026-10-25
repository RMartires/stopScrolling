package com.brainrot.android.dev.analytics

import com.brainrot.android.dev.TrackedApp
import com.brainrot.android.dev.data.DataOrigin
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsCalculatorTest {
    @Test
    fun thirtyDayBoundsContainExactlyThirtyCalendarDays() {
        val today = LocalDate.of(2026, 10, 25)
        val bounds = AnalyticsCalculator.boundsFor(today, AnalyticsPeriod.THIRTY_DAYS)

        assertEquals(LocalDate.of(2026, 9, 26), bounds.start)
        assertEquals(today, bounds.end)
        assertEquals(
            LocalDate.of(2026, 8, 27),
            AnalyticsCalculator.previousBoundsFor(
                AnalyticsPeriod.THIRTY_DAYS,
                bounds,
            ).start,
        )
    }

    @Test
    fun yearBoundsIncludeLeapDay() {
        val bounds = AnalyticsCalculator.boundsFor(
            LocalDate.of(2024, 6, 1),
            AnalyticsPeriod.YEAR,
        )

        assertEquals(LocalDate.of(2024, 1, 1), bounds.start)
        assertEquals(LocalDate.of(2024, 12, 31), bounds.end)
        assertEquals(366, bounds.start.datesUntil(bounds.end.plusDays(1)).count())
    }

    @Test
    fun appSharesUseLargestRemainderAndReconcileToOneHundred() {
        val shares = AnalyticsCalculator.allocateShares(
            mapOf(
                TrackedApp.YOUTUBE.appId to 1,
                TrackedApp.INSTAGRAM.appId to 1,
                TrackedApp.TIKTOK.appId to 1,
            ),
            total = 3,
        )

        assertEquals(100, shares.sumOf { it.percent })
        assertEquals(listOf(34, 33, 33), shares.map { it.percent })
        assertFalse(shares.any { it.count == 0 })
    }

    @Test
    fun comparisonRequiresSeventyPercentCoverageInBothPeriods() {
        assertNull(
            AnalyticsCalculator.comparisonFor(
                total = 20,
                availableDays = 20,
                expectedDays = 30,
                previousTotal = 10,
                previousAvailableDays = 30,
                previousExpectedDays = 30,
            ),
        )
        assertEquals(
            100,
            AnalyticsCalculator.comparisonFor(
                total = 20,
                availableDays = 21,
                expectedDays = 30,
                previousTotal = 10,
                previousAvailableDays = 21,
                previousExpectedDays = 30,
            )?.percentChange,
        )
    }

    @Test
    fun demoFixtureIsDeterministicCompleteAndDemoTagged() {
        val end = LocalDate.of(2026, 10, 25)
        val first = AnalyticsCalculator.buildDemoFixture(end)
        val second = AnalyticsCalculator.buildDemoFixture(end)

        assertEquals(first, second)
        assertEquals(end.minusDays(29), first.start)
        assertEquals(30, first.coverage.size)
        assertEquals(30, first.coverage.map { it.statsDate }.distinct().size)
        assertTrue(first.rows.all { it.origin == DataOrigin.DEMO.name && it.count > 0 })
        assertTrue(first.coverage.all { it.origin == DataOrigin.DEMO.name })
    }

    @Test
    fun insightsAreNeutralDeterministicAndPriorityOrdered() {
        val shares = AnalyticsCalculator.allocateShares(
            mapOf(TrackedApp.YOUTUBE.appId to 8, TrackedApp.INSTAGRAM.appId to 2),
            total = 10,
        )

        assertEquals(
            "25% fewer reels than the previous period.",
            AnalyticsCalculator.insightFor(
                total = 15,
                appShares = shares,
                comparison = PeriodComparison(previousTotal = 20, percentChange = -25),
            ),
        )
        assertEquals(
            "YouTube accounts for 80% of tracked reels.",
            AnalyticsCalculator.insightFor(10, shares, comparison = null),
        )
    }
}
