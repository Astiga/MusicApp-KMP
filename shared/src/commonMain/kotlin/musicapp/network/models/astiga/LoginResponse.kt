package musicapp.network.models.astiga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LicenseResponse(
    @SerialName("subsonic-response")
    val subsonicResponse: LicenseResponseData
) : SubsonicResponse {
    override fun isSuccess(): Boolean {
        return subsonicResponse.status == "ok"
    }
}

@Serializable
data class LicenseResponseData(
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

    @SerialName("license")
    val license: License
)

@Serializable
data class License(
    @SerialName("valid")
    val valid: Boolean,

    @SerialName("email")
    val email: String,

    @SerialName("licenseExpires")
    val licenseExpires: String
)
