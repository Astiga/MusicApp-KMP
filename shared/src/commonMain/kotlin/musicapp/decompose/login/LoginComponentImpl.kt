package musicapp.decompose.login

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import musicapp.localpersistence.LocalPersistenceComponents
import musicapp.login.LoginViewModel
import musicapp.network.AstigaApi

/**
 * Implementation of the LoginComponent interface.
 */
class LoginComponentImpl(
    componentContext: ComponentContext,
    private val astigaApi: AstigaApi,
    private val localPersistenceComponents: LocalPersistenceComponents,
    val output: (LoginComponent.Output) -> Unit,
) : LoginComponent, ComponentContext by componentContext {

    override val viewModel: LoginViewModel
        get() = instanceKeeper.getOrCreate {
            LoginViewModel(astigaApi, localPersistenceComponents)
        }

    override fun onOutPut(output: LoginComponent.Output) {
        output(output)
    }
}