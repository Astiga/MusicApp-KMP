package musicapp.decompose.login

import musicapp.login.LoginViewModel

/**
 * Component for handling login functionality.
 */
interface LoginComponent {
    /**
     * The ViewModel for this component.
     */
    val viewModel: LoginViewModel

    fun onOutPut(output: Output)

    sealed class Output {
        data object OnLoginSuccessful : Output()
    }
}