package com.dutra.agente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.dutra.agente.data.repository.MessageRepository
import com.dutra.agente.utils.Logger
import com.dutra.agente.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ChatViewModel - Gerencia estado da conversa com IA
 *
 * Responsabilidades:
 * - Gerenciar lista de mensagens (StateFlow)
 * - Processar novas mensagens do usuario
 * - Comunicar com MessageRepository
 * - Gerenciar estados: loading, erro, sucesso
 * - Mapear erros para UI
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

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

    // ============= Events =============
    init {
        Logger.viewmodel("ChatViewModel criado")
        createSession()
    }

    /**
     * Cria nova sessao de chat
     */
    fun createSession() {
        Logger.viewmodel("Criando nova sessao")
        viewModelScope.launch {
            when (val result = messageRepository.createSession()) {
                is Result.Success -> {
                    _currentSessionId.emit(result.data)
                    _errorMessage.emit(null)
                    Logger.viewmodel("Sessao criada: ${result.data}")
                }
                is Result.Error -> {
                    _errorMessage.emit(result.message)
                    Logger.viewmodel("Erro ao criar sessao: ${result.message}")
                }
                is Result.Loading -> {
                    _isLoading.emit(true)
                }
            }
        }
    }

    /**
     * Envia mensagem do usuario para processamento
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) {
            viewModelScope.launch {
                _errorMessage.emit("Mensagem nao pode estar vazia")
            }
            return
        }

        Logger.viewmodel("Enviando mensagem: $text")
        viewModelScope.launch {
            _isLoading.emit(true)
            _errorMessage.emit(null)

            val sessionId = _currentSessionId.value

            when (val result = messageRepository.processMessage(text, sessionId)) {
                is Result.Success -> {
                    val response = result.data
                    addMessageToUI(
                        ChatMessage(
                            id = response["id"] as String,
                            text = text,
                            isFromUser = true,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    addMessageToUI(
                        ChatMessage(
                            id = "${System.currentTimeMillis()}_bot",
                            text = response["response"] as String,
                            isFromUser = false,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    _inputText.emit("")
                    Logger.viewmodel("Mensagem processada com sucesso")
                }
                is Result.Error -> {
                    _errorMessage.emit(result.message)
                    Logger.viewmodel("Erro ao processar mensagem: ${result.message}")
                }
                is Result.Loading -> {}
            }
            _isLoading.emit(false)
        }
    }

    /**
     * Atualiza texto de entrada
     */
    fun updateInputText(text: String) {
        viewModelScope.launch {
            _inputText.emit(text)
        }
    }

    /**
     * Limpa historico de chat
     */
    fun clearHistory() {
        Logger.viewmodel("Limpando historico")
        viewModelScope.launch {
            _isLoading.emit(true)
            val sessionId = _currentSessionId.value
            if (sessionId != null) {
                when (val result = messageRepository.clearChatHistory(sessionId)) {
                    is Result.Success -> {
                        _chatMessages.emit(emptyList())
                        _errorMessage.emit(null)
                        Logger.viewmodel("Historico limpo")
                    }
                    is Result.Error -> {
                        _errorMessage.emit(result.message)
                        Logger.viewmodel("Erro ao limpar historico: ${result.message}")
                    }
                    is Result.Loading -> {}
                }
            }
            _isLoading.emit(false)
        }
    }

    /**
     * Limpa mensagem de erro
     */
    fun clearError() {
        viewModelScope.launch {
            _errorMessage.emit(null)
        }
    }

    /**
     * Adiciona mensagem a lista de UI
     */
    private suspend fun addMessageToUI(message: ChatMessage) {
        val currentMessages = _chatMessages.value.toMutableList()
        currentMessages.add(message)
        _chatMessages.emit(currentMessages)
    }

    override fun onCleared() {
        super.onCleared()
        Logger.viewmodel("ChatViewModel destruido")
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
