package com.brainrot.android.dev

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class DevAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return

        if (!isTrackedApp(pkg)) return

        when (pkg) {
            YOUTUBE_PACKAGE, YOUTUBE_PACKAGE_ALT -> ReelStats.setForegroundApp(this, "YouTube")
            INSTAGRAM_PACKAGE -> ReelStats.setForegroundApp(this, "Instagram")
            TIKTOK_PACKAGE -> ReelStats.setForegroundApp(this, "TikTok")
        }

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                val delta = kotlin.math.abs(event.scrollDeltaY) + kotlin.math.abs(event.scrollDeltaX)
                if (delta > 0) {
                    Log.d(TAG, "scroll delta=$delta pkg=$pkg")
                    ReelStats.recordYoutubeSwipe(this, "scroll")
                }
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                // Shorts often skips TYPE_VIEW_SCROLLED; content changes when the active clip changes.
                Log.d(TAG, "content changed pkg=$pkg class=${event.className}")
                ReelStats.recordYoutubeSwipe(this, "content")
            }
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                Log.d(TAG, "windows changed pkg=$pkg")
                ReelStats.recordYoutubeSwipe(this, "window")
            }
        }
    }

    override fun onInterrupt() = Unit

    private fun isTrackedApp(pkg: String): Boolean =
        pkg == YOUTUBE_PACKAGE ||
            pkg == YOUTUBE_PACKAGE_ALT ||
            pkg == INSTAGRAM_PACKAGE ||
            pkg == TIKTOK_PACKAGE

    companion object {
        private const val TAG = "BrainPalDevA11y"
        const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        const val YOUTUBE_PACKAGE_ALT = "com.google.android.apps.youtube"
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val TIKTOK_PACKAGE = "com.zhiliaoapp.musically"
    }
}
