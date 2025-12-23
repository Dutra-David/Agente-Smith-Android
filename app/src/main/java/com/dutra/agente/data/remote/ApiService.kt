package com.dutra.agente.data.remote

import com.dutra.agente.data.model.MessageRequest
import com.dutra.agente.data.model.MessageResponse
import com.dutra.agente.data.model.ConversationHistory
import retrofit2.http.*

/**
 * API Service Interface para conectar com FastText + MLEnhancedNLP Backend
 * Endpoints reais para processamento de mensagens e respostas IA
 */
interface ApiService {

    /**
     * Endpoint #1: Enviar mensagem e obter resposta IA
     * POST /api/v1/message/send
     * Conecta com FastText + MLEnhancedNLP para processar
     */
    @POST("/api/v1/message/send")
    suspend fun sendMessage(
        @Body messageRequest: MessageRequest
    ): MessageResponse

    /**
     * Endpoint #2: Obter histórico de conversa
     * GET /api/v1/message/history/{conversationId}
     * Retorna últimas mensagens da conversa
     */
    @GET("/api/v1/message/history/{conversationId}")
    suspend fun getConversationHistory(
        @Path("conversationId") conversationId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ConversationHistory

    /**
     * Endpoint #3: Enviar feedback para treinamento
     * POST /api/v1/message/feedback
     * Usa feedback do usuário para melhorar IA
     */
    @POST("/api/v1/message/feedback")
    suspend fun sendFeedback(
        @Body feedbackRequest: FeedbackRequest
    ): FeedbackResponse

    /**
     * Endpoint #4: Status do modelo IA
     * GET /api/v1/models/status
     * Verifica dispon ibilidade e saúde do backend
     */
    @GET("/api/v1/models/status")
    suspend fun getModelStatus(): ModelStatusResponse

    /**
     * Endpoint #5: Clear conversa (reset)
     * POST /api/v1/message/clear
     * Limpa histórico de mensagens
     */
    @POST("/api/v1/message/clear/{conversationId}")
    suspend fun clearConversation(
        @Path("conversationId") conversationId: String
    ): ClearResponse
}

// Data Classes para Request/Response

data class MessageRequest(
    val conversationId: String,
    val userId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val context: List<String> = emptyList() // Últimas mensagens para contexto
)

data class MessageResponse(
    val success: Boolean,
    val message: String,
    val response: String,
    val confidence: Float,
    val processingTime: Long,
    val modelVersion: String,
    val intent: String? = null,
    val entities: List<Entity> = emptyList()
)

data class Entity(
    val type: String,
    val value: String,
    val confidence: Float
)

data class ConversationHistory(
    val conversationId: String,
    val messages: List<HistoryMessage>,
    val totalCount: Int
)

data class HistoryMessage(
    val id: String,
    val sender: String, // "user" ou "agent"
    val message: String,
    val response: String? = null,
    val timestamp: Long,
    val confidence: Float? = null
)

data class FeedbackRequest(
    val messageId: String,
    val conversationId: String,
    val feedback: Int, // 1-5 stars
    val comment: String? = null
)

data class FeedbackResponse(
    val success: Boolean,
    val message: String
)

data class ModelStatusResponse(
    val healthy: Boolean,
    val version: String,
    val uptime: Long,
    val accuracy: Float,
    val totalRequests: Long,
    val averageResponseTime: Long
)

data class ClearResponse(
    val success: Boolean,
    val message: String,
    val messagesDeleted: Int
)
