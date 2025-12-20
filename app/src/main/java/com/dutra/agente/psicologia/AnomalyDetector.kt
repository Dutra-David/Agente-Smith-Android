package com.dutra.agente.psicologia

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt
import kotlin.math.abs

/**
 * Data class representing an anomaly detected
 */
data class AnomalyDetected(
    val anomalyId: String,
    val anomalyType: String, // EMOTIONAL_SPIKE, ENGAGEMENT_DROP, RESPONSE_TIME_SPIKE, BEHAVIOR_CHANGE
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val value: Double,
    val expectedRange: Pair<Double, Double>,
    val deviationPercentage: Double,
    val timestamp: String,
    val affectedMetric: String,
    val recommendation: String
)

/**
 * Result from anomaly detection
 */
data class AnomalyDetectionResult(
    val userId: String,
    val anomaliesDetected: List<AnomalyDetected>,
    val hasAnomalies: Boolean,
    val anomalyCount: Int,
    val criticalCount: Int,
    val analysisTimestamp: String
)

/**
 * AnomalyDetector - Detects unusual behavioral patterns and emotional anomalies
 * Uses statistical methods (standard deviation, IQR) for anomaly detection
 */
class AnomalyDetector {

    companion object {
        private const val STANDARD_DEVIATION_THRESHOLD = 2.0 // 2 sigma for anomaly
        private const val IQR_MULTIPLIER = 1.5
    }

    /**
     * Detects anomalies in user behavior
     */
    suspend fun detectAnomalies(
        userId: String,
        emotionHistory: List<Int>,
        engagementHistory: List<Double>,
        responseTimeHistory: List<Long>,
        recentEmotionalValue: Int,
        recentEngagementValue: Double,
        recentResponseTime: Long
    ): AnomalyDetectionResult = withContext(Dispatchers.Default) {
        val anomalies = mutableListOf<AnomalyDetected>()

        // Check emotional anomalies
        emotionHistory.lastOrNull()?.let { lastEmotion ->
            detectEmotionalAnomaly(recentEmotionalValue, emotionHistory)?.let { anomalies.add(it) }
        }

        // Check engagement anomalies
        engagementHistory.lastOrNull()?.let {
            detectEngagementAnomaly(recentEngagementValue, engagementHistory)?.let { anomalies.add(it) }
        }

        // Check response time anomalies
        responseTimeHistory.lastOrNull()?.let {
            detectResponseTimeAnomaly(recentResponseTime, responseTimeHistory)?.let { anomalies.add(it) }
        }

        AnomalyDetectionResult(
            userId = userId,
            anomaliesDetected = anomalies,
            hasAnomalies = anomalies.isNotEmpty(),
            anomalyCount = anomalies.size,
            criticalCount = anomalies.count { it.severity == "CRITICAL" },
            analysisTimestamp = System.currentTimeMillis().toString()
        )
    }

    /**
     * Detects emotional anomalies using statistical analysis
     */
    private fun detectEmotionalAnomaly(
        currentValue: Int,
        history: List<Int>
    ): AnomalyDetected? {
        if (history.size < 3) return null

        val mean = history.average()
        val stdDev = calculateStandardDeviation(history.map { it.toDouble() })
        val upperBound = mean + (STANDARD_DEVIATION_THRESHOLD * stdDev)
        val lowerBound = mean - (STANDARD_DEVIATION_THRESHOLD * stdDev)

        return if (currentValue > upperBound || currentValue < lowerBound) {
            val deviationPercentage = ((currentValue - mean) / mean) * 100
            val severity = calculateSeverity(abs(deviationPercentage))

            AnomalyDetected(
                anomalyId = "emotional_anomaly_${System.currentTimeMillis()}",
                anomalyType = "EMOTIONAL_SPIKE",
                severity = severity,
                value = currentValue.toDouble(),
                expectedRange = lowerBound to upperBound,
                deviationPercentage = abs(deviationPercentage),
                timestamp = System.currentTimeMillis().toString(),
                affectedMetric = "Emotional State",
                recommendation = generateEmotionalRecommendation(currentValue, mean.toInt())
            )
        } else {
            null
        }
    }

