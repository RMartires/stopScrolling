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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    accessibilityEnabled: Boolean,
    overlayEnabled: Boolean,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onShowOverlay: () -> Unit,
    onClearData: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(
            text = "Setup",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))
        StatusChip(
            title = "Accessibility",
            ok = accessibilityEnabled,
            detail = "Required to detect swipes in YouTube, Instagram, TikTok",
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onOpenAccessibility, modifier = Modifier.fillMaxWidth()) {
            Text("Open accessibility settings")
        }
        Spacer(Modifier.height(16.dp))
        StatusChip(
            title = "Display over apps",
            ok = overlayEnabled,
            detail = "Shows the live reel counter bubble",
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onOpenOverlay, modifier = Modifier.fillMaxWidth()) {
            Text("Allow overlay")
        }
        OutlinedButton(onClick = onShowOverlay, modifier = Modifier.fillMaxWidth()) {
            Text("Refresh bubble")
        }
        Spacer(Modifier.height(24.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Private by default",
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "No account, no cloud sync. Analytics live in a local Room database on this phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onClearData, modifier = Modifier.fillMaxWidth()) {
            Text("Clear all local analytics")
        }
    }
}

@Composable
private fun StatusChip(title: String, ok: Boolean, detail: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = "$title · ${if (ok) "Ready" else "Needs setup"}",
                fontWeight = FontWeight.SemiBold,
                color = if (ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
