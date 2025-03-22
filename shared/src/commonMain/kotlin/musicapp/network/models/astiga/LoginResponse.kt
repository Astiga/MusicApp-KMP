package musicapp.network.models.astiga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class License(
    @SerialName("valid")
    val valid: Boolean,

    @SerialName("email")
    val email: String,

    @SerialName("licenseExpires")
    val licenseExpires: String
)
