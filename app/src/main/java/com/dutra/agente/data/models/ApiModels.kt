package com.dutra.agente.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

// Message Models
// Note: MessageRequest is already defined in Message.kt, so we don't redefine it here or we use one of them.
// Let's remove conflicting definitions and add missing ones.

// NLP Models
@Serializable
data class NLPRequest(@SerializedName("text") val text: String)

@Serializable
data class IntentRequest(@SerializedName("text") val text: String)

@Serializable
data class EntityRequest(@SerializedName("text") val text: String)

@Serializable
data class Entity(
    @SerializedName("text") val text: String,
    @SerializedName("type") val type: String,
    @SerializedName("start") val start: Int,
    @SerializedName("end") val end: Int
)

// Response Models
@Serializable
data class NLPResponse(@SerializedName("entities") val entities: List<Entity>)

@Serializable
data class IntentResponse(
    @SerializedName("intent") val intent: String,
    @SerializedName("confidence") val confidence: Float
)

@Serializable
data class EntityResponse(@SerializedName("entities") val entities: List<Entity>)

// Voice Command Models  
@Serializable
data class VoiceCommandRequest(@SerializedName("audio_data") val audioData: String) // Changed ByteArray to String for JSON serialization usually

@Serializable
data class CommandResponse(
    @SerializedName("command") val command: String,
    @SerializedName("confidence") val confidence: Float
)

// Task Models
@Serializable
data class TaskRequest(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("task_name") val taskName: String
)

@Serializable
data class TaskResponse(@SerializedName("task_id") val taskId: String)

@Serializable
data class TaskList(@SerializedName("tasks") val tasks: List<Task>)

@Serializable
data class Task(
    @SerializedName("task_id") val taskId: String,
    @SerializedName("name") val name: String
)

// Execution Models - Missing in original file
@Serializable
data class ExecuteTaskRequest(
    @SerializedName("task_id") val taskId: String
)

@Serializable
data class ExecutionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("result") val result: String
)

// Config Models - Missing in original file
@Serializable
data class ConfigResponse(
    @SerializedName("config") val config: Map<String, String>
)

@Serializable
data class ConfigUpdateRequest(
    @SerializedName("config") val config: Map<String, String>
)

@Serializable
data class UpdateResponse(
    @SerializedName("success") val success: Boolean
)

// Session Models
@Serializable
data class SessionCreateRequest(@SerializedName("user_id") val userId: String)

@Serializable
data class SessionUpdateRequest(@SerializedName("session_id") val sessionId: String)
