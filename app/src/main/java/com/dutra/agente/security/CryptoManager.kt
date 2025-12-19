package com.dutra.agente.seguranca

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * CryptoManager - Gerenciador de Criptografia
 * Implementa AES-256 para criptografia de dados sensíveis (PSI, token, etc)
 */
class CryptoManager {
    
    companion object {
        private const val ALGORITHM = "AES"
        private const val KEY_SIZE = 256
        private const val ENCODING = "UTF-8"
        
        @Volatile
        private var instance: CryptoManager? = null
        
        fun getInstance(): CryptoManager {
            return instance ?: synchronized(this) {
                CryptoManager().also { instance = it }
            }
        }
    }
    
    private var secretKey: SecretKey? = null
    
    init {
        try {
            // Gerar chave AES-256 aleatória
            val keyGen = KeyGenerator.getInstance(ALGORITHM)
            keyGen.init(KEY_SIZE, SecureRandom())
            secretKey = keyGen.generateKey()
        } catch (e: Exception) {
            throw RuntimeException("Erro ao inicializar criptografia: ${e.message}")
        }
    }
    
    /**
     * Criptografa uma string usando AES-256
     */
    fun encrypt(plaintext: String): String? {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Descriptografa uma string usando AES-256
     */
    fun decrypt(encryptedText: String): String? {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            
            val decodedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Criptografa um objeto sensvel (ex: token)
     */
    fun encryptToken(token: String): String? {
        return encrypt(token)
    }
    
    /**
     * Descriptografa um token
     */
    fun decryptToken(encryptedToken: String): String? {
        return decrypt(encryptedToken)
    }
    
    /**
     * Valida integridade de dados criptografados
     */
    fun isValid(encryptedText: String): Boolean {
        return try {
            decrypt(encryptedText) != null
        } catch (e: Exception) {
            false
        }
    }
}
