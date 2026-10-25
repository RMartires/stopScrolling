package com.brainrot.android.dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainrot.android.dev.ui.theme.StopScrollingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReelStats.load(this)
        enableEdgeToEdge()
        setContent {
            StopScrollingTheme {
                DevHomeScreen(
                    onOpenAccessibility = { startActivity(SettingsIntents.accessibilitySettings()) },
                    onOpenOverlay = { startActivity(SettingsIntents.overlaySettings(this)) },
                    onShowOverlay = { OverlayManager.show(this) },
                    onReset = { ReelStats.resetToday(this) },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SettingsIntents.canDrawOverlays(this)) {
            OverlayManager.show(this)
        }
    }
}

@Composable
private fun DevHomeScreen(
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onShowOverlay: () -> Unit,
    onReset: () -> Unit,
) {
    val reelsToday by ReelStats.reelsToday.collectAsStateWithLifecycle()
    val foreground by ReelStats.foregroundApp.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "BrainPal Dev",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tracks YouTube Shorts swipes via Accessibility (dev heuristic)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = reelsToday.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Reels today",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Foreground: ${foreground ?: "unknown"}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onOpenAccessibility, modifier = Modifier.fillMaxWidth()) {
                Text("1. Enable Accessibility service")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onOpenOverlay, modifier = Modifier.fillMaxWidth()) {
                Text("2. Allow display over other apps")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onShowOverlay, modifier = Modifier.fillMaxWidth()) {
                Text("Show reel counter bubble")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
                Text("Reset count")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Then open YouTube Shorts and swipe. The purple bubble should increment.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
