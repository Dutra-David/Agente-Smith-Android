package com.dutra.agente.dominio.casos_uso

import com.dutra.agente.dominio.Message
import com.dutra.agente.dominio.MessageRepository
import javax.inject.Inject

/**
 * Caso de uso para enviar mensagens
 * Responsável por orquestrar o envio de uma mensagem do usuário e obter a resposta do agente
 */
class SendMessageUseCase @Inject constructor(
  private val messageRepository: MessageRepository
) {
  suspend operator fun invoke(content: String): Result<Message> {
    return try {
      val message = messageRepository.sendMessage(content)
      Result.success(message)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
