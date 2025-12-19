package com.dutra.agente.dados

import com.dutra.agente.dados.local.MessageLocalDataSource
import com.dutra.agente.dominio.Message
import com.dutra.agente.dominio.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementação da interface MessageRepository
 * Orquestra a comunicação entre a camada de apresentação e a fonte de dados
 */
class MessageRepositoryImpl @Inject constructor(
  private val localDataSource: MessageLocalDataSource
) : MessageRepository {

  override fun getAllMessages(): Flow<List<Message>> {
    return localDataSource.getAllMessages()
  }

  override suspend fun insertMessage(message: Message) {
    localDataSource.insertMessage(message)
  }

  override suspend fun sendMessage(content: String): Message {
    val userMessage = localDataSource.createUserMessage(content)
    insertMessage(userMessage)
    
    val agentResponse = localDataSource.createAgentMessage(
      "Resposta do agente para: $content"
    )
    insertMessage(agentResponse)
    
    return agentResponse
  }

  override suspend fun clearAllMessages() {
    localDataSource.clearAllMessages()
  }
}
