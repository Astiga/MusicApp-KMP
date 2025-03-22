package musicapp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import musicapp.decompose.chartdetails.ChartDetailsComponent
import musicapp.decompose.chartdetails.ChartDetailsComponentImpl
import musicapp.decompose.dashboard.DashboardMainComponent
import musicapp.decompose.dashboard.DashboardMainComponentImpl
import musicapp.decompose.login.LoginComponent
import musicapp.decompose.login.LoginComponentImpl
import musicapp.decompose.player.PlayerComponent
import musicapp.decompose.player.PlayerComponentImpl
import musicapp.network.AstigaApi
import musicapp.network.SpotifyApi
import musicapp.network.models.topfiftycharts.Item
import musicapp.player.MediaPlayerController
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by abdulbasit on 19/03/2023.
 */
class MusicRootImpl(
    componentContext: ComponentContext,
) : MusicRoot, ComponentContext by componentContext, KoinComponent {
    private val spotifyApi: SpotifyApi by inject()
    private val mediaPlayerController: MediaPlayerController by inject()
    private val astigaApi: AstigaApi by inject()

    private val login: (ComponentContext, (LoginComponent.Output) -> Unit) -> LoginComponent = { childContext, output ->
        LoginComponentImpl(
            componentContext = childContext, astigaApi = astigaApi, output = output
        )
    }

    private val dashboardMain: (ComponentContext, (DashboardMainComponent.Output) -> Unit) -> DashboardMainComponent =
        { childContext, output ->
            DashboardMainComponentImpl(
                componentContext = childContext, spotifyApi = spotifyApi, output = output
            )
        }

    private val chartDetails: (ComponentContext, playlistId: String, playingTrackId: String, chatDetailsInput: SharedFlow<ChartDetailsComponent.Input>, (ChartDetailsComponent.Output) -> Unit) -> ChartDetailsComponent =
        { childContext, playlistId, playingTrackId, chartDetailsInput, output ->
            ChartDetailsComponentImpl(
                componentContext = childContext,
                spotifyApi = spotifyApi,
                playlistId = playlistId,
                output = output,
                playingTrackId = playingTrackId,
                chatDetailsInput = chartDetailsInput
            )
        }

    //to keep track of the playing track
    private var currentPlayingTrack = "-1"
    private val musicPlayerInput = MutableSharedFlow<PlayerComponent.Input>()
    private val chatDetailsInput = MutableSharedFlow<ChartDetailsComponent.Input>()

    constructor(
        componentContext: ComponentContext, api: SpotifyApi, mediaPlayerController: MediaPlayerController
    ) : this(componentContext = componentContext)

    private val navigation = StackNavigation<Configuration>()
    private val dialogNavigation = SlotNavigation<DialogConfig>()

    private val stack = childStack(
        source = navigation,
        serializer = serializer(),
        initialConfiguration = Configuration.Login,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        configuration: Configuration, componentContext: ComponentContext
    ): MusicRoot.Child = when (configuration) {
        Configuration.Login -> MusicRoot.Child.Login(
            login(componentContext, ::loginOutput)
        )

        is Configuration.Dashboard -> MusicRoot.Child.Dashboard(
            dashboardMain(componentContext, ::dashboardOutput)
        )

        is Configuration.Details -> MusicRoot.Child.Details(
            chartDetails(
                componentContext, configuration.playlistId, currentPlayingTrack, chatDetailsInput, ::detailsOutput
            )
        )
    }

    private fun loginOutput(output: LoginComponent.Output) {
        when (output) {
            LoginComponent.Output.OnLoginSuccessful -> navigation.push(
                Configuration.Dashboard
            )
        }
    }

    private fun dashboardOutput(output: DashboardMainComponent.Output) {
        when (output) {
            is DashboardMainComponent.Output.PlaylistSelected -> navigation.push(
                Configuration.Details(
                    output.playlistId, currentPlayingTrack
                )
            )
        }
    }

    private fun detailsOutput(output: ChartDetailsComponent.Output) {
        when (output) {
            is ChartDetailsComponent.Output.GoBack -> navigation.pop()
            is ChartDetailsComponent.Output.OnPlayAllSelected -> {
                dialogNavigation.activate(DialogConfig(output.playlist))
                CoroutineScope(Dispatchers.Default).launch {
                    musicPlayerInput.emit(PlayerComponent.Input.UpdateTracks(output.playlist))
                }
            }

            is ChartDetailsComponent.Output.OnTrackSelected -> {
                dialogNavigation.activate(DialogConfig(output.playlist, output.trackId))
                CoroutineScope(Dispatchers.Default).launch {
                    musicPlayerInput.emit(PlayerComponent.Input.PlayTrack(output.trackId, output.playlist))
                }
            }
        }
    }

    private val player = childSlot(
        source = dialogNavigation,
        serializer = serializer(),
        initialConfiguration = { null },
        key = "PlayerView",
        handleBackButton = true,
        childFactory = { config, _ ->
            PlayerComponentImpl(
                componentContext = componentContext,
                mediaPlayerController = mediaPlayerController,
                trackList = config.playlist,
                selectedTrack = config.selectedTrack,
                playerInputs = musicPlayerInput,
                output = {
                    when (it) {
                        PlayerComponent.Output.OnPause -> TODO()
                        PlayerComponent.Output.OnPlay -> TODO()

                        is PlayerComponent.Output.OnTrackUpdated -> {
                            CoroutineScope(Dispatchers.Default).launch {
                                currentPlayingTrack = it.trackId
                                chatDetailsInput.emit(ChartDetailsComponent.Input.TrackUpdated(it.trackId))
                            }
                        }
                    }
                })
        })

    override val childStack: Value<ChildStack<*, MusicRoot.Child>>
        get() = value()

    override val dialogOverlay: Value<ChildSlot<*, PlayerComponent>>
        get() = player

    private fun value() = stack

    @Serializable
    private sealed class Configuration {

        @Serializable
        data object Login : Configuration()

        @Serializable
        data object Dashboard : Configuration()

        @Serializable
        data class Details(
            val playlistId: String,
            val playingTrackId: String,
        ) : Configuration()
    }

    @Serializable
    private data class DialogConfig(
        val playlist: List<Item>, val selectedTrack: String = ""
    )
}
