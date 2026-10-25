package com.brainrot.android.dev

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class DevAccessibilityService : AccessibilityService() {

    private val repository: ReelRepository
        get() = (application as DevApp).reelRepository

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return
        val app = TrackedApp.fromPackageOrAlt(pkg) ?: return

        repository.setForegroundApp(app.label)

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                val delta = kotlin.math.abs(event.scrollDeltaY) + kotlin.math.abs(event.scrollDeltaX)
                if (delta > 0) {
                    repository.recordReel(this, app, "scroll")
                }
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                repository.recordReel(this, app, "content")
            }
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                repository.recordReel(this, app, "window")
            }
        }
    }

    override fun onInterrupt() = Unit

    companion object {
        private const val TAG = "BrainPalDevA11y"
        const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        const val YOUTUBE_PACKAGE_ALT = "com.google.android.apps.youtube"
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val TIKTOK_PACKAGE = "com.zhiliaoapp.musically"
    }
}
