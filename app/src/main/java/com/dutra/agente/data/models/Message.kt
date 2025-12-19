package com.dutra.agente.data.models

import kotlinx.serialization.Serializable

/**
 * Message - Data model for chat messages
 * 
 * @param id Unique identifier for the message
 * @param content The message text content
 * @param isFromUser Whether the message is from the user (true) or agent (false)
 * @param timestamp Milliseconds since epoch when message was sent
 * @param metadata Additional metadata about the message (optional)
 */
@Serializable
data class Message(
  val id: String,
  val content: String,
  val isFromUser: Boolean,
  val timestamp: Long,
  val metadata: Map<String, String>? = null
)

/**
 * MessageRequest - Request model for sending messages to API
 */
@Serializable
data class MessageRequest(
  val sessionId: String,
  val content: String,
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * MessageResponse - Response model from message processing API
 */
@Serializable
data class MessageResponse(
  val id: String,
  val content: String,
  val timestamp: Long,
  val confidence: Float? = null
)

/**
 * ChatHistory - Data model for chat history
 */
@Serializable
data class ChatHistory(
  val sessionId: String,
  val messages: List<Message>,
  val createdAt: Long,
  val updatedAt: Long
)

/**
 * SessionRequest - Request model for session management
 */
@Serializable
data class SessionRequest(
  val sessionId: String
)

/**
 * SessionResponse - Response model for session creation/updates
 */
@Serializable
data class SessionResponse(
  val sessionId: String,
  val createdAt: Long,
  val status: String = "active"
)

/**
 * ClearResponse - Response for clearing chat history
 */
@Serializable
data class ClearResponse(
  val success: Boolean,
  val message: String
)
