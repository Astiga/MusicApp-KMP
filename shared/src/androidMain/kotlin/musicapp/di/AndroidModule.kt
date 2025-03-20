package musicapp.di

import musicapp.player.MediaPlayerController
import musicapp.player.PlatformContext
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    // Platform Context
    single { 
        PlatformContext(androidContext().applicationContext)
    }

    // Media Player Controller
    single { 
        MediaPlayerController(get())
    }
}