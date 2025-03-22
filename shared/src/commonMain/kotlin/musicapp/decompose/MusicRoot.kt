package musicapp.decompose

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import musicapp.decompose.login.LoginComponent

/**
 * Created by abdulbasit on 19/03/2023.
 */
interface MusicRoot {

    val childStack: Value<ChildStack<*, Child>>
    val dialogOverlay: Value<ChildSlot<*, PlayerComponent>>

    sealed class Child {
        data class Login(val loginComponent: LoginComponent) : Child()
        data class Dashboard(val dashboardMainComponent: DashboardMainComponent) : Child()
        data class Details(val detailsComponent: ChartDetailsComponent) : Child()
    }
}
