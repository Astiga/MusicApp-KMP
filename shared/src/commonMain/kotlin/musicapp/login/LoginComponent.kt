package musicapp.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import musicapp.network.AstigaApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Component for handling login functionality.
 */
class LoginComponent : KoinComponent {
    private val astigaApi: AstigaApi by inject()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    /**
     * Attempts to login with the provided credentials.
     */
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Username and password cannot be empty")
            return
        }

        _loginState.value = LoginState.Loading

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pingSuccess = astigaApi.ping(
                    username = username,
                    password = password, // Password should already be in the format "enc:encrypted_password"
                    version = "1.2.0",
                    client = "Astiga"
                )

                if (pingSuccess) {
                    // Validate license
                    val licenseSuccess = astigaApi.getLicense(
                        username = username,
                        password = password,
                        version = "1.2.0",
                        client = "Astiga"
                    )

                    if (licenseSuccess) {
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error("License validation failed")
                    }
                } else {
                    _loginState.value = LoginState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("An error occurred: ${e.message}")
            }
        }
    }
}

/**
 * Represents the state of the login process.
 */
sealed class LoginState {
    data object Initial : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
