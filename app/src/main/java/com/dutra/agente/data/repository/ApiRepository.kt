package com.dutra.agente.data.repository

import android.content.Context
import com.dutra.agente.network.ApiService
import com.dutra.agente.data.models.*
import retrofit2.Callback

class ApiRepository(private val apiService: ApiService, private val context: Context) {
    
    // Chat operations
    fun processMessage(
        sessionId: String,
        message: String,
        callback: Callback<MessageResponse>
    ) {
        val request = MessageRequest(sessionId, message)
        apiService.processMessage(request).enqueue(callback)
    }
    
    fun getChatHistory(
        sessionId: String,
        callback: Callback<ChatHistory>
    ) {
        apiService.getChatHistory(sessionId).enqueue(callback)
    }
    
    fun clearChatHistory(
        sessionId: String,
        callback: Callback<ClearResponse>
    ) {
        val request = SessionRequest(sessionId)
        apiService.clearChatHistory(request).enqueue(callback)
    }
    
    // NLP operations
    fun classifyIntent(
        text: String,
        callback: Callback<IntentResponse>
    ) {
        val request = IntentRequest(text)
        apiService.classifyIntent(request).enqueue(callback)
    }
    
    fun extractEntities(
        text: String,
        callback: Callback<EntityResponse>
    ) {
        val request = EntityRequest(text)
        apiService.extractEntities(request).enqueue(callback)
    }
    
    fun processNLP(
        text: String,
        callback: Callback<NLPResponse>
    ) {
        val request = NLPRequest(text)
        apiService.processNLP(request).enqueue(callback)
    }
    
    // Voice command operations
    fun processVoiceCommand(
        audioData: ByteArray,
        callback: Callback<CommandResponse>
    ) {
        val request = VoiceCommandRequest(audioData)
        apiService.processVoiceCommand(request).enqueue(callback)
    }
    
    // Task operations
    fun createTask(
        sessionId: String,
        taskName: String,
        callback: Callback<TaskResponse>
    ) {
        val request = TaskRequest(sessionId, taskName, emptyMap())
        apiService.createTask(request).enqueue(callback)
    }
    
    fun listTasks(
        sessionId: String,
        callback: Callback<TaskList>
    ) {
        apiService.listTasks(sessionId).enqueue(callback)
    }
    
    // Session operations
    fun createSession(
        userId: String,
        callback: Callback<SessionResponse>
    ) {
        val request = SessionCreateRequest(userId)
        apiService.createSession(request).enqueue(callback)
    }
}
