package musicapp.network

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
     * @return True if authentication is successful, false otherwise
     */
    suspend fun ping(
        username: String,
        password: String,
        version: String = "1.2.0",
        client: String = "Astiga",
        useBasicAuth: Boolean = false
    ): Boolean

    /**
     * Validate the user license.
     * 
     * @param username The username/email (URL encoded)
     * @param password The password (will be encrypted with "enc:" prefix and utf8HexEncode)
     * @param version The API version (e.g., "1.2.0")
     * @param client The client name (e.g., "Astiga")
     * @param useBasicAuth Whether to use HTTP Basic Authentication instead of query parameters
     * @return True if license is valid, false otherwise
     */
    suspend fun getLicense(
        username: String,
        password: String,
        version: String = "1.2.0",
        client: String = "Astiga",
        useBasicAuth: Boolean = false
    ): Boolean
}
