package com.dutra.agente.apresentacao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutra.agente.dominio.Message
import com.dutra.agente.dominio.casos_uso.GetMessagesUseCase
import com.dutra.agente.dominio.casos_uso.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
  private val sendMessageUseCase: SendMessageUseCase,
  private val getMessagesUseCase: GetMessagesUseCase
) : ViewModel() {

  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  val messages: StateFlow<List<Message>> = _messages.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  init {
    observeMessages()
  }

  private fun observeMessages() {
    viewModelScope.launch {
      getMessagesUseCase().collect { messageList ->
        _messages.value = messageList
      }
    }
  }

  fun sendMessage(content: String) {
    if (content.isBlank()) return
    
    viewModelScope.launch {
      _isLoading.value = true
      sendMessageUseCase(content)
        .onSuccess { _isLoading.value = false }
        .onFailure { _isLoading.value = false }
    }
  }
}
