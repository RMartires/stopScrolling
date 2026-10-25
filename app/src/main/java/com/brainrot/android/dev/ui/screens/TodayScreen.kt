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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainrot.android.dev.ReelRepository
import com.brainrot.android.dev.analytics.AnalyticsPeriod
import com.brainrot.android.dev.analytics.AnalyticsUiState
import com.brainrot.android.dev.ui.components.AnalyticsLoading
import com.brainrot.android.dev.ui.components.AnalyticsMessage
import com.brainrot.android.dev.ui.components.AppShareStory
import com.brainrot.android.dev.ui.components.CoverageLabel
import com.brainrot.android.dev.ui.components.DemoBadge
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(
    repository: ReelRepository,
    onOpenSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by repository.observeUiState(AnalyticsPeriod.TODAY)
        .collectAsStateWithLifecycle(initialValue = AnalyticsUiState.Loading)
    val foreground by repository.foregroundApp.collectAsStateWithLifecycle()

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
            Text(
                text = "Today",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(24.dp))
            when (val current = state) {
                AnalyticsUiState.Loading -> AnalyticsLoading()
                is AnalyticsUiState.PermissionBlocked -> AnalyticsMessage(
                    title = "Finish setup to start counting",
                    body = "BrainPal needs accessibility access to detect swipes. " +
                        "Overlay access keeps the live counter visible.",
                    actionLabel = "Open setup",
                    onAction = onOpenSetup,
                )
                is AnalyticsUiState.Empty -> AnalyticsMessage(
                    title = "No tracked swipes yet",
                    body = "Open YouTube, Instagram, or TikTok and swipe through a few reels.",
                )
                is AnalyticsUiState.Error -> AnalyticsMessage(
                    title = "Analytics unavailable",
                    body = current.message,
                )
                is AnalyticsUiState.Content -> {
                    val snapshot = current.snapshot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = snapshot.total.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f),
                        )
                        if (snapshot.isDemo) DemoBadge()
                    }
                    Text(
                        text = if (snapshot.total == 0) {
                            "No reels counted during tracked time"
                        } else {
                            "Reels scrolled"
                        },
                        style = MaterialTheme.typography.titleLarge,
                    )
                    snapshot.comparison?.let {
                        Text(
                            text = comparisonText(it.percentChange, "yesterday"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    CoverageLabel(snapshot.availableDays, snapshot.expectedDays)
                    Spacer(Modifier.height(28.dp))
                    AppShareStory(
                        shares = snapshot.appShares,
                        selectedAppId = null,
                        onSelectApp = {},
                        isDemo = snapshot.isDemo,
                    )
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = snapshot.insight,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Now tracking: ${foreground ?: "no supported app open"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun comparisonText(percentChange: Int, period: String): String = when {
    percentChange > 0 -> "$percentChange% more than $period"
    percentChange < 0 -> "${-percentChange}% fewer than $period"
    else -> "About the same as $period"
}
