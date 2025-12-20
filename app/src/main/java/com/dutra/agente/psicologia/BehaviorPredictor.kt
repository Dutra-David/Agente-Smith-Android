package com.dutra.agente.psicologia

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data class representing predicted user behavior
 */
data class BehaviorPrediction(
    val predictionId: String,
    val userId: String,
    val behaviorType: String, // ENGAGEMENT_LEVEL, EMOTIONAL_STATE, CHURN_RISK, FEATURE_INTEREST
    val predictedValue: Double,
    val confidence: Double,
    val timeToHorizon: String,
    val recommendation: String,
    val predictorModel: String,
    val timestamp: String
)

/**
 * Result from behavior prediction
 */
data class BehaviorPredictionResult(
    val userId: String,
    val predictions: List<BehaviorPrediction>,
    val overallChurnRisk: Double,
    val nextLikelyAction: String,
    val predictedEmotionalTrend: String,
    val analysisTimestamp: String
)

/**
 * BehaviorPredictor - Predicts future user behavior based on historical patterns
 * Uses simple statistical models and trend extrapolation
 */
class BehaviorPredictor {

    /**
     * Predicts next user behavior based on historical data
     */
    suspend fun predictBehavior(
        userId: String,
        emotionHistory: List<Int>,
        engagementHistory: List<Double>,
        interactionFrequency: Int,
        daysSinceLastInteraction: Int
    ): BehaviorPredictionResult = withContext(Dispatchers.Default) {
        val predictions = mutableListOf<BehaviorPrediction>()

        // Predict engagement level
        predictions.add(predictEngagementLevel(userId, engagementHistory))

        // Predict emotional state
        predictions.add(predictEmotionalState(userId, emotionHistory))

        // Predict churn risk
        predictions.add(predictChurnRisk(userId, interactionFrequency, daysSinceLastInteraction))

        // Calculate overall churn risk
        val churnRiskPrediction = predictions.find { it.behaviorType == "CHURN_RISK" }
        val overallChurnRisk = churnRiskPrediction?.predictedValue ?: 0.1

        // Predict next action
        val nextLikelyAction = predictNextAction(emotionHistory, engagementHistory)

        // Predict emotional trend
        val emotionalTrend = predictEmotionalTrend(emotionHistory)

        BehaviorPredictionResult(
            userId = userId,
            predictions = predictions,
            overallChurnRisk = overallChurnRisk,
            nextLikelyAction = nextLikelyAction,
            predictedEmotionalTrend = emotionalTrend,
            analysisTimestamp = System.currentTimeMillis().toString()
        )
    }

    /**
     * Predicts next engagement level using trend extrapolation
     */
    private fun predictEngagementLevel(
        userId: String,
        engagementHistory: List<Double>
    ): BehaviorPrediction {
        if (engagementHistory.isEmpty()) {
            return BehaviorPrediction(
                predictionId = "engagement_${System.currentTimeMillis()}",
                userId = userId,
                behaviorType = "ENGAGEMENT_LEVEL",
                predictedValue = 0.5,
                confidence = 0.3,
                timeToHorizon = "7 days",
                recommendation = "Insufficient data for engagement prediction.",
                predictorModel = "Linear Extrapolation",
                timestamp = System.currentTimeMillis().toString()
            )
        }

        val recentEngagement = engagementHistory.takeLast(10)
        val trend = calculateTrend(recentEngagement.map { it.toDouble() })
        val currentAverage = recentEngagement.average()
        val predictedEngagement = (currentAverage + trend).coerceIn(0.0, 1.0)
        val confidence = calculateConfidence(engagementHistory.size)

        return BehaviorPrediction(
            predictionId = "engagement_${System.currentTimeMillis()}",
            userId = userId,
            behaviorType = "ENGAGEMENT_LEVEL",
            predictedValue = predictedEngagement,
            confidence = confidence,
            timeToHorizon = "7 days",
            recommendation = getEngagementRecommendation(predictedEngagement),
            predictorModel = "Linear Extrapolation",
            timestamp = System.currentTimeMillis().toString()
        )
    }

    /**
     * Predicts emotional state trend
     */
    private fun predictEmotionalState(
        userId: String,
        emotionHistory: List<Int>
    ): BehaviorPrediction {
        if (emotionHistory.isEmpty()) {
            return BehaviorPrediction(
                predictionId = "emotion_${System.currentTimeMillis()}",
                userId = userId,
                behaviorType = "EMOTIONAL_STATE",
                predictedValue = 5.0,
                confidence = 0.3,
                timeToHorizon = "3 days",
                recommendation = "Insufficient data for emotional prediction.",
                predictorModel = "Moving Average",
                timestamp = System.currentTimeMillis().toString()
            )
        }

        val recentEmotion = emotionHistory.takeLast(10).map { it.toDouble() }
        val trend = calculateTrend(recentEmotion)
        val currentAverage = recentEmotion.average()
        val predictedEmotion = (currentAverage + trend).coerceIn(1.0, 10.0)
        val confidence = calculateConfidence(emotionHistory.size)

        return BehaviorPrediction(
            predictionId = "emotion_${System.currentTimeMillis()}",
            userId = userId,
            behaviorType = "EMOTIONAL_STATE",
            predictedValue = predictedEmotion,
            confidence = confidence,
            timeToHorizon = "3 days",
            recommendation = getEmotionalRecommendation(predictedEmotion),
            predictorModel = "Moving Average",
            timestamp = System.currentTimeMillis().toString()
        )
    }

