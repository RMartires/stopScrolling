package com.brainrot.android.dev.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReelDaoTest {
    private lateinit var database: ReelDatabase
    private lateinit var dao: ReelDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ReelDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.reelDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun replacementIsDateBoundedAndDemoCleanupPreservesRealRows() = runBlocking {
        val before = DailyAppReels("2026-09-01", "youtube", 4, DataOrigin.REAL.name)
        val replaced = DailyAppReels("2026-10-01", "youtube", 9, DataOrigin.REAL.name)
        dao.upsertAll(listOf(before, replaced))

        val demo = DailyAppReels("2026-10-01", "instagram", 7, DataOrigin.DEMO.name)
        dao.replaceRangeWithDemo(
            startDate = "2026-10-01",
            endDate = "2026-10-30",
            rows = listOf(demo),
            coverage = listOf(
                DailyTrackingCoverage("2026-10-01", DataOrigin.DEMO.name),
            ),
        )

        assertEquals(4, dao.get("2026-09-01", "youtube", DataOrigin.REAL.name)?.count)
        assertNull(dao.get("2026-10-01", "youtube", DataOrigin.REAL.name))
        assertEquals(7, dao.get("2026-10-01", "instagram", DataOrigin.DEMO.name)?.count)

        dao.clearDemoData()

        assertEquals(4, dao.totalBetween("2026-01-01", "2026-12-31"))
        assertEquals(
            emptyList<DailyTrackingCoverage>(),
            dao.observeCoverageBetween("2026-01-01", "2026-12-31").first(),
        )
    }
}
