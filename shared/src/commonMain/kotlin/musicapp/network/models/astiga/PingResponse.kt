package musicapp.network.models.astiga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PingResponseData(
    @SerialName("version")
    val version: String,
)