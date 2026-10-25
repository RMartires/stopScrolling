package com.brainrot.android.dev

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

object OverlayManager {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var overlayView: TextView? = null
    private var windowManager: WindowManager? = null

    fun show(context: Context) {
        if (!SettingsIntents.canDrawOverlays(context)) return
        mainHandler.post {
            if (overlayView != null) {
                refresh(context)
                return@post
            }
            val appContext = context.applicationContext
            windowManager = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val view = TextView(appContext).apply {
                textSize = 14f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(24, 16, 24, 16)
                setBackgroundColor(0xCC5B21B6.toInt())
            }
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                x = 24
                y = 120
            }
            windowManager?.addView(view, params)
            overlayView = view
            refresh(context)
        }
    }

    fun hide() {
        mainHandler.post {
            overlayView?.let { windowManager?.removeView(it) }
            overlayView = null
        }
    }

    fun refresh(context: Context, count: Int? = null) {
        val value = count ?: context.getSharedPreferences("reel_stats", Context.MODE_PRIVATE)
            .getInt("reels_today", 0)
        mainHandler.post {
            overlayView?.text = "Reels: $value"
        }
    }
}
