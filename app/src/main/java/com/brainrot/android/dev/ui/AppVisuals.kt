package com.brainrot.android.dev.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.brainrot.android.dev.TrackedApp

data class AppVisual(
    val label: String,
    val color: Color,
    val icon: ImageVector,
)

fun visualForAppId(appId: String): AppVisual = when (appId) {
    TrackedApp.YOUTUBE.appId ->
        AppVisual("YouTube", Color(0xFFFF5A5F), Icons.Default.PlayCircle)
    TrackedApp.INSTAGRAM.appId ->
        AppVisual("Instagram", Color(0xFFE56A9B), Icons.Default.PhotoCamera)
    TrackedApp.TIKTOK.appId ->
        AppVisual("TikTok", Color(0xFF55D9D0), Icons.Default.MusicNote)
    else ->
        AppVisual(
            appId.replaceFirstChar { it.uppercase() },
            Color(0xFFBCA1F5),
            Icons.Default.Smartphone,
        )
}
