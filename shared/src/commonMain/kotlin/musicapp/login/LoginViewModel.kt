package musicapp.login

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import musicapp.network.AstigaApi

/**
 * ViewModel for handling login functionality.
 */
class LoginViewModel(
    private val astigaApi: AstigaApi
) : InstanceKeeper.Instance {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    private val job = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + coroutineExceptionHandler + job)

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

        viewModelScope.launch {
            try {
                val pingResponse = astigaApi.ping(
                    username = username,
                    password = password
                )

                if (pingResponse.isSuccess()) {
                    // Validate license
                    val licenseResponse = astigaApi.getLicense()

                    if (licenseResponse.isSuccess()) {
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

    /**
     * Resets the login state to initial.
     */
    fun resetState() {
        _loginState.value = LoginState.Initial
    }

    override fun onDestroy() {
        viewModelScope.cancel()
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
