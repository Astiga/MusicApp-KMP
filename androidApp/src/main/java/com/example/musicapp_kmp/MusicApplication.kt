package com.example.musicapp_kmp

import android.app.Application
import musicapp.di.androidModule
import musicapp.di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@MusicApplication)
            modules(commonModule, androidModule)
        }
    }
}