package musicapp.localpersistence

interface LocalPersistenceComponents {
    fun writeWithKey(key: String, value: String)
    fun readWithKey(key: String): String
    suspend fun readToken(): String
    fun writeToke(value: String)
}