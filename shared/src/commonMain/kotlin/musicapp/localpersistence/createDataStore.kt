package musicapp.localpersistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun astigaDataStore(): DataStore<Preferences>

internal const val dataStoreFileName = "login_credentials.preferences_pb"
