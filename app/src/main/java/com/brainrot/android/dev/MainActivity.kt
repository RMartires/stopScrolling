package com.brainrot.android.dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brainrot.android.dev.ui.screens.AnalyticsScreen
import com.brainrot.android.dev.ui.screens.SettingsScreen
import com.brainrot.android.dev.ui.screens.TodayScreen
import com.brainrot.android.dev.ui.theme.StopScrollingTheme

class MainActivity : ComponentActivity() {
    private val repository: ReelRepository
        get() = (application as DevApp).reelRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StopScrollingTheme {
                BrainPalApp(
                    repository = repository,
                    accessibilityEnabled = SettingsIntents.isAccessibilityEnabled(this),
                    overlayEnabled = SettingsIntents.canDrawOverlays(this),
                    onOpenAccessibility = { startActivity(SettingsIntents.accessibilitySettings()) },
                    onOpenOverlay = { startActivity(SettingsIntents.overlaySettings(this)) },
                    onShowOverlay = { OverlayManager.show(this) },
                    onClearData = { repository.clearAll(this) },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SettingsIntents.canDrawOverlays(this)) {
            OverlayManager.show(this)
            repository.refreshOverlay(this)
        }
    }
}

private enum class Tab(val label: String) {
    Today("Today"),
    Analytics("Analytics"),
    Settings("Setup"),
}

@Composable
private fun BrainPalApp(
    repository: ReelRepository,
    accessibilityEnabled: Boolean,
    overlayEnabled: Boolean,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onShowOverlay: () -> Unit,
    onClearData: () -> Unit,
) {
    var tab by rememberSaveable { mutableStateOf(Tab.Today) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == Tab.Today,
                    onClick = { tab = Tab.Today },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(Tab.Today.label) },
                )
                NavigationBarItem(
                    selected = tab == Tab.Analytics,
                    onClick = { tab = Tab.Analytics },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text(Tab.Analytics.label) },
                )
                NavigationBarItem(
                    selected = tab == Tab.Settings,
                    onClick = { tab = Tab.Settings },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(Tab.Settings.label) },
                )
            }
        },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (tab) {
            Tab.Today -> TodayScreen(repository = repository, modifier = modifier)
            Tab.Analytics -> AnalyticsScreen(repository = repository, modifier = modifier)
            Tab.Settings -> SettingsScreen(
                modifier = modifier,
                accessibilityEnabled = accessibilityEnabled,
                overlayEnabled = overlayEnabled,
                onOpenAccessibility = onOpenAccessibility,
                onOpenOverlay = onOpenOverlay,
                onShowOverlay = onShowOverlay,
                onClearData = onClearData,
            )
        }
    }
}
