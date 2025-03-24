package musicapp.localpersistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual fun astigaDataStore(): DataStore<Preferences> {
    return runBlocking {
        getDataStore(producePath = { ContextUtils.context.filesDir.resolve(dataStoreFileName).absolutePath })
    }
}

private lateinit var dataStore: DataStore<Preferences>

@OptIn(InternalCoroutinesApi::class)
private val lock = SynchronizedObject()

fun getDataStore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { dataStore = it }
        }
    }