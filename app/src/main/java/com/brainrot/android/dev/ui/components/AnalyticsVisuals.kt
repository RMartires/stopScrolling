package com.brainrot.android.dev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brainrot.android.dev.TrackedApp
import com.brainrot.android.dev.analytics.AnalyticsPeriod
import com.brainrot.android.dev.analytics.AnalyticsSnapshot
import com.brainrot.android.dev.analytics.AppShare
import com.brainrot.android.dev.analytics.CalendarDay
import com.brainrot.android.dev.analytics.DayAvailability
import com.brainrot.android.dev.analytics.MonthSummary
import com.brainrot.android.dev.ui.theme.Warning
import com.brainrot.android.dev.ui.visualForAppId
import java.time.format.DateTimeFormatter

@Composable
fun PeriodSelector(
    selectedPeriod: AnalyticsPeriod,
    onSelected: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(4.dp),
    ) {
        AnalyticsPeriod.entries.forEach { period ->
            val isSelected = period == selectedPeriod
            TextButton(
                onClick = { onSelected(period) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = RoundedCornerShape(10.dp),
                    )
                    .semantics {
                        selected = isSelected
                        role = Role.Tab
                        contentDescription = "${period.label()} analytics"
                    },
            ) {
                Text(
                    text = period.label(),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun AppShareStory(
    shares: List<AppShare>,
    selectedAppId: String?,
    onSelectApp: (String?) -> Unit,
    isDemo: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val shareByApp = shares.associateBy { it.app }
    Column(modifier = modifier.fillMaxWidth()) {
        if (shares.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .semantics {
                        contentDescription = shares.joinToString(
                            prefix = "App share. ",
                            separator = ". ",
                        ) {
                            "${it.app.label}, ${it.count} reels, ${it.percent} percent" +
                                if (isDemo) ", demo data" else ""
                        }
                    },
            ) {
                shares.forEach { share ->
                    val visual = visualForAppId(share.app.appId)
                    Box(
                        modifier = Modifier
                            .weight(share.percent.coerceAtLeast(1).toFloat())
                            .fillMaxHeight()
                            .background(visual.color)
                            .clickable { onSelectApp(share.app.appId) },
                    )
                }
            }
        } else {
            Text(
                text = "No app share for this tracked period",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(14.dp))
        TrackedApp.entries.forEach { app ->
            val share = shareByApp[app]
            val count = share?.count ?: 0
            val percent = share?.percent ?: 0
            val visual = visualForAppId(app.appId)
            val isSelected = app.appId == selectedAppId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onSelectApp(if (isSelected) null else app.appId)
                    }
                    .then(
                        if (isSelected) {
                            Modifier.border(2.dp, visual.color, RoundedCornerShape(10.dp))
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = 10.dp)
                    .semantics {
                        selected = isSelected
                        role = Role.Button
                        contentDescription =
                            "${visual.label}, $count reels, $percent percent" +
                            if (isDemo) ", demo data" else ""
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(visual.color, RoundedCornerShape(3.dp)),
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.color,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = visual.label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "$percent%  ·  $count",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun DailyTrend(
    days: List<CalendarDay>,
    selectedAppId: String?,
    selectedDate: String?,
    onSelectDate: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleDays = days.filter { it.availability != DayAvailability.FUTURE }
    val totals = visibleDays.map { day ->
        selectedAppId?.let { day.appCounts[it] ?: 0 } ?: day.total
    }
    val maxTotal = totals.maxOrNull()?.coerceAtLeast(1) ?: 1
    val dateFormat = DateTimeFormatter.ofPattern("MMM d")

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (selectedAppId == null) "Daily trend" else {
                "${visualForAppId(selectedAppId).label} trend"
            },
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            visibleDays.forEachIndexed { index, day ->
                val value = totals[index]
                val dateKey = day.date.toString()
                val isSelected = selectedDate == dateKey
                val visualColor = when (day.availability) {
                    DayAvailability.TRACKED,
                    DayAvailability.TRACKED_ZERO,
                    -> MaterialTheme.colorScheme.primary
                    DayAvailability.UNAVAILABLE -> MaterialTheme.colorScheme.outline
                    DayAvailability.FUTURE -> Color.Transparent
                }
                Column(
                    modifier = Modifier
                        .width(48.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = day.availability != DayAvailability.UNAVAILABLE) {
                            onSelectDate(if (isSelected) null else dateKey)
                        }
                        .semantics {
                            selected = isSelected
                            role = Role.Button
                            contentDescription = when (day.availability) {
                                DayAvailability.UNAVAILABLE ->
                                    "${day.date.format(dateFormat)}, unavailable"
                                DayAvailability.TRACKED_ZERO ->
                                    "${day.date.format(dateFormat)}, zero tracked reels" +
                                        if (day.isDemo) ", demo data" else ""
                                else ->
                                    "${day.date.format(dateFormat)}, $value tracked reels" +
                                        if (day.isDemo) ", demo data" else ""
                            }
                        }
                        .padding(horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (isSelected) 24.dp else 18.dp)
                            .fillMaxHeight(
                                fraction = if (day.availability == DayAvailability.UNAVAILABLE) {
                                    0.08f
                                } else {
                                    (value.toFloat() / maxTotal).coerceIn(0.04f, 1f)
                                },
                            )
                            .background(
                                color = visualColor.copy(
                                    alpha = if (day.availability == DayAvailability.UNAVAILABLE) {
                                        0.35f
                                    } else {
                                        1f
                                    },
                                ),
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                            ),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun YearHeatmap(
    days: List<CalendarDay>,
    selectedDate: String?,
    onSelectDate: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val weeks = days.chunked(7)
    val max = days.maxOfOrNull { it.total }?.coerceAtLeast(1) ?: 1
    val dateFormat = DateTimeFormatter.ofPattern("MMM d")

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Scrolling intensity", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            weeks.forEach { week ->
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    week.forEach { day ->
                        val dateKey = day.date.toString()
                        val isSelected = selectedDate == dateKey
                        val intensity = (day.total.toFloat() / max).coerceIn(0f, 1f)
                        val color = when (day.availability) {
                            DayAvailability.TRACKED,
                            DayAvailability.TRACKED_ZERO,
                            -> MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.22f + intensity * 0.78f,
                            )
                            DayAvailability.UNAVAILABLE ->
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
                            DayAvailability.FUTURE -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(color)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.secondary,
                                            RoundedCornerShape(7.dp),
                                        )
                                    } else {
                                        Modifier
                                    },
                                )
                                .clickable(
                                    enabled = day.availability != DayAvailability.FUTURE,
                                ) {
                                    onSelectDate(if (isSelected) null else dateKey)
                                }
                                .semantics {
                                    selected = isSelected
                                    role = Role.Button
                                    contentDescription = when (day.availability) {
                                        DayAvailability.UNAVAILABLE ->
                                            "${day.date.format(dateFormat)}, unavailable"
                                        DayAvailability.FUTURE ->
                                            "${day.date.format(dateFormat)}, future date"
                                        DayAvailability.TRACKED_ZERO ->
                                            "${day.date.format(dateFormat)}, zero tracked reels" +
                                                if (day.isDemo) ", demo data" else ""
                                        DayAvailability.TRACKED ->
                                            "${day.date.format(dateFormat)}, ${day.total} tracked reels" +
                                                if (day.isDemo) ", demo data" else ""
                                    }
                                },
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Faint cells are unavailable history, not zero usage.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun MonthTotals(
    months: List<MonthSummary>,
    selectedMonth: String?,
    onSelectMonth: (String?) -> Unit,
    isDemo: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val max = months.maxOfOrNull { it.total }?.coerceAtLeast(1) ?: 1
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Monthly totals", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            months.forEach { month ->
                val isSelected = selectedMonth == month.monthKey
                Column(
                    modifier = Modifier
                        .width(52.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onSelectMonth(if (isSelected) null else month.monthKey)
                        }
                        .semantics {
                            selected = isSelected
                            role = Role.Button
                            contentDescription =
                                "${month.monthKey}, ${month.total} reels, " +
                                "${month.availableDays} recorded days" +
                                if (isDemo) ", includes demo data" else ""
                        }
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (isSelected) 28.dp else 20.dp)
                            .fillMaxHeight((month.total.toFloat() / max).coerceIn(0.04f, 1f))
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                            ),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = month.monthKey.takeLast(2),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionDetails(
    snapshot: AnalyticsSnapshot,
    selectedDate: String?,
    selectedMonth: String?,
    modifier: Modifier = Modifier,
) {
    val day = selectedDate?.let { key ->
        snapshot.days.firstOrNull { it.date.toString() == key }
    }
    val month = selectedMonth?.let { key ->
        snapshot.months.firstOrNull { it.monthKey == key }
    }
    if (day == null && month == null) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .semantics {
                contentDescription = when {
                    day != null ->
                        "${day.date}, ${day.total} reels, " +
                            "${if (day.isDemo) "demo data" else "personal data"}"
                    month != null ->
                        "${month.monthKey}, ${month.total} reels, " +
                            "${month.availableDays} recorded days"
                    else -> ""
                }
            },
    ) {
        Text(
            text = day?.date?.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
                ?: month?.monthKey.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = when {
                day != null -> "${day.total} reels · " +
                    when (day.availability) {
                        DayAvailability.UNAVAILABLE -> "Unavailable"
                        DayAvailability.FUTURE -> "Future"
                        DayAvailability.TRACKED_ZERO -> "Tracked, zero reels"
                        DayAvailability.TRACKED ->
                            if (day.isDemo) "Demo data" else "Personal data"
                    }
                month != null -> "${month.total} reels · ${month.availableDays} recorded days"
                else -> ""
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        day?.appCounts
            ?.filterValues { it > 0 }
            ?.toList()
            ?.sortedByDescending { it.second }
            ?.forEach { (appId, count) ->
                Text(
                    text = "${visualForAppId(appId).label}: $count",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
    }
}

@Composable
fun DemoBadge(modifier: Modifier = Modifier) {
    Text(
        text = "DEMO",
        modifier = modifier
            .border(1.dp, Warning, RoundedCornerShape(999.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
            .semantics { contentDescription = "Showing demo data" },
        color = Warning,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
    )
}

private fun AnalyticsPeriod.label(): String = when (this) {
    AnalyticsPeriod.TODAY -> "Today"
    AnalyticsPeriod.THIRTY_DAYS -> "30 days"
    AnalyticsPeriod.YEAR -> "Year"
}
