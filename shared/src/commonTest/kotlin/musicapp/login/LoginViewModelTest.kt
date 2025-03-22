package musicapp.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import musicapp.network.AstigaApi
import musicapp.network.models.astiga.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private fun createSuccessPingResponse(): Result<SubsonicResponse<PingResponseData?>> {
        val pingResponseData: PingResponseData? = PingResponseData(version = "1.0.0")
        val subsonicResponseData = SubsonicResponseData<PingResponseData?>(
            status = "ok",
            version = "1.0.0",
            data = pingResponseData
        )
        val subsonicResponse = SubsonicResponse(subsonicResponse = subsonicResponseData)
        return Result.success(subsonicResponse)
    }

    private fun createFailedPingResponse(): Result<SubsonicResponse<PingResponseData?>> {
        val subsonicResponseData = SubsonicResponseData<PingResponseData?>(
            status = "failed",
            version = "1.0.0",
            error = "Invalid credentials",
            message = "Authentication failed",
            data = null
        )
        val subsonicResponse = SubsonicResponse(subsonicResponse = subsonicResponseData)
        return Result.success(subsonicResponse)
    }

    private fun createSuccessLicenseResponse(): Result<SubsonicResponse<License?>> {
        val licenseData: License? = License(
            valid = true,
            email = "test@example.com",
            licenseExpires = "2025-01-01"
        )
        val subsonicResponseData = SubsonicResponseData<License?>(
            status = "ok",
            version = "1.0.0",
            data = licenseData
        )
        val subsonicResponse = SubsonicResponse(subsonicResponse = subsonicResponseData)
        return Result.success(subsonicResponse)
    }

    private fun createFailedLicenseResponse(): Result<SubsonicResponse<License?>> {
        val subsonicResponseData = SubsonicResponseData<License?>(
            status = "failed",
            version = "1.0.0",
            error = "License validation failed",
            message = "License validation failed",
            data = null
        )
        val subsonicResponse = SubsonicResponse(subsonicResponse = subsonicResponseData)
        return Result.success(subsonicResponse)
    }

    @Test
    fun testLoginWithEmptyCredentials() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        try {
            val mockApi = object : AstigaApi {
                override suspend fun ping(
                    username: String,
                    password: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<PingResponseData?>> {
                    throw IllegalStateException("This should not be called")
                }

                override suspend fun getLicense(
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<License?>> {
                    throw IllegalStateException("This should not be called")
                }

                override suspend fun getUser(
                    targetUsername: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<User?>> {
                    throw IllegalStateException("This should not be called")
                }
            }

            val viewModel = LoginViewModel(mockApi)

            assertTrue(viewModel.loginState.value is LoginState.Initial)

            viewModel.login("", "")

            assertTrue(viewModel.loginState.value is LoginState.Error)
            val errorState = viewModel.loginState.value as LoginState.Error
            assertEquals("Username and password cannot be empty", errorState.message)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun testSuccessfulLogin() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        try {
            val mockApi = object : AstigaApi {
                override suspend fun ping(
                    username: String,
                    password: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<PingResponseData?>> {
                    return createSuccessPingResponse()
                }

                override suspend fun getLicense(
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<License?>> {
                    return createSuccessLicenseResponse()
                }

                override suspend fun getUser(
                    targetUsername: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<User?>> {
                    throw IllegalStateException("This should not be called")
                }
            }

            val viewModel = LoginViewModel(mockApi)

            assertTrue(viewModel.loginState.value is LoginState.Initial)

            viewModel.login("testuser", "testpassword")

            assertTrue(viewModel.loginState.value is LoginState.Loading)

            advanceUntilIdle()

            assertTrue(viewModel.loginState.value is LoginState.Success)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun testFailedPing() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        try {
            val mockApi = object : AstigaApi {
                override suspend fun ping(
                    username: String,
                    password: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<PingResponseData?>> {
                    return createFailedPingResponse()
                }

                override suspend fun getLicense(
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<License?>> {
                    throw IllegalStateException("This should not be called")
                }

                override suspend fun getUser(
                    targetUsername: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<User?>> {
                    throw IllegalStateException("This should not be called")
                }
            }

            val viewModel = LoginViewModel(mockApi)

            assertTrue(viewModel.loginState.value is LoginState.Initial)

            viewModel.login("testuser", "testpassword")

            assertTrue(viewModel.loginState.value is LoginState.Loading)

            advanceUntilIdle()

            assertTrue(viewModel.loginState.value is LoginState.Error)
            val errorState = viewModel.loginState.value as LoginState.Error
            assertTrue(errorState.message.contains("Invalid username or password"))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun testFailedLicenseValidation() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        try {
            val mockApi = object : AstigaApi {
                override suspend fun ping(
                    username: String,
                    password: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<PingResponseData?>> {
                    val pingResponseData: PingResponseData? = PingResponseData(version = "1.0.0")
                    val subsonicResponseData = SubsonicResponseData<PingResponseData?>(
                        status = "ok",
                        version = "1.0.0",
                        data = pingResponseData
                    )
                    val subsonicResponse = SubsonicResponse(subsonicResponse = subsonicResponseData)
                    return Result.success(subsonicResponse)
                }

                override suspend fun getLicense(
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<License?>> {
                    return Result.failure(Exception("License validation failed"))
                }

                override suspend fun getUser(
                    targetUsername: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<User?>> {
                    throw IllegalStateException("This should not be called")
                }
            }

            val viewModel = LoginViewModel(mockApi)

            assertTrue(viewModel.loginState.value is LoginState.Initial)

            viewModel.login("testuser", "testpassword")

            assertTrue(viewModel.loginState.value is LoginState.Loading)

            advanceUntilIdle()

            assertTrue(viewModel.loginState.value is LoginState.Error)
            val errorState = viewModel.loginState.value as LoginState.Error
            assertTrue(errorState.message.contains("License validation failed"))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun testExceptionDuringApiCall() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        try {
            val mockApi = object : AstigaApi {
                override suspend fun ping(
                    username: String,
                    password: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<PingResponseData?>> {
                    throw Exception("Network error")
                }

                override suspend fun getLicense(
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<License?>> {
                    throw IllegalStateException("This should not be called")
                }

                override suspend fun getUser(
                    targetUsername: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<User?>> {
                    throw IllegalStateException("This should not be called")
                }
            }

            val viewModel = LoginViewModel(mockApi)

            assertTrue(viewModel.loginState.value is LoginState.Initial)

            viewModel.login("testuser", "testpassword")

            assertTrue(viewModel.loginState.value is LoginState.Loading)

            advanceUntilIdle()

            assertTrue(viewModel.loginState.value is LoginState.Error)
            val errorState = viewModel.loginState.value as LoginState.Error
            assertTrue(errorState.message.contains("An error occurred"))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun testResetState() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        try {
            val mockApi = object : AstigaApi {
                override suspend fun ping(
                    username: String,
                    password: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<PingResponseData?>> {
                    return createSuccessPingResponse()
                }

                override suspend fun getLicense(
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<License?>> {
                    return createSuccessLicenseResponse()
                }

                override suspend fun getUser(
                    targetUsername: String,
                    useBasicAuth: Boolean
                ): Result<SubsonicResponse<User?>> {
                    throw IllegalStateException("This should not be called")
                }
            }

            val viewModel = LoginViewModel(mockApi)

            assertTrue(viewModel.loginState.value is LoginState.Initial)

            viewModel.login("testuser", "testpassword")

            assertTrue(viewModel.loginState.value is LoginState.Loading)

            advanceUntilIdle()

            assertTrue(viewModel.loginState.value is LoginState.Success)

            viewModel.resetState()

            assertTrue(viewModel.loginState.value is LoginState.Initial)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
