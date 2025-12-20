package com.dutra.agente.analytics

import javax.inject.Inject
import timber.log.Timber

data class Event(
    val type: String,
    val data: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis()
)

class EventTracker @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {
    companion object {
        private const val TAG = "EventTracker"
        const val EVENT_CHAT_MESSAGE_SENT = "chat_message_sent"
        const val EVENT_CHAT_MESSAGE_RECEIVED = "chat_message_received"
        const val EVENT_USER_PROFILE_UPDATED = "user_profile_updated"
        const val EVENT_SETTINGS_CHANGED = "settings_changed"
        const val EVENT_APP_OPENED = "app_opened"
        const val EVENT_APP_CLOSED = "app_closed"
        const val EVENT_FEATURE_ACCESSED = "feature_accessed"
    }

    private val trackedEvents = mutableListOf<Event>()

    /**
     * Rastreia evento de mensagem de chat enviada
     */
    fun trackChatMessageSent(messageId: String, length: Int) {
        track(
            EVENT_CHAT_MESSAGE_SENT,
            mapOf(
                "message_id" to messageId,
                "length" to length
            )
        )
    }

    /**
     * Rastreia evento de mensagem de chat recebida
     */
    fun trackChatMessageReceived(messageId: String, fromAgent: Boolean) {
        track(
            EVENT_CHAT_MESSAGE_RECEIVED,
            mapOf(
                "message_id" to messageId,
                "from_agent" to fromAgent
            )
        )
    }

    /**
     * Rastreia atualização de perfil
     */
    fun trackUserProfileUpdated(userId: String, fieldsUpdated: List<String>) {
        track(
            EVENT_USER_PROFILE_UPDATED,
            mapOf(
                "user_id" to userId,
                "fields_updated" to fieldsUpdated.size
            )
        )
    }

    /**
     * Rastreia mudanças de configuração
     */
    fun trackSettingsChanged(settingName: String, newValue: Any) {
        track(
            EVENT_SETTINGS_CHANGED,
            mapOf(
                "setting_name" to settingName,
                "new_value" to newValue.toString()
            )
        )
    }

    /**
     * Rastreia abertura do app
     */
    fun trackAppOpened(sessionId: String) {
        track(
            EVENT_APP_OPENED,
            mapOf("session_id" to sessionId)
        )
    }

    /**
     * Rastreia fechamento do app
     */
    fun trackAppClosed(sessionDuration: Long) {
        track(
            EVENT_APP_CLOSED,
            mapOf("session_duration_ms" to sessionDuration)
        )
    }

    /**
     * Rastreia acesso a funcionalidade
     */
    fun trackFeatureAccessed(featureName: String) {
        track(
            EVENT_FEATURE_ACCESSED,
            mapOf("feature_name" to featureName)
        )
    }

    /**
     * Método genérico para rastrear eventos
     */
    private fun track(eventType: String, data: Map<String, Any>) {
        try {
            val event = Event(
                type = eventType,
                data = data
            )
            trackedEvents.add(event)
            analyticsManager.trackEvent(eventType, data)
            Timber.d("$TAG: Event tracked - $eventType")
        } catch (e: Exception) {
            Timber.e(e, "Erro ao rastrear evento: $eventType")
        }
    }

    /**
     * Retorna todos os eventos rastreados
     */
    fun getAllTrackedEvents(): List<Event> = trackedEvents.toList()

    /**
     * Limpa eventos rastreados
     */
    fun clearTrackedEvents() {
        trackedEvents.clear()
        Timber.d("$TAG: Tracked events cleared")
    }

    /**
     * Retorna quantidade de eventos
     */
    fun getTrackedEventCount(): Int = trackedEvents.size
}
