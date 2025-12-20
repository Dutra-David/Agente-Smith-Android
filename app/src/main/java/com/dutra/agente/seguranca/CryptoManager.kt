package com.dutra.agente.seguranca

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.util.*

/**
 * CryptoManager - Gerenciador de criptografia AES-256-GCM
 *
 * Responsabilidades:
 * - Criptografar dados sensíveis (mensagens, tokens, credenciais)
 * - Descriptografar dados criptografados
 * - Gerar e gerenciar chaves de criptografia
 * - Utilizar Android Keystore para armazenamento seguro de chaves
 *
 * Padrão: Singleton com injeção de dependência via Hilt
 */
class CryptoManager(
    private val context: Context
) {
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "agente_smith_key"
        private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val IV_SIZE = 12 // 96 bits para GCM
        private const val TAG_SIZE = 128 // 128 bits para GCM
    }

    private val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    init {
        // Garantir que a chave existe
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }

    /**
     * Gera uma nova chave AES-256 no Android Keystore
     */
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setKeySize(KEY_SIZE)
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setRandomizedEncryptionRequired(true)
        }.build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    /**
     * Criptografa uma string de texto simples
     *
     * @param plaintext Texto a ser criptografado
     * @return String Base64 contendo [IV + CipherText]
     */
    fun encrypt(plaintext: String): String {
        val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey

        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))

        // Combinar IV + Ciphertext
        val encryptedData = iv + ciphertext
        return Base64.getEncoder().encodeToString(encryptedData)
    }

    /**
     * Descriptografa uma string criptografada
     *
     * @param encryptedData String Base64 contendo [IV + CipherText]
     * @return Texto original descriptografado
     */
    fun decrypt(encryptedData: String): String {
        return try {
            val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val decodedData = Base64.getDecoder().decode(encryptedData)

            // Extrair IV (primeiros 12 bytes)
            val iv = decodedData.copyOfRange(0, IV_SIZE)
            val ciphertext = decodedData.copyOfRange(IV_SIZE, decodedData.size)

            // Criar GCMParameterSpec com IV
            val gcmSpec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)

            val plaintext = cipher.doFinal(ciphertext)
            plaintext.toString(StandardCharsets.UTF_8)
        } catch (e: Exception) {
            // Log do erro em produção
            throw CryptoException("Falha ao descriptografar dados", e)
        }
    }

    /**
     * Criptografa e codifica um byte array
     */
    fun encryptBytes(data: ByteArray): String {
        val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val ciphertext = cipher.doFinal(data)
        val encryptedData = iv + ciphertext

        return Base64.getEncoder().encodeToString(encryptedData)
    }

    /**
     * Descriptografa um byte array
     */
    fun decryptBytes(encryptedData: String): ByteArray {
        return try {
            val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val decodedData = Base64.getDecoder().decode(encryptedData)

            val iv = decodedData.copyOfRange(0, IV_SIZE)
            val ciphertext = decodedData.copyOfRange(IV_SIZE, decodedData.size)

            val gcmSpec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)

            cipher.doFinal(ciphertext)
        } catch (e: Exception) {
            throw CryptoException("Falha ao descriptografar bytes", e)
        }
    }

    /**
     * Verifica se uma string foi criptografada corretamente
     */
    fun isEncrypted(text: String): Boolean {
        return try {
            Base64.getDecoder().decode(text)
            text.length > IV_SIZE
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}

/**
 * Exceção personalizada para operações de criptografia
 */
class CryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)
