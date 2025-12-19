package com.dutra.agente.network

import retrofit2.Call
import retrofit2.http.*
import com.dutra.agente.data.models.*

interface ApiService {
    // Chat and message endpoints
    @POST("api/v1/chat/process")
    fun processMessage(@Body request: MessageRequest): Call<MessageResponse>

    @GET("api/v1/chat/history")
    fun getChatHistory(@Query("session_id") sessionId: String): Call<ChatHistory>

    @POST("api/v1/chat/clear")
    fun clearChatHistory(@Body request: SessionRequest): Call<ClearResponse>

    // NLP and command processing
    @POST("api/v1/nlp/process")
    fun processNLP(@Body request: NLPRequest): Call<NLPResponse>

    @POST("api/v1/nlp/intent_classification")
    fun classifyIntent(@Body request: IntentRequest): Call<IntentResponse>

    @POST("api/v1/nlp/entity_extraction")
    fun extractEntities(@Body request: EntityRequest): Call<EntityResponse>

    // Voice command processing
    @POST("api/v1/voice/process")
    fun processVoiceCommand(@Body request: VoiceCommandRequest): Call<CommandResponse>

    // Task scheduling
    @POST("api/v1/tasks/create")
    fun createTask(@Body request: TaskRequest): Call<TaskResponse>

    @GET("api/v1/tasks/list")
    fun listTasks(@Query("session_id") sessionId: String): Call<TaskList>

    @POST("api/v1/tasks/execute")
    fun executeTask(@Body request: ExecuteTaskRequest): Call<ExecutionResponse>

    // Configuration
    @GET("api/v1/config")
    fun getConfiguration(): Call<ConfigResponse>

    @POST("api/v1/config/update")
    fun updateConfiguration(@Body request: ConfigUpdateRequest): Call<UpdateResponse>

    // Session management
    @POST("api/v1/session/create")
    fun createSession(@Body request: SessionCreateRequest): Call<SessionResponse>

    @POST("api/v1/session/update")
    fun updateSession(@Body request: SessionUpdateRequest): Call<UpdateResponse>
}