    /**
     * Predicts churn risk (likelihood of user disengagement)
     */
    private fun predictChurnRisk(
        userId: String,
        interactionFrequency: Int,
        daysSinceLastInteraction: Int
    ): BehaviorPrediction {
        // Simple churn risk model
        var churnRisk = 0.0
        var confidence = 0.7

        // Factor 1: Days since last interaction (higher = more risk)
        if (daysSinceLastInteraction > 30) churnRisk += 0.3
        else if (daysSinceLastInteraction > 14) churnRisk += 0.2
        else if (daysSinceLastInteraction > 7) churnRisk += 0.1

        // Factor 2: Interaction frequency (lower = more risk)
        if (interactionFrequency < 2) churnRisk += 0.3
        else if (interactionFrequency < 5) churnRisk += 0.2
        else if (interactionFrequency < 10) churnRisk += 0.1

        churnRisk = minOf(1.0, churnRisk)

        return BehaviorPrediction(
            predictionId = "churn_${System.currentTimeMillis()}",
            userId = userId,
            behaviorType = "CHURN_RISK",
            predictedValue = churnRisk,
            confidence = confidence,
            timeToHorizon = "30 days",
            recommendation = getChurnRecommendation(churnRisk),
            predictorModel = "Engagement Score Model",
            timestamp = System.currentTimeMillis().toString()
        )
    }

    /**
     * Predicts user's next likely action based on patterns
     */
    private fun predictNextAction(
        emotionHistory: List<Int>,
        engagementHistory: List<Double>
    ): String {
        if (emotionHistory.isEmpty() || engagementHistory.isEmpty()) {
            return "User profile insufficient for action prediction"
        }

        val recentEmotion = emotionHistory.lastOrNull() ?: 5
        val recentEngagement = engagementHistory.lastOrNull() ?: 0.5

        return when {
            recentEmotion > 7 && recentEngagement > 0.7 -> "Likely to explore premium features or provide feedback"
            recentEmotion > 5 && recentEngagement > 0.5 -> "Expected to continue regular interaction"
            recentEmotion < 3 && recentEngagement < 0.3 -> "High risk of disengagement - intervention needed"
            recentEmotion < 5 && recentEngagement < 0.5 -> "Likely to reduce frequency of interactions"
            else -> "Maintaining current engagement pattern"
        }
    }

    /**
     * Predicts emotional trend
     */
    private fun predictEmotionalTrend(emotionHistory: List<Int>): String {
        if (emotionHistory.size < 2) return "INSUFFICIENT_DATA"

        val recentEmotion = emotionHistory.takeLast(10).map { it.toDouble() }
        val trend = calculateTrend(recentEmotion)

        return when {
            trend > 0.2 -> "IMPROVING"
            trend < -0.2 -> "DECLINING"
            else -> "STABLE"
        }
    }

    /**
     * Helper: Calculate trend using linear regression
     */
    private fun calculateTrend(values: List<Double>): Double {
        if (values.size < 2) return 0.0

        val n = values.size
        val sumX = (0 until n).sum()
        val sumY = values.sum()
        val sumXY = (0 until n).map { it * values[it] }.sum()
        val sumX2 = (0 until n).map { it * it }.sum()

        val slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        return slope / values.average() // Normalize slope
    }

    /**
     * Helper: Calculate prediction confidence based on data size
     */
    private fun calculateConfidence(dataSize: Int): Double {
        return minOf(0.95, 0.4 + (dataSize / 100.0) * 0.55)
    }

    /**
     * Helper: Get engagement recommendation
     */
    private fun getEngagementRecommendation(engagement: Double): String {
        return when {
            engagement > 0.8 -> "User is highly engaged. Capitalize with premium offers."
            engagement > 0.5 -> "User engagement is healthy. Continue current strategy."
            engagement > 0.2 -> "User engagement declining. Offer re-engagement content."
            else -> "Critical: User highly disengaged. Intervention required."
        }
    }

    /**
     * Helper: Get emotional recommendation
     */
    private fun getEmotionalRecommendation(emotion: Double): String {
        return when {
            emotion > 8 -> "User is highly satisfied. Opportunity for upselling."
            emotion > 6 -> "User is satisfied. Maintain current experience."
            emotion > 4 -> "User emotion is neutral. Try engaging activities."
            else -> "User is dissatisfied. Offer support and improvements."
        }
    }

    /**
     * Helper: Get churn risk recommendation
     */
    private fun getChurnRecommendation(churnRisk: Double): String {
        return when {
            churnRisk > 0.7 -> "CRITICAL: High churn risk. Immediate personalized intervention recommended."
            churnRisk > 0.4 -> "WARNING: Moderate churn risk. Increase engagement touchpoints."
            churnRisk > 0.2 -> "Preventive: Low-moderate churn risk. Monitor closely."
            else -> "Low churn risk. User is stable."
        }
    }
}
