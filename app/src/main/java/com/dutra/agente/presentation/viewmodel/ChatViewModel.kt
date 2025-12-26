package com.dutra.agente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.dutra.agente.data.repository.MessageRepository
import com.dutra.agente.utils.Logger
import com.dutra.agente.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log

/**
 * ChatViewModel - Gerencia estado da conversa com IA
 *
 * Responsabilidades:
 * - Gerenciar lista de mensagens (StateFlow)
 * - Processar novas mensagens do usuario
 * - Comunicar com MessageRepository
 * - Gerenciar estados: loading, erro, sucesso
 * - Mapear erros para UI
 * 
 * IMPORTANTE: Não faz inicialização pesada no init() para evitar travamento
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    
    private val TAG = "ChatViewModel"
    
    // ============= State Flows =============
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()
    
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()
    
    private var sessionCreatedAttempted = false
    
    // ============= Init Block =============
    init {
        Log.d(TAG, "ChatViewModel criado")
        try {
            Logger.viewmodel("ChatViewModel criado")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao logar inicialização", e)
        }
        // NÃO cria sessão aqui - fazer depois via UI
        // Inicializar com dados vazio seguros
        _chatMessages.value = emptyList()
        _currentSessionId.value = null
        _inputText.value = ""
        Log.d(TAG, "ChatViewModel inicializado com estado seguro")
    }
    
    /**
     * Cria nova sessao de chat - CHAMADO DEPOIS QUE UI ESTÁ PRONTA
     */
    fun createSession() {
        if (sessionCreatedAttempted) return
        sessionCreatedAttempted = true
        
        Log.d(TAG, "Criando nova sessao")
        try {
            Logger.viewmodel("Criando nova sessao")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao logar criação de sessão", e)
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                when (val result = messageRepository.createSession()) {
                    is Result.Success -> {
                        _currentSessionId.value = result.data
                        _errorMessage.value = null
                        Log.d(TAG, "Sessão criada: ${result.data}")
                        try {
                            Logger.viewmodel("Sessão criada: ${result.data}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Erro ao logar sucesso de sessão", e)
                        }
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                        _currentSessionId.value = "mock_session_${System.currentTimeMillis()}"
                        Log.e(TAG, "Erro ao criar sessão: ${result.message}")
                        try {
                            Logger.viewmodel("Erro ao criar sessão: ${result.message}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Erro ao logar erro de sessão", e)
                        }
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exceção na criação de sessão", e)
                _errorMessage.value = "Erro: ${e.message}"
                _currentSessionId.value = "mock_session_${System.currentTimeMillis()}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Envia mensagem do usuario para processamento
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) {
            viewModelScope.launch {
                _errorMessage.value = "Mensagem não pode estar vazia"
            }
            return
        }
        
        Log.d(TAG, "Enviando mensagem: $text")
        try {
            Logger.viewmodel("Enviando mensagem: $text")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao logar envio de mensagem", e)
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val sessionId = _currentSessionId.value ?: "mock_session_${System.currentTimeMillis()}"
                
                when (val result = messageRepository.processMessage(text, sessionId)) {
                    is Result.Success -> {
                        val response = result.data
                        
                        // Adicionar mensagem do usuário
                        addMessageToUI(
                            ChatMessage(
                                id = response["id"] as? String ?: "${System.currentTimeMillis()}_user",
                                text = text,
                                isFromUser = true,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        
                        // Adicionar resposta do bot
                        addMessageToUI(
                            ChatMessage(
                                id = "${System.currentTimeMillis()}_bot",
                                text = response["response"] as? String ?: "Resposta padrão do bot",
                                isFromUser = false,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        
                        _inputText.value = ""
                        Log.d(TAG, "Mensagem processada com sucesso")
                        try {
                            Logger.viewmodel("Mensagem processada com sucesso")
                        } catch (e: Exception) {
                            Log.e(TAG, "Erro ao logar sucesso de mensagem", e)
                        }
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                        // Adicionar mensagem fake do bot em modo offline
                        addMessageToUI(
                            ChatMessage(
                                id = "${System.currentTimeMillis()}_bot_offline",
                                text = "[Modo Offline] Não consegui processar sua mensagem. Erro: ${result.message}",
                                isFromUser = false,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        Log.e(TAG, "Erro ao processar mensagem: ${result.message}")
                        try {
                            Logger.viewmodel("Erro ao processar mensagem: ${result.message}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Erro ao logar erro de mensagem", e)
                        }
                    }
                    is Result.Loading -> {}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exceção ao processar mensagem", e)
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Atualiza texto de entrada
     */
    fun updateInputText(text: String) {
        viewModelScope.launch {
            _inputText.value = text
        }
    }
    
    /**
     * Limpa historico de chat
     */
    fun clearHistory() {
        Log.d(TAG, "Limpando historico")
        try {
            Logger.viewmodel("Limpando historico")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao logar limpeza de historico", e)
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val sessionId = _currentSessionId.value
                
                if (sessionId != null) {
                    when (val result = messageRepository.clearChatHistory(sessionId)) {
                        is Result.Success -> {
                            _chatMessages.value = emptyList()
                            _errorMessage.value = null
                            Log.d(TAG, "Historico limpo")
                            try {
                                Logger.viewmodel("Historico limpo")
                            } catch (e: Exception) {
                                Log.e(TAG, "Erro ao logar limpeza bem-sucedida", e)
                            }
                        }
                        is Result.Error -> {
                            _errorMessage.value = result.message
                            Log.e(TAG, "Erro ao limpar historico: ${result.message}")
                            try {
                                Logger.viewmodel("Erro ao limpar historico: ${result.message}")
                            } catch (e: Exception) {
                                Log.e(TAG, "Erro ao logar erro de limpeza", e)
                            }
                        }
                        is Result.Loading -> {}
                    }
                } else {
                    _chatMessages.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exceção ao limpar historico", e)
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpa mensagem de erro
     */
    fun clearError() {
        viewModelScope.launch {
            _errorMessage.value = null
        }
    }
    
    /**
     * Adiciona mensagem a lista de UI
     */
    private fun addMessageToUI(message: ChatMessage) {
        val currentMessages = _chatMessages.value.toMutableList()
        currentMessages.add(message)
        _chatMessages.value = currentMessages
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ChatViewModel destruido")
        try {
            Logger.viewmodel("ChatViewModel destruido")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao logar destruição", e)
        }
    }
}

/**
 * Data class para representar mensagem na UI
 */
data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long
)
