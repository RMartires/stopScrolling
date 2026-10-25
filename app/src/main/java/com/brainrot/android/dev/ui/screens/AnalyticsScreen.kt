package com.brainrot.android.dev.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainrot.android.dev.ReelRepository
import com.brainrot.android.dev.TrackedApp
import com.brainrot.android.dev.ui.components.AppBreakdownCard
import com.brainrot.android.dev.ui.components.DayBreakdownRow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsScreen(repository: ReelRepository, modifier: Modifier = Modifier) {
    val todayByApp by repository.observeTodayByApp().collectAsStateWithLifecycle(emptyList())
    val dailyTotals by repository.observeDailyTotals(14).collectAsStateWithLifecycle(emptyList())

    val countsById = todayByApp.associate { it.appId to it.count }
    val maxApp = (todayByApp.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    val maxDay = (dailyTotals.maxOfOrNull { it.total } ?: 0).coerceAtLeast(1)
    val dateLabel = DateTimeFormatter.ofPattern("EEE, MMM d")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Stored on this device only",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Per app (today)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TrackedApp.entries.forEach { app ->
                AppBreakdownCard(
                    appId = app.appId,
                    count = countsById[app.appId] ?: 0,
                    maxCount = maxApp,
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Per day",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        if (dailyTotals.isEmpty()) {
            Text(
                text = "Swipe in a tracked app to start building history.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dailyTotals.forEach { day ->
                    val parsed = runCatching { LocalDate.parse(day.statsDate) }.getOrNull()
                    val label = parsed?.format(dateLabel) ?: day.statsDate
                    DayBreakdownRow(
                        label = label,
                        total = day.total,
                        maxTotal = maxDay,
                    )
                }
            }
        }
    }
}
