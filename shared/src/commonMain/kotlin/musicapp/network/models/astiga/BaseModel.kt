package musicapp.network.models.astiga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubsonicResponse<T>(
    @SerialName("subsonic-response")
    val subsonicResponse: SubsonicResponseData<T>
)

@Serializable
data class SubsonicResponseData<T>(
    @SerialName("status")
    val status: String,

    @SerialName("version")
    val version: String,

    @SerialName("serverVersion")
    val serverVersion: String? = null,

    @SerialName("error")
    val error: String? = null,

    @SerialName("message")
    val message: String? = null,

    val data: T,
)