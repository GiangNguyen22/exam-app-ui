package com.internalexam

import com.internalexam.data.security.KeystoreCryptoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class KeystoreCryptoManagerTest {

    @Test
    fun testEncryptAndDecryptJWT() {
        val originalToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdHVkZW50MSJ9.signature12345"

        val encryptedBase64 = KeystoreCryptoManager.encrypt(originalToken)
        assertNotNull(encryptedBase64)
        assertNotEquals(originalToken, encryptedBase64)

        val decrypted = KeystoreCryptoManager.decrypt(encryptedBase64)
        assertEquals(originalToken, decrypted)
    }

    @Test
    fun testDecryptInvalidDataReturnsNull() {
        val result = KeystoreCryptoManager.decrypt("not-a-valid-base64-or-gcm-data")
        assertNull(result)
    }

    @Test
    fun testEmptyInputHandling() {
        assertEquals("", KeystoreCryptoManager.encrypt(""))
        assertNull(KeystoreCryptoManager.decrypt(null))
        assertNull(KeystoreCryptoManager.decrypt(""))
    }
}
