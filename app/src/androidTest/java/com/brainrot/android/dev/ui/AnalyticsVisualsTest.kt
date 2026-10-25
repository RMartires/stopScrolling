package com.brainrot.android.dev.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.brainrot.android.dev.TrackedApp
import com.brainrot.android.dev.analytics.AnalyticsPeriod
import com.brainrot.android.dev.analytics.AppShare
import com.brainrot.android.dev.analytics.CalendarDay
import com.brainrot.android.dev.analytics.DayAvailability
import com.brainrot.android.dev.ui.components.AnalyticsLoading
import com.brainrot.android.dev.ui.components.AnalyticsMessage
import com.brainrot.android.dev.ui.components.AppShareStory
import com.brainrot.android.dev.ui.components.PeriodSelector
import com.brainrot.android.dev.ui.components.YearHeatmap
import com.brainrot.android.dev.ui.theme.StopScrollingTheme
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AnalyticsVisualsTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun rangeSelectorExposesSelectionAndChangesRange() {
        var selected = AnalyticsPeriod.THIRTY_DAYS
        compose.setContent {
            StopScrollingTheme {
                PeriodSelector(
                    selectedPeriod = selected,
                    onSelected = { selected = it },
                )
            }
        }

        compose.onNodeWithContentDescription("30 days analytics").assertIsSelected()
        compose.onNodeWithContentDescription("Year analytics").performClick()
        compose.runOnIdle { assertEquals(AnalyticsPeriod.YEAR, selected) }
    }

    @Test
    fun appShareExposesExactTalkBackValuesAndSelection() {
        compose.setContent {
            StopScrollingTheme {
                AppShareStory(
                    shares = listOf(
                        AppShare(TrackedApp.YOUTUBE, count = 72, percent = 72),
                        AppShare(TrackedApp.INSTAGRAM, count = 28, percent = 28),
                    ),
                    selectedAppId = TrackedApp.YOUTUBE.appId,
                    onSelectApp = {},
                )
            }
        }

        compose.onNodeWithContentDescription("YouTube, 72 reels, 72 percent")
            .assertIsSelected()
        compose.onNodeWithContentDescription("Instagram, 28 reels, 28 percent")
            .assertIsDisplayed()
    }

    @Test
    fun heatmapDistinguishesUnavailableZeroAndFutureSemantically() {
        val start = LocalDate.of(2026, 1, 1)
        compose.setContent {
            StopScrollingTheme {
                YearHeatmap(
                    days = listOf(
                        CalendarDay(
                            start,
                            0,
                            emptyMap(),
                            DayAvailability.UNAVAILABLE,
                            false,
                        ),
                        CalendarDay(
                            start.plusDays(1),
                            0,
                            emptyMap(),
                            DayAvailability.TRACKED_ZERO,
                            false,
                        ),
                        CalendarDay(
                            start.plusDays(2),
                            0,
                            emptyMap(),
                            DayAvailability.FUTURE,
                            false,
                        ),
                    ),
                    selectedDate = null,
                    onSelectDate = {},
                )
            }
        }

        compose.onNodeWithContentDescription("Jan 1, unavailable").assertIsDisplayed()
        compose.onNodeWithContentDescription("Jan 2, zero tracked reels").assertIsDisplayed()
        compose.onNodeWithContentDescription("Jan 3, future date").assertIsDisplayed()
    }

    @Test
    fun loadingBlockedAndEmptyStateGeometryRendersForScreenshots() {
        compose.setContent {
            StopScrollingTheme {
                AnalyticsLoading()
                AnalyticsMessage(
                    title = "Tracking is paused",
                    body = "Complete setup to collect new activity.",
                )
            }
        }

        compose.onNodeWithText("Tracking is paused").assertIsDisplayed()
        val image = compose.onNodeWithText("Tracking is paused").captureToImage()
        assertTrue(image.width > 0)
        assertTrue(image.height > 0)
    }
}
