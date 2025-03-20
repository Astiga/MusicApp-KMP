package musicapp.di

import musicapp.decompose.ChartDetailsComponent
import musicapp.decompose.ChartDetailsComponentImpl
import musicapp.decompose.DashboardMainComponent
import musicapp.decompose.DashboardMainComponentImpl
import musicapp.decompose.MusicRootImpl
import musicapp.network.SpotifyApi
import musicapp.network.SpotifyApiImpl
import org.koin.dsl.module

val commonModule = module {
    single<SpotifyApi> { 
        SpotifyApiImpl()
    }

    factory { (componentContext: com.arkivanov.decompose.ComponentContext) ->
        MusicRootImpl(
            componentContext = componentContext,
            api = get(),
            mediaPlayerController = get()
        )
    }

    factory { (componentContext: com.arkivanov.decompose.ComponentContext, output: (DashboardMainComponent.Output) -> Unit) ->
        DashboardMainComponentImpl(
            componentContext = componentContext,
            spotifyApi = get(),
            output = output
        )
    }

    factory { (componentContext: com.arkivanov.decompose.ComponentContext, playlistId: String, playingTrackId: String, chartDetailsInput: kotlinx.coroutines.flow.SharedFlow<ChartDetailsComponent.Input>, output: (ChartDetailsComponent.Output) -> Unit) ->
        ChartDetailsComponentImpl(
            componentContext = componentContext,
            spotifyApi = get(),
            playlistId = playlistId,
            output = output,
            playingTrackId = playingTrackId,
            chatDetailsInput = chartDetailsInput
        )
    }
}
