package musicapp.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import musicapp.network.models.astiga.*
import musicapp.utils.EncodingUtils
import kotlin.Result

/**
 * Implementation of the Astiga API interface.
 */
class AstigaApiImpl : AstigaApi {

    private var storedUsername: String? = null
    private var storedPassword: String? = null


    override suspend fun ping(
        username: String,
        password: String,
        useBasicAuth: Boolean
    ): Result<PingSuccess> {
        storedUsername = username
        storedPassword = password

        return try {
            val response = httpClient.get {
                astigaEndpoint(
                    path = AstigaApiConstants.Endpoints.PING,
                    username = username,
                    password = password,
                    useBasicAuth = useBasicAuth
                )
            }.body<PingResponse>()

            if (response.isSuccess()) {
                Result.success(
                    PingSuccess(
                        version = response.subsonicResponse.version,
                        serverVersion = response.subsonicResponse.serverVersion
                    )
                )
            } else {
                Result.failure(
                    Exception(response.subsonicResponse.message ?: "Unknown error")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLicense(
        useBasicAuth: Boolean
    ): Result<LicenseSuccess> {
        try {
            val username = storedUsername ?: throw IllegalStateException("Username not stored. Call ping() first.")
            val password = storedPassword ?: throw IllegalStateException("Password not stored. Call ping() first.")

            val response = httpClient.get {
                astigaEndpoint(
                    path = AstigaApiConstants.Endpoints.GET_LICENSE,
                    username = username,
                    password = password,
                    useBasicAuth = useBasicAuth
                )
            }.body<LicenseResponse>()

            return if (response.isSuccess()) {
                Result.success(
                    LicenseSuccess(
                        version = response.subsonicResponse.version,
                        serverVersion = response.subsonicResponse.serverVersion,
                        license = response.subsonicResponse.license
                    )
                )
            } else {
                Result.failure(
                    Exception(response.subsonicResponse.message ?: "Unknown error")
                )
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getUser(
        targetUsername: String,
        useBasicAuth: Boolean
    ): Result<UserSuccess> {
        try {
            val username = storedUsername ?: throw IllegalStateException("Username not stored. Call ping() first.")
            val password = storedPassword ?: throw IllegalStateException("Password not stored. Call ping() first.")

            val response = httpClient.get {
                astigaEndpoint(
                    path = AstigaApiConstants.Endpoints.GET_USER,
                    username = username,
                    password = password,
                    useBasicAuth = useBasicAuth,
                    additionalParams = mapOf("username" to targetUsername)
                )
            }.body<UserResponse>()

            return if (response.isSuccess()) {
                Result.success(
                    UserSuccess(
                        version = response.subsonicResponse.version,
                        serverVersion = response.subsonicResponse.serverVersion,
                        user = response.subsonicResponse.user
                    )
                )
            } else {
                Result.failure(
                    Exception(response.subsonicResponse.message ?: "Unknown error")
                )
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


    private val httpClient = HttpClient {
        expectSuccess = true
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    private fun HttpRequestBuilder.astigaEndpoint(
        path: String,
        username: String,
        password: String,
        useBasicAuth: Boolean = false,
        additionalParams: Map<String, String> = emptyMap()
    ) {
        // Obfuscate password with "enc:" prefix and utf8HexEncode
        val obfuscatedPassword = "enc:" + EncodingUtils.utf8HexEncode(password)

        url {
            takeFrom(AstigaApiConstants.BASE_URL)
            encodedPath = "$path.view"

            if (useBasicAuth) {
                // Use HTTP Basic Authentication
                header(HttpHeaders.Authorization, EncodingUtils.basicAuthEncode(username, password))
                parameters.append("v", AstigaApiConstants.API_VERSION)
                parameters.append("c", AstigaApiConstants.CLIENT_NAME)
            } else {
                // Use query parameters
                parameters.append("u", username)
                parameters.append("p", obfuscatedPassword)
                parameters.append("v", AstigaApiConstants.API_VERSION)
                parameters.append("c", AstigaApiConstants.CLIENT_NAME)
            }

            additionalParams.forEach { (key, value) ->
                parameters.append(key, value)
            }

            parameters.append("f", "json")
        }
    }
}
