package musicapp.network.models.astiga

import kotlinx.serialization.Serializable

@Serializable
data class PingSuccess(
    val version: String,
    val serverVersion: String? = null
)

@Serializable
data class LicenseSuccess(
    val version: String,
    val serverVersion: String? = null,
    val license: License
)

@Serializable
data class UserSuccess(
    val version: String,
    val serverVersion: String? = null,
    val user: User
)