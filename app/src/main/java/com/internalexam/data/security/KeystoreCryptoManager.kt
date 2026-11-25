package com.internalexam.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object KeystoreCryptoManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "exam_app_session_token_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val IV_LENGTH_BYTES = 12

    // Fallback key only for JVM unit tests where AndroidKeyStore provider is unavailable
    private var jvmFallbackKey: SecretKey? = null

    private fun getOrCreateSecretKey(): SecretKey {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (keyStore.containsAlias(KEY_ALIAS)) {
                val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
                entry?.secretKey ?: generateKeystoreKey()
            } else {
                generateKeystoreKey()
            }
        } catch (_: Exception) {
            // AndroidKeyStore unavailable (e.g. running on JVM test runner)
            getOrCreateJvmFallbackKey()
        }
    }

    private fun generateKeystoreKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun getOrCreateJvmFallbackKey(): SecretKey {
        return jvmFallbackKey ?: synchronized(this) {
            jvmFallbackKey ?: KeyGenerator.getInstance("AES").apply { init(256) }.generateKey().also {
                jvmFallbackKey = it
            }
        }
    }

    fun encrypt(plaintext: String): String {
        if (plaintext.isEmpty()) return ""
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv ?: ByteArray(IV_LENGTH_BYTES)
        val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))

        val combined = ByteArray(iv.size + ciphertext.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(ciphertext, 0, combined, iv.size, ciphertext.size)

        return try {
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (_: Exception) {
            // Standard Java Base64 for host unit test environment
            java.util.Base64.getEncoder().encodeToString(combined)
        }
    }

    fun decrypt(encryptedBase64: String?): String? {
        if (encryptedBase64.isNullOrBlank()) return null
        return try {
            val combined = try {
                Base64.decode(encryptedBase64, Base64.NO_WRAP)
            } catch (_: Exception) {
                java.util.Base64.getDecoder().decode(encryptedBase64)
            }

            if (combined.size <= IV_LENGTH_BYTES) return null

            val iv = ByteArray(IV_LENGTH_BYTES)
            val ciphertext = ByteArray(combined.size - IV_LENGTH_BYTES)

            System.arraycopy(combined, 0, iv, 0, IV_LENGTH_BYTES)
            System.arraycopy(combined, IV_LENGTH_BYTES, ciphertext, 0, ciphertext.size)

            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(ciphertext)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }
}
