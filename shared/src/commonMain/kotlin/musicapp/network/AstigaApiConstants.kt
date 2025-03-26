package musicapp.network

import androidx.datastore.preferences.core.stringPreferencesKey

object AstigaApiConstants {

    const val BASE_URL = "https://play.asti.ga/"
    const val API_VERSION = "1.2.0"
    const val CLIENT_NAME = "Astiga"
    const val ASTIGA_REGISTRATION = "https://play.asti.ga/register"
    const val ASTIGA_SYNC = "https://play.asti.ga/sync"

    object Endpoints {
        const val PING = "rest/ping"
        const val GET_LICENSE = "rest/getLicense"
        const val GET_USER = "rest/getUser"
    }

    object Datastore{
        val USERNAME_TOKEN = stringPreferencesKey("username_token")
        val PASSWORD_TOKEN = stringPreferencesKey("password_token")
    }
}
