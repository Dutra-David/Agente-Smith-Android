package com.dutra.agente.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.dutra.agente.data.models.Message
import java.util.UUID
import javax.inject.Inject

/**
 * ChatViewModel - Handles chat screen state and logic
 * 
 * This ViewModel manages:
 * - Chat message list
 * - User input
 * - Message sending
 * - API communication (to be integrated)
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
  // Repository will be injected when implemented
  // private val chatRepository: ChatRepository
) : ViewModel() {
  
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  val messages: StateFlow<List<Message>> = _messages.asStateFlow()
  
  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
  
  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
  
  private val _sessionId = MutableStateFlow<String>(UUID.randomUUID().toString())
  val sessionId: StateFlow<String> = _sessionId.asStateFlow()
  
  init {
    // Initialize with a welcome message
    addWelcomeMessage()
  }
  
  /**
   * Send a user message to the chat
   */
  fun sendMessage(content: String) {
    if (content.isBlank()) {
      return
    }
    
    viewModelScope.launch {
      try {
        // Add user message immediately
        val userMessage = Message(
          id = UUID.randomUUID().toString(),
          content = content,
          isFromUser = true,
          timestamp = System.currentTimeMillis()
        )
        
        _messages.value = _messages.value + userMessage
        
        // TODO: Send to API and get response
        // For now, simulate a response
        simulateAgentResponse(content)
        
      } catch (e: Exception) {
        _errorMessage.value = "Erro ao enviar mensagem: ${e.message}"
      }
    }
  }
  
  /**
   * Simulate an agent response (temporary until API integration)
   */
  private suspend fun simulateAgentResponse(userInput: String) {
    _isLoading.value = true
    
    try {
      // Simulate delay
      kotlinx.coroutines.delay(1000)
      
      val response = when {
        userInput.lowercase().contains("olá") || userInput.lowercase().contains("oi") -> 
          "Olá! Como posso ajudá-lo?"
        userInput.lowercase().contains("ajuda") -> 
          "Claro! Estou aqui para ajudar. O que você precisa?"
        userInput.lowercase().contains("obrigado") -> 
          "De nada! Fico feliz em poder ajudar."
        else -> 
          "Entendi. Pode me dar mais detalhes sobre isso?"
      }
      
      val agentMessage = Message(
        id = UUID.randomUUID().toString(),
        content = response,
        isFromUser = false,
        timestamp = System.currentTimeMillis()
      )
      
      _messages.value = _messages.value + agentMessage
      _errorMessage.value = null
      
    } catch (e: Exception) {
      _errorMessage.value = "Erro ao processar resposta: ${e.message}"
    } finally {
      _isLoading.value = false
    }
  }
  
  /**
   * Clear chat history
   */
  fun clearChat() {
    viewModelScope.launch {
      _messages.value = emptyList()
      _sessionId.value = UUID.randomUUID().toString()
      addWelcomeMessage()
    }
  }
  
  /**
   * Add initial welcome message
   */
  private fun addWelcomeMessage() {
    val welcomeMessage = Message(
      id = UUID.randomUUID().toString(),
      content = "Bem-vindo ao Agente Smith! Como posso ajudá-lo hoje?",
      isFromUser = false,
      timestamp = System.currentTimeMillis()
    )
    _messages.value = listOf(welcomeMessage)
  }
}
