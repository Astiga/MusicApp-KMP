package musicapp.network

object AstigaApiConstants {

    const val BASE_URL = "https://play.asti.ga/"
    const val API_VERSION = "1.2.0"
    const val CLIENT_NAME = "Astiga"
    const val ASTIGA_REGISTRATION = "https://play.asti.ga/register"

    object Endpoints {
        const val PING = "rest/ping"
        const val GET_LICENSE = "rest/getLicense"
        const val GET_USER = "rest/getUser"
    }
}
