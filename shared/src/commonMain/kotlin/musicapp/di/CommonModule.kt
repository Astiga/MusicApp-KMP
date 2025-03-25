package musicapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import musicapp.decompose.MusicRootImpl
import musicapp.decompose.chartdetails.ChartDetailsComponent
import musicapp.decompose.chartdetails.ChartDetailsComponentImpl
import musicapp.decompose.dashboard.DashboardMainComponent
import musicapp.decompose.dashboard.DashboardMainComponentImpl
import musicapp.localpersistence.LocalPersistenceComponents
import musicapp.localpersistence.LocalPersistenceImp
import musicapp.localpersistence.astigaDataStore
import musicapp.network.AstigaApi
import musicapp.network.AstigaApiImpl
import musicapp.network.SpotifyApi
import musicapp.network.SpotifyApiImpl
import org.koin.dsl.module

val commonModule = module {
    single<SpotifyApi> {
        SpotifyApiImpl()
    }
    single<DataStore<Preferences>> { astigaDataStore() }
    single<LocalPersistenceComponents> { LocalPersistenceImp(get()) }

    single<AstigaApi> {
        AstigaApiImpl()
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
