package com.brainrot.android.dev.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainrot.android.dev.ReelRepository
import com.brainrot.android.dev.analytics.AnalyticsPeriod
import com.brainrot.android.dev.analytics.AnalyticsSnapshot
import com.brainrot.android.dev.analytics.AnalyticsUiState
import com.brainrot.android.dev.ui.components.AnalyticsLoading
import com.brainrot.android.dev.ui.components.AnalyticsMessage
import com.brainrot.android.dev.ui.components.AppShareStory
import com.brainrot.android.dev.ui.components.CoverageLabel
import com.brainrot.android.dev.ui.components.DailyTrend
import com.brainrot.android.dev.ui.components.DemoBadge
import com.brainrot.android.dev.ui.components.MonthTotals
import com.brainrot.android.dev.ui.components.PeriodSelector
import com.brainrot.android.dev.ui.components.SelectionDetails
import com.brainrot.android.dev.ui.components.YearHeatmap

@Composable
fun AnalyticsScreen(
    repository: ReelRepository,
    onOpenSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var period by rememberSaveable { mutableStateOf(AnalyticsPeriod.THIRTY_DAYS) }
    var selectedAppId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedDate by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedMonth by rememberSaveable { mutableStateOf<String?>(null) }
    val state by repository.observeUiState(period)
        .collectAsStateWithLifecycle(initialValue = AnalyticsUiState.Loading)

    LaunchedEffect(period) {
        selectedAppId = null
        selectedDate = null
        selectedMonth = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
        ) {
            Text("Analytics", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Stored on this device only",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            PeriodSelector(selectedPeriod = period, onSelected = { period = it })
            Spacer(Modifier.height(28.dp))
            when (val current = state) {
                AnalyticsUiState.Loading -> AnalyticsLoading()
                is AnalyticsUiState.PermissionBlocked -> AnalyticsMessage(
                    title = "Tracking is paused",
                    body = "Complete setup to collect new activity. Existing local history " +
                        "will remain on this phone.",
                    actionLabel = "Open setup",
                    onAction = onOpenSetup,
                )
                is AnalyticsUiState.Empty -> AnalyticsMessage(
                    title = "No history for this period",
                    body = "Use a supported app to build your private activity history, " +
                        "or generate test data from Setup.",
                    actionLabel = "Open setup",
                    onAction = onOpenSetup,
                )
                is AnalyticsUiState.Error -> {
                    Text(
                        text = current.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    current.previous?.let {
                        Spacer(Modifier.height(18.dp))
                        AnalyticsContent(
                            snapshot = it,
                            selectedAppId = selectedAppId,
                            selectedDate = selectedDate,
                            selectedMonth = selectedMonth,
                            onSelectApp = {
                                selectedAppId = it
                                if (it != null) {
                                    selectedDate = null
                                    selectedMonth = null
                                }
                            },
                            onSelectDate = {
                                selectedDate = it
                                if (it != null) {
                                    selectedAppId = null
                                    selectedMonth = null
                                }
                            },
                            onSelectMonth = {
                                selectedMonth = it
                                if (it != null) {
                                    selectedAppId = null
                                    selectedDate = null
                                }
                            },
                        )
                    }
                }
                is AnalyticsUiState.Content -> AnalyticsContent(
                    snapshot = current.snapshot,
                    selectedAppId = selectedAppId,
                    selectedDate = selectedDate,
                    selectedMonth = selectedMonth,
                    onSelectApp = {
                        selectedAppId = it
                        if (it != null) {
                            selectedDate = null
                            selectedMonth = null
                        }
                    },
                    onSelectDate = {
                        selectedDate = it
                        if (it != null) {
                            selectedAppId = null
                            selectedMonth = null
                        }
                    },
                    onSelectMonth = {
                        selectedMonth = it
                        if (it != null) {
                            selectedAppId = null
                            selectedDate = null
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun AnalyticsContent(
    snapshot: AnalyticsSnapshot,
    selectedAppId: String?,
    selectedDate: String?,
    selectedMonth: String?,
    onSelectApp: (String?) -> Unit,
    onSelectDate: (String?) -> Unit,
    onSelectMonth: (String?) -> Unit,
) {
    if (snapshot.period == AnalyticsPeriod.THIRTY_DAYS) {
        AppShareStory(
            shares = snapshot.appShares,
            selectedAppId = selectedAppId,
            onSelectApp = onSelectApp,
            isDemo = snapshot.isDemo,
        )
        Spacer(Modifier.height(30.dp))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = snapshot.total.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = if (snapshot.total == 0) {
                    "No reels counted during tracked time"
                } else {
                    "Reels scrolled"
                },
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (snapshot.isDemo) DemoBadge()
    }
    Spacer(Modifier.height(10.dp))
    CoverageLabel(snapshot.availableDays, snapshot.expectedDays)
    snapshot.comparison?.let {
        Spacer(Modifier.height(10.dp))
        Text(
            text = analyticsComparison(it.percentChange),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Spacer(Modifier.height(30.dp))

    when (snapshot.period) {
        AnalyticsPeriod.TODAY -> {
            AppShareStory(
                shares = snapshot.appShares,
                selectedAppId = selectedAppId,
                onSelectApp = onSelectApp,
                isDemo = snapshot.isDemo,
            )
            Spacer(Modifier.height(30.dp))
            DailyTrend(
                days = snapshot.days,
                selectedAppId = selectedAppId,
                selectedDate = selectedDate,
                onSelectDate = onSelectDate,
            )
        }
        AnalyticsPeriod.THIRTY_DAYS -> DailyTrend(
            days = snapshot.days,
            selectedAppId = selectedAppId,
            selectedDate = selectedDate,
            onSelectDate = onSelectDate,
        )
        AnalyticsPeriod.YEAR -> {
            MonthTotals(
                months = snapshot.months,
                selectedMonth = selectedMonth,
                onSelectMonth = onSelectMonth,
                isDemo = snapshot.isDemo,
            )
            Spacer(Modifier.height(30.dp))
            YearHeatmap(
                days = selectedMonth?.let { month ->
                    snapshot.days.filter { it.date.toString().startsWith(month) }
                } ?: snapshot.days,
                selectedDate = selectedDate,
                onSelectDate = onSelectDate,
            )
            Spacer(Modifier.height(30.dp))
            AppShareStory(
                shares = snapshot.appShares,
                selectedAppId = selectedAppId,
                onSelectApp = onSelectApp,
                isDemo = snapshot.isDemo,
            )
        }
    }
    Spacer(Modifier.height(18.dp))
    SelectionDetails(
        snapshot = snapshot,
        selectedDate = selectedDate,
        selectedMonth = selectedMonth,
    )
    Spacer(Modifier.height(28.dp))
    Text(
        text = snapshot.insight,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private fun analyticsComparison(percentChange: Int): String = when {
    percentChange > 0 -> "$percentChange% more than the previous equal period"
    percentChange < 0 -> "${-percentChange}% fewer than the previous equal period"
    else -> "About the same as the previous equal period"
}
