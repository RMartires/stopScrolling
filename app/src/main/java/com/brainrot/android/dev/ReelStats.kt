package com.brainrot.android.dev

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ReelStats {
    private const val TAG = "BrainPalDevStats"
    private const val PREFS = "reel_stats"
    private const val KEY_COUNT = "reels_today"

    private val mainHandler = Handler(Looper.getMainLooper())

    private val _reelsToday = MutableStateFlow(0)
    val reelsToday: StateFlow<Int> = _reelsToday.asStateFlow()

    private val _foregroundApp = MutableStateFlow<String?>(null)
    val foregroundApp: StateFlow<String?> = _foregroundApp.asStateFlow()

    private var lastCountAtMs = 0L
    private var lastSource: String? = null

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        _reelsToday.value = prefs.getInt(KEY_COUNT, 0)
    }

    fun recordYoutubeSwipe(context: Context, source: String) {
        val now = System.currentTimeMillis()
        val minGapMs = when (source) {
            "scroll" -> 700L
            "content" -> 1800L
            else -> 1200L
        }
        if (now - lastCountAtMs < minGapMs && source == lastSource) return
        lastCountAtMs = now
        lastSource = source

        val next = _reelsToday.value + 1
        _reelsToday.value = next
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_COUNT, next)
            .apply()
        Log.d(TAG, "reel count=$next source=$source")
        mainHandler.post { OverlayManager.refresh(context, next) }
    }

    fun setForegroundApp(context: Context, label: String?) {
        _foregroundApp.value = label
    }

    fun resetToday(context: Context) {
        _reelsToday.value = 0
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_COUNT, 0)
            .apply()
        mainHandler.post { OverlayManager.refresh(context, 0) }
    }
}
