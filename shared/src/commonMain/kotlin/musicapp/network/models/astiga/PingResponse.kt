package musicapp.network.models.astiga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PingResponse(
    @SerialName("subsonic-response")
    val subsonicResponse: PingResponseData
) : SubsonicResponse {
    override fun isSuccess(): Boolean {
        return subsonicResponse.status == "ok"
    }
}

@Serializable
data class PingResponseData(
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
)