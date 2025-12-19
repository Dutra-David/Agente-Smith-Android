package com.dutra.agente.data.models

import com.google.gson.annotations.SerializedName

// Message Models
data class MessageRequest(
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("user_id")
    val userId: String? = null
)

data class MessageResponse(
    @SerializedName("response")
    val response: String,
    @SerializedName("intent")
    val intent: String,
    @SerializedName("entities")
    val entities: List<Entity>,
    @SerializedName("confidence")
    val confidence: Float
)

data class ChatHistory(
    @SerializedName("messages")
    val messages: List<ChatMessage>,
    @SerializedName("session_id")
    val sessionId: String
)

data class ChatMessage(
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("sender")
    val sender: String,
    @SerializedName("timestamp")
    val timestamp: Long
)

// NLP Models
data class NLPRequest(@SerializedName("text") val text: String)
data class IntentRequest(@SerializedName("text") val text: String)
data class EntityRequest(@SerializedName("text") val text: String)
data class Entity(
    @SerializedName("text") val text: String,
    @SerializedName("type") val type: String,
    @SerializedName("start") val start: Int,
    @SerializedName("end") val end: Int
)

// Response Models
data class NLPResponse(@SerializedName("entities") val entities: List<Entity>)
data class IntentResponse(
    @SerializedName("intent") val intent: String,
    @SerializedName("confidence") val confidence: Float
)
data class EntityResponse(@SerializedName("entities") val entities: List<Entity>)

// Voice Command Models  
data class VoiceCommandRequest(@SerializedName("audio_data") val audioData: ByteArray)
data class CommandResponse(
    @SerializedName("command") val command: String,
    @SerializedName("confidence") val confidence: Float
)

// Task Models
data class TaskRequest(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("task_name") val taskName: String
)
data class TaskResponse(@SerializedName("task_id") val taskId: String)
data class TaskList(@SerializedName("tasks") val tasks: List<Task>)
data class Task(
    @SerializedName("task_id") val taskId: String,
    @SerializedName("name") val name: String
)

// Session Models
data class SessionRequest(@SerializedName("session_id") val sessionId: String)
data class SessionResponse(@SerializedName("session_id") val sessionId: String)
data class SessionCreateRequest(@SerializedName("user_id") val userId: String)
