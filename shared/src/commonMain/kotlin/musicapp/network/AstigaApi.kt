package musicapp.network

import musicapp.network.models.astiga.License
import musicapp.network.models.astiga.PingResponseData
import musicapp.network.models.astiga.SubsonicResponse
import musicapp.network.models.astiga.User

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
     * @return Result containing PingSuccess on success, or an Exception on failure
     */
    suspend fun ping(
        username: String,
        password: String,
        useBasicAuth: Boolean = false
    ): Result<SubsonicResponse<PingResponseData?>>

    /**
     * Validate the user license.
     * Uses the credentials stored from the last call to ping().
     *
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return Result containing LicenseSuccess on success, or an Exception on failure
     */
    suspend fun getLicense(
        useBasicAuth: Boolean = false
    ): Result<SubsonicResponse<License?>>

    /**
     * Get user information.
     * Uses the credentials stored from the last call to ping().
     *
     * @param targetUsername The username to get information for (usually the same as the stored username)
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return Result containing UserSuccess on success, or an Exception on failure
     */
    suspend fun getUser(
        targetUsername: String,
        useBasicAuth: Boolean = false
    ): Result<SubsonicResponse<User?>>
}
