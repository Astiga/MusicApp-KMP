package musicapp.localpersistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class DataStoreHelper : KoinComponent {
    val context: Context by inject()
}

actual fun astigaDataStore(): DataStore<Preferences> {

    return runBlocking {
        val dataStoreHelper = DataStoreHelper()
        getDataStore(producePath = { dataStoreHelper.context.filesDir.resolve(dataStoreFileName).absolutePath })
    }
}


private lateinit var dataStore: DataStore<Preferences>

private val lock = Any()

fun getDataStore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { dataStore = it }
        }
    }
