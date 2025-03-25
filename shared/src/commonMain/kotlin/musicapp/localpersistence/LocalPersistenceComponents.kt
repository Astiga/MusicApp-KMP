package musicapp.localpersistence

interface LocalPersistenceComponents {
    suspend fun writeUserNameAndPassword(userName: String, password: String)
    suspend fun readUserName(): String
    suspend fun readPassword(): String
    suspend fun deleteUserNameAndPassword()
}