package com.dutra.agente.dominio

import java.time.LocalDateTime

/**
 * Entidade de Mensagem do domínio
 * Representa uma mensagem no chat entre usuário e agente IA
 */
data class Message(
  val id: String,
  val content: String,
  val sender: MessageSender,
  val timestamp: LocalDateTime,
  val isSending: Boolean = false,
  val hasError: Boolean = false
)

/**
 * Enum para identificar o remetente da mensagem
 */
enum class MessageSender {
  USER,
  AGENT
}
