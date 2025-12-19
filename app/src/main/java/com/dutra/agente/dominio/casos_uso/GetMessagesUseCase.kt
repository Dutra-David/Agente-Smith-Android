package com.dutra.agente.dominio.casos_uso

import com.dutra.agente.dominio.Message
import com.dutra.agente.dominio.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para recuperar mensagens
 * Fornece um fluxo observável de todas as mensagens do histórico
 */
class GetMessagesUseCase @Inject constructor(
  private val messageRepository: MessageRepository
) {
  operator fun invoke(): Flow<List<Message>> {
    return messageRepository.getAllMessages()
  }
}
