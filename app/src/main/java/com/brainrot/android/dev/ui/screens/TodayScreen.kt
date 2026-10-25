package com.brainrot.android.dev.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainrot.android.dev.ReelRepository
import com.brainrot.android.dev.TrackedApp
import com.brainrot.android.dev.ui.components.AppBreakdownCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(repository: ReelRepository, modifier: Modifier = Modifier) {
    val total by repository.observeTodayTotal().collectAsStateWithLifecycle(0)
    val byApp by repository.observeTodayByApp().collectAsStateWithLifecycle(emptyList())
    val foreground by repository.foregroundApp.collectAsStateWithLifecycle()

    val countsById = byApp.associate { it.appId to it.count }
    val maxApp = (byApp.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    val todayLabel = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(
            text = "Today",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = todayLabel,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(20.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = total.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Reels scrolled",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Now: ${foreground ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "By app",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TrackedApp.entries.forEach { app ->
                AppBreakdownCard(
                    appId = app.appId,
                    count = countsById[app.appId] ?: 0,
                    maxCount = maxApp,
                )
            }
        }
    }
}
