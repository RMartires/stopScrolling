package com.brainrot.android.dev

import android.content.Context
import com.brainrot.android.dev.data.DayTotal
import com.brainrot.android.dev.data.ReelDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReelRepository(context: Context) {
    private val db = ReelDatabase.build(context.applicationContext)
    private val dao = db.reelDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val _foregroundApp = MutableStateFlow<String?>(null)
    val foregroundApp: StateFlow<String?> = _foregroundApp.asStateFlow()

    private var lastCountAtMs = 0L
    private var lastSource: String? = null

    fun today(): String = LocalDate.now().format(dateFormatter)

    fun observeTodayTotal(): Flow<Int> = dao.observeTotalForDate(today())

    fun observeTodayByApp() = dao.observeForDate(today())

    fun observeDailyTotals(days: Int = 14): Flow<List<DayTotal>> = dao.observeDailyTotals(days)

    fun recordReel(context: Context, app: TrackedApp, source: String) {
        val now = System.currentTimeMillis()
        val minGapMs = when (source) {
            "scroll" -> 700L
            "content" -> 1800L
            else -> 1200L
        }
        if (now - lastCountAtMs < minGapMs && source == lastSource) return
        lastCountAtMs = now
        lastSource = source

        scope.launch {
            dao.increment(today(), app.appId)
            val total = dao.totalForDate(today())
            OverlayManager.refresh(context, total)
        }
    }

    fun setForegroundApp(label: String?) {
        _foregroundApp.value = label
    }

    fun clearAll(context: Context) {
        scope.launch {
            dao.clearAll()
            OverlayManager.refresh(context, 0)
        }
    }

    fun refreshOverlay(context: Context) {
        scope.launch {
            OverlayManager.refresh(context, dao.totalForDate(today()))
        }
    }
}