    /**
     * Detects engagement anomalies
     */
    private fun detectEngagementAnomaly(
        currentValue: Double,
        history: List<Double>
    ): AnomalyDetected? {
        if (history.size < 3) return null

        val mean = history.average()
        val stdDev = calculateStandardDeviation(history)
        val upperBound = mean + (STANDARD_DEVIATION_THRESHOLD * stdDev)
        val lowerBound = maxOf(0.0, mean - (STANDARD_DEVIATION_THRESHOLD * stdDev))

        return if (currentValue > upperBound || currentValue < lowerBound) {
            val deviationPercentage = ((currentValue - mean) / mean) * 100
            val severity = calculateSeverity(abs(deviationPercentage))

            AnomalyDetected(
                anomalyId = "engagement_anomaly_${System.currentTimeMillis()}",
                anomalyType = "ENGAGEMENT_DROP",
                severity = severity,
                value = currentValue,
                expectedRange = lowerBound to upperBound,
                deviationPercentage = abs(deviationPercentage),
                timestamp = System.currentTimeMillis().toString(),
                affectedMetric = "User Engagement",
                recommendation = generateEngagementRecommendation(currentValue, mean)
            )
        } else {
            null
        }
    }

    /**
     * Detects response time anomalies
     */
    private fun detectResponseTimeAnomaly(
        currentValue: Long,
        history: List<Long>
    ): AnomalyDetected? {
        if (history.size < 3) return null

        val mean = history.average()
        val stdDev = calculateStandardDeviation(history.map { it.toDouble() })
        val upperBound = mean + (STANDARD_DEVIATION_THRESHOLD * stdDev)
        val lowerBound = maxOf(0.0, mean - (STANDARD_DEVIATION_THRESHOLD * stdDev))

        return if (currentValue > upperBound || currentValue < lowerBound) {
            val deviationPercentage = ((currentValue - mean) / mean) * 100
            val severity = calculateSeverity(abs(deviationPercentage))

            AnomalyDetected(
                anomalyId = "response_time_anomaly_${System.currentTimeMillis()}",
                anomalyType = "RESPONSE_TIME_SPIKE",
                severity = severity,
                value = currentValue.toDouble(),
                expectedRange = lowerBound to upperBound,
                deviationPercentage = abs(deviationPercentage),
                timestamp = System.currentTimeMillis().toString(),
                affectedMetric = "Response Time (ms)",
                recommendation = if (currentValue > mean) "Response time is slower than usual. Consider checking system performance." else "Response time is faster than expected."
            )
        } else {
            null
        }
    }

    /**
     * Batch detect anomalies for multiple metrics
     */
    suspend fun detectAnomaliesBatch(
        userId: String,
        emotionHistory: List<Int>,
        engagementHistory: List<Double>,
        responseTimeHistory: List<Long>
    ): AnomalyDetectionResult = withContext(Dispatchers.Default) {
        val anomalies = mutableListOf<AnomalyDetected>()

        // Get recent values
        val recentEmotion = emotionHistory.lastOrNull() ?: 5
        val recentEngagement = engagementHistory.lastOrNull() ?: 0.5
        val recentResponseTime = responseTimeHistory.lastOrNull() ?: 1000

        detectAnomalies(
            userId,
            emotionHistory,
            engagementHistory,
            responseTimeHistory,
            recentEmotion,
            recentEngagement,
            recentResponseTime
        )
    }

    /**
     * Helper: Calculate standard deviation
     */
    private fun calculateStandardDeviation(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return sqrt(variance)
    }

    /**
     * Helper: Calculate severity level based on deviation percentage
     */
    private fun calculateSeverity(deviationPercentage: Double): String {
        return when {
            deviationPercentage > 50 -> "CRITICAL"
            deviationPercentage > 30 -> "HIGH"
            deviationPercentage > 15 -> "MEDIUM"
            else -> "LOW"
        }
    }

    /**
     * Helper: Generate recommendation for emotional anomaly
     */
    private fun generateEmotionalRecommendation(currentValue: Int, averageValue: Int): String {
        return when {
            currentValue > averageValue + 2 -> "User shows elevated emotional state. Consider offering support or calming activities."
            currentValue < averageValue - 2 -> "User shows low emotional state. Recommend positive engagement or motivational content."
            else -> "Emotional state anomaly detected. Monitor user wellbeing."
        }
    }

    /**
     * Helper: Generate recommendation for engagement anomaly
     */
    private fun generateEngagementRecommendation(currentValue: Double, averageValue: Double): String {
        return when {
            currentValue < averageValue * 0.5 -> "Engagement has dropped significantly. Consider re-engaging the user with fresh content."
            currentValue > averageValue * 1.5 -> "User shows unusually high engagement. Great opportunity to capitalize on interest."
            else -> "Engagement anomaly detected. Review content relevance."
        }
    }

    /**
     * Checks if anomaly requires immediate intervention
     */
    fun requiresIntervention(anomaly: AnomalyDetected): Boolean {
        return anomaly.severity in listOf("CRITICAL", "HIGH")
    }
}
