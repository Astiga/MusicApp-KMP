package musicapp.decompose.dashboard

import musicapp.dashboard.DashboardViewModel


/**
 * Created by abdulbasit on 19/03/2023.
 */
interface DashboardMainComponent {
    val viewModel: DashboardViewModel

    fun onOutPut(output: Output)

    sealed class Output {
        data class PlaylistSelected(val playlistId: String) : Output()
    }
}
