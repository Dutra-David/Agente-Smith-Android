package com.dutra.agente.analytics

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

data class AnalyticsEvent(
    val eventName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val properties: Map<String, Any> = emptyMap()
)

data class UserEngagementMetrics(
    val userId: String,
    val sessionDuration: Long,
    val interactionCount: Int,
    val featuresUsed: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

@Singleton
class AnalyticsManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "AnalyticsManager"
    }

    private val events = mutableListOf<AnalyticsEvent>()
    private val maxEventsInMemory = 1000

    /**
     * Registra um evento de analytics
     */
    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap()) {
        try {
            val event = AnalyticsEvent(
                eventName = eventName,
                properties = properties
            )
            events.add(event)

            if (events.size > maxEventsInMemory) {
                events.removeAt(0) // Remove evento mais antigo
            }

            Timber.d("$TAG: Event tracked - $eventName")
        } catch (e: Exception) {
            Timber.e(e, "Erro ao registrar evento: $eventName")
        }
    }

    /**
     * Rastreia engajamento do usuário
     */
    fun trackEngagement(
        userId: String,
        sessionDuration: Long,
        interactionCount: Int,
        featuresUsed: List<String>
    ) {
        val metrics = UserEngagementMetrics(
            userId = userId,
            sessionDuration = sessionDuration,
            interactionCount = interactionCount,
            featuresUsed = featuresUsed
        )

        trackEvent(
            "user_engagement",
            mapOf(
                "user_id" to userId,
                "session_duration" to sessionDuration,
                "interaction_count" to interactionCount,
                "features_used" to featuresUsed.size
            )
        )

        Timber.d("$TAG: Engagement tracked for $userId - ${interactionCount} interactions")
    }

    /**
     * Rastreia uso de funcionalidade
     */
    fun trackFeatureUsage(featureName: String, metadata: Map<String, Any> = emptyMap()) {
        trackEvent(
            "feature_usage",
            (mapOf("feature_name" to featureName) + metadata)
        )
    }

    /**
     * Rastreia erro da aplicação
     */
    fun trackError(errorName: String, errorMessage: String, stacktrace: String = "") {
        trackEvent(
            "app_error",
            mapOf(
                "error_name" to errorName,
                "error_message" to errorMessage,
                "stacktrace" to stacktrace
            )
        )
        Timber.e("$TAG: Error tracked - $errorName: $errorMessage")
    }

    /**
     * Rastreia performance
     */
    fun trackPerformance(
        operationName: String,
        durationMs: Long,
        success: Boolean
    ) {
        trackEvent(
            "performance",
            mapOf(
                "operation" to operationName,
                "duration_ms" to durationMs,
                "success" to success
            )
        )
    }

    /**
     * Retorna todos os eventos rastreados
     */
    fun getAllEvents(): List<AnalyticsEvent> = events.toList()

    /**
     * Limpa eventos da memória
     */
    fun clearEvents() {
        events.clear()
        Timber.d("$TAG: Events cleared")
    }

    /**
     * Retorna quantidade de eventos
     */
    fun getEventCount(): Int = events.size

    /**
     * Envia eventos para o servidor (implementação futura)
     */
    suspend fun flushEvents() {
        try {
            Timber.d("$TAG: Flushing ${events.size} events...")
            // TODO: Implementar envio para servidor/Firebase
            // clearEvents()
        } catch (e: Exception) {
            Timber.e(e, "Erro ao enviar eventos")
        }
    }
}
