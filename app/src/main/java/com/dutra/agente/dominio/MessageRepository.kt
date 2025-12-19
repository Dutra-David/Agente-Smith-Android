package com.dutra.agente.dominio

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface para gerenciar dados de mensagens
 * Define o contrato para operações de leitura/escrita de mensagens
 */
interface MessageRepository {
  /**
   * Recupera todas as mensagens
   * @return Flow de lista de mensagens para observação em tempo real
   */
  fun getAllMessages(): Flow<List<Message>>

  /**
   * Insere uma nova mensagem
   * @param message Mensagem a ser inserida
   */
  suspend fun insertMessage(message: Message)

  /**
   * Envia uma mensagem para o agente IA
   * @param content Conteúdo da mensagem
   * @return Mensagem de resposta do agente
   */
  suspend fun sendMessage(content: String): Message

  /**
   * Limpa todo o histórico de mensagens
   */
  suspend fun clearAllMessages()
}
