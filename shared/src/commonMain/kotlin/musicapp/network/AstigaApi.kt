package musicapp.network

import musicapp.network.models.astiga.LicenseResponse
import musicapp.network.models.astiga.PingResponse

/**
 * Interface for the Astiga API.
 * Provides methods for authentication and license validation.
 */
interface AstigaApi {
    /**
     * Ping the server to check connectivity and authentication.
     *
     * @param username The username/email (URL encoded)
     * @param password The password (will be encrypted with "enc:" prefix and utf8HexEncode)
     * @param version The API version (e.g., "1.2.0")
     * @param client The client name (e.g., "Astiga")
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return PingResponse object containing the response data
     */
    suspend fun ping(
        username: String,
        password: String,
        version: String = "1.2.0",
        client: String = "Astiga",
        useBasicAuth: Boolean = false
    ): PingResponse

    /**
     * Validate the user license.
     *
     * @param username The username/email (URL encoded)
     * @param password The password (will be encrypted with "enc:" prefix and utf8HexEncode)
     * @param version The API version (e.g., "1.2.0")
     * @param client The client name (e.g., "Astiga")
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return LicenseResponse object containing the response data
     */
    suspend fun getLicense(
        username: String,
        password: String,
        version: String = "1.2.0",
        client: String = "Astiga",
        useBasicAuth: Boolean = false
    ): LicenseResponse
}
