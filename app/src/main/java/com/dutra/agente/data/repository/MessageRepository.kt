package com.dutra.agente.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.dutra.agente.network.ApiService
import com.dutra.agente.utils.Result
import timber.log.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * MessageRepository - Gerencia mensagens com cache em memoria e sincronizacao com backend
 *
 * Funcionalidades:
 * - Cache em memoria para respostas de API
 * - Tratamento robusto de erros com Result<T> wrapper
 * - Logging estruturado com Timber
 * - Operacoes assincronas com Coroutines
 * - Persistencia futura via Room Database (proximo passo)
 */
class MessageRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Cache em memoria
    private val _messageCache = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val messageCache: StateFlow<List<Map<String, Any>>> = _messageCache.asStateFlow()

    private val _sessionCache = MutableStateFlow<String?>(null)
    val sessionCache: StateFlow<String?> = _sessionCache.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    suspend fun processMessage(
        message: String,
        sessionId: String? = null
    ): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Timber.d("Processando mensagem: $message (session: $sessionId)")

            if (message.isBlank()) {
                throw IllegalArgumentException("Mensagem nao pode estar vazia")
            }

            val mockResponse = mapOf(
                "id" to System.currentTimeMillis().toString(),
                "message" to message,
                "response" to "Resposta do agente para: $message",
                "timestamp" to System.currentTimeMillis(),
                "sessionId" to (sessionId ?: generateSessionId())
            )

            updateCache(mockResponse)
            _errorState.emit(null)

            Timber.i("Mensagem processada com sucesso")
            Result.Success(mockResponse)

        } catch (e: IllegalArgumentException) {
            Timber.w("Erro de validacao: ${e.message}")
            _errorState.emit(e.message)
            Result.Error(e.message ?: "Erro de validacao")

        } catch (e: Exception) {
            Timber.e(e, "Erro ao processar mensagem")
            _errorState.emit(e.message)
            Result.Error(e.message ?: "Erro desconhecido ao processar mensagem")
        }
    }

    suspend fun getChatHistory(sessionId: String): Result<List<Map<String, Any>>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Timber.d("Recuperando historico da sessao: $sessionId")

            Result.Success(_messageCache.value.filter { 
                it["sessionId"] == sessionId 
            })

        } catch (e: Exception) {
            Timber.e(e, "Erro ao recuperar historico")
            Result.Error(e.message ?: "Erro ao recuperar historico")
        }
    }

    suspend fun clearChatHistory(sessionId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            Timber.i("Limpando historico da sessao: $sessionId")

            _messageCache.emit(
                _messageCache.value.filter { it["sessionId"] != sessionId }
            )
            _errorState.emit(null)

            Result.Success(true)

        } catch (e: Exception) {
            Timber.e(e, "Erro ao limpar historico")
            Result.Error(e.message ?: "Erro ao limpar historico")
        }
    }

    suspend fun createSession(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            Timber.d("Criando nova sessao")

            val sessionId = generateSessionId()
            _sessionCache.emit(sessionId)
            _errorState.emit(null)

            Timber.i("Sessao criada: $sessionId")
            Result.Success(sessionId)

        } catch (e: Exception) {
            Timber.e(e, "Erro ao criar sessao")
            Result.Error(e.message ?: "Erro ao criar sessao")
        }
    }

    fun getLocalMessages(): List<Map<String, Any>> {
        return _messageCache.value
    }

    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        Timber.i("Limpando todos os caches")
        _messageCache.emit(emptyList())
        _sessionCache.emit(null)
        _errorState.emit(null)
    }

    private suspend fun updateCache(message: Map<String, Any>) {
        val currentCache = _messageCache.value.toMutableList()
        currentCache.add(message)
        _messageCache.emit(currentCache)
    }

    private fun generateSessionId(): String {
        return "session_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}
