package musicapp.network.models.astiga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the response from the Astiga API.
 * The API returns XML responses, but we'll use kotlinx.serialization to parse them.
 */
@Serializable
data class SubsonicResponse(
    @SerialName("status")
    val status: String,
    
    @SerialName("version")
    val version: String,
    
    @SerialName("serverVersion")
    val serverVersion: String? = null,
    
    @SerialName("error")
    val error: String? = null,
    
    @SerialName("message")
    val message: String? = null
) {
    /**
     * Checks if the response indicates success.
     */
    fun isSuccess(): Boolean {
        return status == "ok"
    }
}