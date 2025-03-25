package musicapp.localpersistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import musicapp.network.AstigaApiConstants

class LocalPersistenceImp(private val dataStore: DataStore<Preferences>) : LocalPersistenceComponents{
    override suspend fun writeUserNameAndPassword(userName: String, password: String) {
        dataStore.edit {
            it[AstigaApiConstants.Datastore.USERNAME_TOKEN] = userName
            it[AstigaApiConstants.Datastore.PASSWORD_TOKEN] = password
        }
    }

    override suspend fun readUserName(): String {
      return dataStore.data.map { preferences ->
            preferences[AstigaApiConstants.Datastore.USERNAME_TOKEN] ?: ""
        }.first()
    }

    override suspend fun readPassword(): String {
        return dataStore.data.map { preferences ->
            preferences[AstigaApiConstants.Datastore.PASSWORD_TOKEN] ?: ""
        }.first()
    }

    override suspend fun deleteUserNameAndPassword() {
        dataStore.edit {
            it.clear()
        }
    }


}