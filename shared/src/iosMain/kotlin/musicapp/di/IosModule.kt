package musicapp.di

import musicapp.player.MediaPlayerController
import musicapp.player.PlatformContext
import org.koin.dsl.module

val iosModule = module {
    // Platform Context
    single { 
        PlatformContext()
    }

    // Media Player Controller
    single { 
        MediaPlayerController(get())
    }
}