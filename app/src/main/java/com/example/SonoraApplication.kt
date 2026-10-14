package com.example

import android.app.Application
import com.example.data.local.SonoraDatabase
import com.example.data.repository.MediaStoreRepository
import com.example.data.settings.SonoraSettingsRepository
import com.example.playback.SonoraPlaybackController

class SonoraApplication : Application() {

    companion object {
        lateinit var instance: SonoraApplication
            private set
    }

    val database by lazy {
        SonoraDatabase.getInstance(this)
    }

    val settingsRepository by lazy {
        SonoraSettingsRepository(this)
    }

    val mediaStoreRepository by lazy {
        MediaStoreRepository(this)
    }

    val playbackController by lazy {
        SonoraPlaybackController(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
