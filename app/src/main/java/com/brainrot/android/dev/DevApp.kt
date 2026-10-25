package com.brainrot.android.dev

import android.app.Application

class DevApp : Application() {
    lateinit var reelRepository: ReelRepository
        private set

    override fun onCreate() {
        super.onCreate()
        reelRepository = ReelRepository(this)
    }
}
