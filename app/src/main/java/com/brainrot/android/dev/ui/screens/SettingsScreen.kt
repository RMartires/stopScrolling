package com.brainrot.android.dev.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainrot.android.dev.ReelRepository
import com.brainrot.android.dev.analytics.DemoOperation
import com.brainrot.android.dev.ui.theme.Success

@Composable
fun SettingsScreen(
    repository: ReelRepository,
    modifier: Modifier = Modifier,
    accessibilityEnabled: Boolean,
    overlayEnabled: Boolean,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onShowOverlay: () -> Unit,
    onClearData: () -> Unit,
    onGenerateDemo: () -> Unit,
    onClearDemo: () -> Unit,
) {
    var showDemoConfirmation by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    val demoOperation by repository.demoOperation.collectAsStateWithLifecycle()
    val operationRunning = demoOperation == DemoOperation.Running

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
                text = "Setup",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Permissions, privacy, and test data",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(28.dp))
            StatusLine(
                title = "Accessibility",
                ok = accessibilityEnabled,
                detail = "Detects swipes in YouTube, Instagram, and TikTok.",
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = onOpenAccessibility, modifier = Modifier.fillMaxWidth()) {
                Text(if (accessibilityEnabled) "Review accessibility" else "Enable accessibility")
            }
            Spacer(Modifier.height(24.dp))
            StatusLine(
                title = "Display over apps",
                ok = overlayEnabled,
                detail = "Keeps the live counter visible over supported apps.",
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = onOpenOverlay, modifier = Modifier.fillMaxWidth()) {
                Text(if (overlayEnabled) "Review overlay access" else "Allow overlay")
            }
            OutlinedButton(onClick = onShowOverlay, modifier = Modifier.fillMaxWidth()) {
                Text("Refresh bubble")
            }
            Spacer(Modifier.height(34.dp))
            Text("Your data", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No account and no cloud sync. Analytics stay in a local database " +
                    "on this phone.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
            Text("Test analytics", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Generate a deterministic 30-day history to test daily and yearly views.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { showDemoConfirmation = true },
                enabled = !operationRunning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (operationRunning) "Working…" else "Generate 30-day test data")
            }
            OutlinedButton(
                onClick = onClearDemo,
                enabled = !operationRunning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Clear demo data")
            }
            when (val operation = demoOperation) {
                DemoOperation.Idle -> Unit
                DemoOperation.Running -> {
                    Spacer(Modifier.height(8.dp))
                    Text("Updating local analytics…")
                }
                is DemoOperation.Success -> OperationMessage(
                    message = operation.message,
                    isError = false,
                    onDismiss = repository::dismissDemoMessage,
                )
                is DemoOperation.Error -> OperationMessage(
                    message = operation.message,
                    isError = true,
                    onDismiss = repository::dismissDemoMessage,
                )
            }
            Spacer(Modifier.height(30.dp))
            TextButton(
                onClick = { showClearConfirmation = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Clear all local analytics", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDemoConfirmation) {
        AlertDialog(
            onDismissRequest = { showDemoConfirmation = false },
            title = { Text("Replace the last 30 days?") },
            text = {
                Text(
                    "Generating test data deletes all real and demo analytics from the last " +
                        "30 days. This cannot be restored.",
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDemoConfirmation = false
                        onGenerateDemo()
                    },
                ) {
                    Text("Replace data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDemoConfirmation = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear all analytics?") },
            text = { Text("All locally stored reel counts will be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmation = false
                        onClearData()
                    },
                ) {
                    Text("Clear all")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun StatusLine(title: String, ok: Boolean, detail: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (ok) "READY" else "NEEDS SETUP",
                style = MaterialTheme.typography.labelMedium,
                color = if (ok) Success else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = detail,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun OperationMessage(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
) {
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) MaterialTheme.colorScheme.error else Success,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onDismiss) {
            Text("Dismiss")
        }
    }
}
