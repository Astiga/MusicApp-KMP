package musicapp.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
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
    ): Boolean {
        return try {
            val response = httpClient.get {
                astigaEndpoint("rest/ping", username, password, version, client, useBasicAuth)
            }.body<String>()

            // Check if the response contains the success status
            response.contains("status=\"ok\"")
        } catch (e: Exception) {
            // Log the error in a production app
            false
        }
    }

    override suspend fun getLicense(
        username: String,
        password: String,
        version: String,
        client: String,
        useBasicAuth: Boolean
    ): Boolean {
        return try {
            val response = httpClient.get {
                astigaEndpoint("rest/getLicense", username, password, version, client, useBasicAuth)
            }.body<String>()

            // Check if the response contains the success status
            response.contains("status=\"ok\"")
        } catch (e: Exception) {
            // Log the error in a production app
            false
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
            json(Json { isLenient = true; ignoreUnknownKeys = true })
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
            takeFrom("https://play.asti.ga/")
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
        }
    }
}
