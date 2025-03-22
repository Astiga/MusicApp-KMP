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
    ): PingResponse {
        storedUsername = username
        storedPassword = password

        return try {
            httpClient.get {
                astigaEndpoint(
                    path = AstigaApiConstants.Endpoints.PING,
                    username = username,
                    password = password,
                    useBasicAuth = useBasicAuth
                )
            }.body<PingResponse>()
        } catch (e: Exception) {
            PingResponse(
                subsonicResponse = PingResponseData(
                    status = "failed",
                    version = AstigaApiConstants.API_VERSION,
                    error = "0",
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }

    override suspend fun getLicense(
        useBasicAuth: Boolean
    ): LicenseResponse {

        val username = storedUsername ?: throw IllegalStateException("Username not stored. Call ping() first.")
        val password = storedPassword ?: throw IllegalStateException("Password not stored. Call ping() first.")

        return try {
            httpClient.get {
                astigaEndpoint(
                    path = AstigaApiConstants.Endpoints.GET_LICENSE,
                    username = username,
                    password = password,
                    useBasicAuth = useBasicAuth
                )
            }.body<LicenseResponse>()
        } catch (e: Exception) {
            LicenseResponse(
                subsonicResponse = LicenseResponseData(
                    status = "failed",
                    version = AstigaApiConstants.API_VERSION,
                    error = "0",
                    message = e.message ?: "Unknown error",
                    license = License(
                        valid = false,
                        email = "",
                        licenseExpires = ""
                    )
                )
            )
        }
    }

    override suspend fun getUser(
        targetUsername: String,
        useBasicAuth: Boolean
    ): UserResponse {

        val username = storedUsername ?: throw IllegalStateException("Username not stored. Call ping() first.")
        val password = storedPassword ?: throw IllegalStateException("Password not stored. Call ping() first.")

        return try {
            httpClient.get {
                astigaEndpoint(
                    path = AstigaApiConstants.Endpoints.GET_USER,
                    username = username,
                    password = password,
                    useBasicAuth = useBasicAuth,
                    additionalParams = mapOf("username" to targetUsername)
                )
            }.body<UserResponse>()
        } catch (e: Exception) {
            UserResponse(
                subsonicResponse = UserResponseData(
                    status = "failed",
                    version = AstigaApiConstants.API_VERSION,
                    error = "0",
                    message = e.message ?: "Unknown error",
                    user = User(
                        username = "",
                        email = "",
                        scrobblingEnabled = false,
                        adminRole = false,
                        settingsRole = false,
                        downloadRole = false,
                        uploadRole = false,
                        playlistRole = false,
                        coverArtRole = false,
                        commentRole = false,
                        podcastRole = false,
                        streamRole = false,
                        jukeboxRole = false,
                        shareRole = false
                    )
                )
            )
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
