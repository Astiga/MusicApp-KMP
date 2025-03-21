package musicapp.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import musicapp.decompose.login.LoginComponent
import musicapp.theme.gradientBrush
import musicapp_kmp.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(loginComponent: musicapp.decompose.LoginComponent) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current

    // Get the login state from the ViewModel
    val loginState by loginComponent.viewModel.loginState.collectAsState()

    // Handle login state changes
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Error -> {
                showError = true
                errorMessage = (loginState as LoginState.Error).message
            }

            is LoginState.Success -> {
                // In a real app, we would navigate to the next screen here
                // For now, just show a success message
                showError = true
                errorMessage = "Login successful!"
                loginComponent.onOutPut(LoginComponent.Output.OnLoginSuccessful)
            }

            else -> {
                showError = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loginState is LoginState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
        }

        if (showError) {
            Snackbar(
                modifier = Modifier.padding(8.dp),
                action = {
                    TextButton(onClick = { showError = false }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_logo_text),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(30.dp),
                contentScale = ContentScale.Fit
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(Res.string.prompt_email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(Res.string.prompt_password)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = { loginComponent.viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = loginState !is LoginState.Loading
            ) {
                Text(text = stringResource(Res.string.action_sign_in))
            }

            TextButton(
                onClick = {
                    uriHandler.openUri("https://play.asti.ga/register")
                },
                modifier = Modifier.padding(top = 5.dp)
            ) {
                Text(
                    text = stringResource(Res.string.settings_accounts_register),
                    fontSize = 14.sp
                )
            }
        }
    }
}
