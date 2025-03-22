package musicapp.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import musicapp.network.models.astiga.License
import musicapp.network.models.astiga.LicenseResponse
import musicapp.network.models.astiga.LicenseResponseData
import musicapp.network.models.astiga.PingResponse
import musicapp.network.models.astiga.PingResponseData
import musicapp.utils.EncodingUtils

/**
 * Implementation of the Astiga API interface.
 */
class AstigaApiImpl : AstigaApi {

    override suspend fun ping(
        username: String,
        password: String,
        version: String,
        client: String,
        useBasicAuth: Boolean
    ): PingResponse {
        return try {
            httpClient.get {
                astigaEndpoint(AstigaApiConstants.Endpoints.PING, username, password, version, client, useBasicAuth)
            }.body<PingResponse>()
        } catch (e: Exception) {
            // Log the error in a production app
            PingResponse(
                subsonicResponse = PingResponseData(
                    status = "failed",
                    version = version,
                    error = "0",
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }

    override suspend fun getLicense(
        username: String,
        password: String,
        version: String,
        client: String,
        useBasicAuth: Boolean
    ): LicenseResponse {
        return try {
            httpClient.get {
                astigaEndpoint(AstigaApiConstants.Endpoints.GET_LICENSE, username, password, version, client, useBasicAuth)
            }.body<LicenseResponse>()
        } catch (e: Exception) {
            LicenseResponse(
                subsonicResponse = LicenseResponseData(
                    status = "failed",
                    version = version,
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
        version: String,
        client: String,
        useBasicAuth: Boolean = false
    ) {
        // Obfuscate password with "enc:" prefix and utf8HexEncode
        val obfuscatedPassword = "enc:" + EncodingUtils.utf8HexEncode(password)

        url {
            takeFrom(AstigaApiConstants.BASE_URL)
            encodedPath = "$path.view"

            if (useBasicAuth) {
                // Use HTTP Basic Authentication
                header(HttpHeaders.Authorization, EncodingUtils.basicAuthEncode(username, password))
                parameters.append("v", version)
                parameters.append("c", client)
            } else {
                // Use query parameters
                parameters.append("u", username)
                parameters.append("p", obfuscatedPassword)
                parameters.append("v", version)
                parameters.append("c", client)
            }
            parameters.append("f", "json")
        }
    }
}
