package musicapp.utils

/**
 * Utility class for encoding operations.
 */
object EncodingUtils {
    
    /**
     * Encodes a string to UTF-8 and then to hexadecimal.
     * 
     * @param input The string to encode
     * @return The UTF-8 hex encoded string
     */
    fun utf8HexEncode(input: String): String {
        // Convert string to UTF-8 bytes and then to hex
        val bytes = input.encodeToByteArray()
        return bytes.joinToString("") { byte -> 
            byte.toUByte().toString(16).padStart(2, '0')
        }
    }
    
    /**
     * Encodes a username and password for HTTP Basic Authentication.
     * 
     * @param username The username
     * @param password The password
     * @return The Base64 encoded "username:password" string
     */
    fun basicAuthEncode(username: String, password: String): String {
        val credentials = "$username:$password"
        val bytes = credentials.encodeToByteArray()
        return "Basic " + encodeBase64(bytes)
    }
    
    /**
     * Encodes a byte array to Base64.
     * 
     * @param bytes The byte array to encode
     * @return The Base64 encoded string
     */
    private fun encodeBase64(bytes: ByteArray): String {
        val base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val result = StringBuilder()
        
        var i = 0
        while (i < bytes.size) {
            val b1 = bytes.getOrNull(i++)?.toInt() ?: break
            val b2 = bytes.getOrNull(i++)?.toInt() ?: -1
            val b3 = bytes.getOrNull(i++)?.toInt() ?: -1
            
            val group = (b1 and 0xFF shl 16) or
                        (if (b2 >= 0) b2 and 0xFF shl 8 else 0) or
                        (if (b3 >= 0) b3 and 0xFF else 0)
            
            result.append(base64Chars[(group shr 18) and 0x3F])
            result.append(base64Chars[(group shr 12) and 0x3F])
            result.append(if (b2 >= 0) base64Chars[(group shr 6) and 0x3F] else '=')
            result.append(if (b3 >= 0) base64Chars[group and 0x3F] else '=')
        }
        
        return result.toString()
    }
}