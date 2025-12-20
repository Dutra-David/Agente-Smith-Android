package com.dutra.agente.seguranca

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecurePreferences - Wrapper para armazenamento seguro de dados no dispositivo
 *
 * Fornece uma camada de abstração sobre EncryptedSharedPreferences do Jetpack Security.
 * Todos os dados são criptografados automaticamente usando AES-256-GCM.
 *
 * Responsabilidades:
 * - Armazenar dados sensíveis de forma segura (tokens, ID de sessão, preferências do usuário)
 * - Criptografia automática/descriptografia transparente
 * - Gerenciamento de chave mestra (MasterKey) do Android Keystore
 * - Métodos de conveniência para tipos comuns (String, Boolean, Int, Long)
 *
 * Padrão: Singleton com injeção de dependência via Hilt
 */
class SecurePreferences(
    context: Context,
    private val fileName: String = "agente_smith_secure_prefs"
) {

    companion object {
        // Chaves de preferências
        const val KEY_SESSION_ID = "session_id"
        const val KEY_USER_TOKEN = "user_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_LAST_SYNC = "last_sync"
        const val KEY_API_ENDPOINT = "api_endpoint"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_LANGUAGE = "language"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        fileName,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // ==================== STRING ====================
    /**
     * Salva uma string criptografada
     */
    fun putString(key: String, value: String) {
        encryptedPreferences.edit().putString(key, value).apply()
    }

    /**
     * Recupera uma string descriptografada
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return encryptedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // ==================== BOOLEAN ====================
    /**
     * Salva um boolean criptografado
     */
    fun putBoolean(key: String, value: Boolean) {
        encryptedPreferences.edit().putBoolean(key, value).apply()
    }

    /**
     * Recupera um boolean descriptografado
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedPreferences.getBoolean(key, defaultValue)
    }

    // ==================== INTEGER ====================
    /**
     * Salva um inteiro criptografado
     */
    fun putInt(key: String, value: Int) {
        encryptedPreferences.edit().putInt(key, value).apply()
    }

    /**
     * Recupera um inteiro descriptografado
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return encryptedPreferences.getInt(key, defaultValue)
    }

    // ==================== LONG ====================
    /**
     * Salva um long criptografado
     */
    fun putLong(key: String, value: Long) {
        encryptedPreferences.edit().putLong(key, value).apply()
    }

    /**
     * Recupera um long descriptografado
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return encryptedPreferences.getLong(key, defaultValue)
    }

    // ==================== OPERAÇÕES ====================
    /**
     * Remove uma preferência
     */
    fun remove(key: String) {
        encryptedPreferences.edit().remove(key).apply()
    }

    /**
     * Limpa todas as preferências
     */
    fun clearAll() {
        encryptedPreferences.edit().clear().apply()
    }

    /**
     * Verifica se uma chave existe
     */
    fun contains(key: String): Boolean {
        return encryptedPreferences.contains(key)
    }

    /**
     * Retorna todas as preferências (com cuidado - pode incluir dados sensíveis)
     */
    fun getAll(): Map<String, *> {
        return encryptedPreferences.all
    }

    // ==================== DADOS DE SESSÃO ====================
    /**
     * Salva informações de sessão do usuário
     */
    fun saveUserSession(
        sessionId: String,
        userId: String,
        userName: String,
        token: String
    ) {
        encryptedPreferences.edit().apply {
            putString(KEY_SESSION_ID, sessionId)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_TOKEN, token)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Recupera token do usuário (para autenticação)
     */
    fun getUserToken(): String? {
        return getString(KEY_USER_TOKEN).takeIf { it.isNotEmpty() }
    }

    /**
     * Verifica se usuário está logado
     */
    fun isLoggedIn(): Boolean {
        return getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Limpa sessão do usuário (logout)
     */
    fun clearUserSession() {
        encryptedPreferences.edit().apply {
            remove(KEY_SESSION_ID)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_TOKEN)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }
}
