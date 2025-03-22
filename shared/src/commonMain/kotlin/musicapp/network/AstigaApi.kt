package musicapp.network

import musicapp.network.models.astiga.LicenseResponse
import musicapp.network.models.astiga.PingResponse
import musicapp.network.models.astiga.UserResponse

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
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return PingResponse object containing the response data
     */
    suspend fun ping(
        username: String,
        password: String,
        useBasicAuth: Boolean = false
    ): PingResponse

    /**
     * Validate the user license.
     *
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return LicenseResponse object containing the response data
     * @throws IllegalStateException if ping() has not been called first
     */
    suspend fun getLicense(
        useBasicAuth: Boolean = false
    ): LicenseResponse

    /**
     * Get user information.
     * Uses the credentials stored from the last call to ping().
     *
     * @param targetUsername The username to get information for (usually the same as the stored username)
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return UserResponse object containing the response data
     * @throws IllegalStateException if ping() has not been called first
     */
    suspend fun getUser(
        targetUsername: String,
        useBasicAuth: Boolean = false
    ): UserResponse
}
