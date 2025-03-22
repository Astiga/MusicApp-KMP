package musicapp.network.models.astiga

/**
 * Base interface for all Subsonic API responses.
 * Provides a common method to check if the response was successful.
 */
interface SubsonicResponse {
    fun isSuccess(): Boolean
}