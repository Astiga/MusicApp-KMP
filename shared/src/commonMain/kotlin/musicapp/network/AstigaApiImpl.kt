package musicapp.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Implementation of the Astiga API interface.
 */
class AstigaApiImpl : AstigaApi {

    override suspend fun ping(
        username: String,
        password: String,
        version: String,
        client: String
    ): Boolean {
        return try {
            val response = httpClient.get {
                astigaEndpoint("rest/ping", username, password, version, client)
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
        client: String
    ): Boolean {
        return try {
            val response = httpClient.get {
                astigaEndpoint("rest/getLicense", username, password, version, client)
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
        client: String
    ) {
        url {
            takeFrom("https://play.asti.ga/")
            encodedPath = "$path.view"
            parameters.append("u", username)
            parameters.append("p", password)
            parameters.append("v", version)
            parameters.append("c", client)
        }
    }
}
