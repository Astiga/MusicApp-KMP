package musicapp.network.models.astiga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("subsonic-response")
    val subsonicResponse: UserResponseData
) : SubsonicResponse {
    override fun isSuccess(): Boolean {
        return subsonicResponse.status == "ok"
    }
}

@Serializable
data class UserResponseData(
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

    @SerialName("user")
    val user: User
)

@Serializable
data class User(
    @SerialName("username")
    val username: String,

    @SerialName("email")
    val email: String,

    @SerialName("scrobblingEnabled")
    val scrobblingEnabled: Boolean,

    @SerialName("adminRole")
    val adminRole: Boolean,

    @SerialName("settingsRole")
    val settingsRole: Boolean,

    @SerialName("downloadRole")
    val downloadRole: Boolean,

    @SerialName("uploadRole")
    val uploadRole: Boolean,

    @SerialName("playlistRole")
    val playlistRole: Boolean,

    @SerialName("coverArtRole")
    val coverArtRole: Boolean,

    @SerialName("commentRole")
    val commentRole: Boolean,

    @SerialName("podcastRole")
    val podcastRole: Boolean,

    @SerialName("streamRole")
    val streamRole: Boolean,

    @SerialName("jukeboxRole")
    val jukeboxRole: Boolean,

    @SerialName("shareRole")
    val shareRole: Boolean
)