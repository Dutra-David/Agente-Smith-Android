package com.dutra.agente.dados.local

import com.dutra.agente.dominio.Message
import com.dutra.agente.dominio.MessageSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Fonte de dados local usando em-memória para armazenar mensagens
 * Implementação temporária que será substituída por Room Database
 */
class MessageLocalDataSource @Inject constructor() {
  private val messages = mutableListOf<Message>()

  fun getAllMessages(): Flow<List<Message>> {
    return flowOf(messages.toList())
  }

  suspend fun insertMessage(message: Message) {
    messages.add(message)
  }

  suspend fun clearAllMessages() {
    messages.clear()
  }

  fun createUserMessage(content: String): Message {
    return Message(
      id = UUID.randomUUID().toString(),
      content = content,
      sender = MessageSender.USER,
      timestamp = LocalDateTime.now(),
      isSending = false,
      hasError = false
    )
  }

  fun createAgentMessage(content: String): Message {
    return Message(
      id = UUID.randomUUID().toString(),
      content = content,
      sender = MessageSender.AGENT,
      timestamp = LocalDateTime.now(),
      isSending = false,
      hasError = false
    )
  }
}
