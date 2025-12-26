package com.smith.recommendations

import kotlinx.coroutines.*

/**
 * MOTOR DE RECOMENDACOES PERSONALIZADO - Smith Phase 5.2
 * Ajusta recomendacoes de investimento baseado em estado emocional do usuario
 */

data class PersonalizedRecommendation(
    val asset: String,
    val action: String, // BUY, HOLD, SELL
    val confidence: Float,
    val riskLevel: String, // LOW, MEDIUM, HIGH
    val reason: String,
    val emotionalAdjustment: String,
    val timestamp: Long = System.currentTimeMillis()
)

class PersonalizedRecommendationEngine {
    
    suspend fun generateRecommendation(
        asset: String,
        marketSentiment: Float,
        userRiskProfile: String,
        userStressLevel: Float,
        userEmotionalState: String,
        userHistoricalAccuracy: Float
    ): PersonalizedRecommendation = withContext(Dispatchers.Default) {
        
        val baseRecommendation = determineBaseRecommendation(asset, marketSentiment)
        val riskAdjustment = adjustForRiskProfile(userRiskProfile, userStressLevel)
        val emotionalAdjustment = adjustForEmotionalState(userEmotionalState, userStressLevel)
        val finalConfidence = calculateConfidence(marketSentiment, userHistoricalAccuracy)
        
        PersonalizedRecommendation(
            asset = asset,
            action = applyEmotionalFilter(baseRecommendation, emotionalAdjustment),
            confidence = finalConfidence,
            riskLevel = riskAdjustment,
            reason = generateExplanation(baseRecommendation, userStressLevel),
            emotionalAdjustment = emotionalAdjustment
        )
    }
    
    private fun determineBaseRecommendation(asset: String, marketSentiment: Float): String = when {
        marketSentiment > 0.7f -> "BUY"
        marketSentiment < 0.3f -> "SELL"
        else -> "HOLD"
    }
    
    private fun adjustForRiskProfile(riskProfile: String, stressLevel: Float): String = when {
        stressLevel > 75f -> "LOW"
        riskProfile == "AGGRESSIVE" && stressLevel < 50f -> "HIGH"
        riskProfile == "MODERATE" -> "MEDIUM"
        else -> "LOW"
    }
    
    private fun adjustForEmotionalState(emotionalState: String, stressLevel: Float): String = when {
        emotionalState == "FEAR" && stressLevel > 70f -> "PROTECTIVE"
        emotionalState == "GREED" && stressLevel > 60f -> "CAUTIOUS"
        emotionalState == "CALM" -> "RATIONAL"
        else -> "NEUTRAL"
    }
    
    private fun applyEmotionalFilter(baseAction: String, emotionalAdjustment: String): String = when {
        emotionalAdjustment == "PROTECTIVE" && baseAction == "BUY" -> "HOLD"
        emotionalAdjustment == "CAUTIOUS" && baseAction == "BUY" -> "HOLD"
        emotionalAdjustment == "PROTECTIVE" && baseAction == "SELL" -> "HOLD"
        else -> baseAction
    }
    
    private fun calculateConfidence(marketSentiment: Float, userAccuracy: Float): Float {
        val sentimentConfidence = kotlin.math.abs(marketSentiment - 0.5f) * 2f
        return (sentimentConfidence * 0.7f + userAccuracy * 0.3f) * 100f
    }
    
    private fun generateExplanation(action: String, stressLevel: Float): String = when {
        stressLevel > 75f -> "Sua emocao esta alterando a recomendacao. Espere ficar mais calmo para decidir."
        action == "BUY" -> "Sinais de mercado sugerem oportunidade de entrada."
        action == "SELL" -> "Recomendo sair agora, mercado tem sinais de fraqueza."
        else -> "Manter a posicao e monitorar."
    }
    
    // ==================== SISTEMA DE PROTETION ====================
    
    fun shouldWarnAboutDecision(stressLevel: Float, lastDecisionTime: Long): Boolean {
        val timeSinceLastDecision = System.currentTimeMillis() - lastDecisionTime
        return stressLevel > 70f && timeSinceLastDecision < 60000 // 1 minuto
    }
    
    fun generateRiskAdjustment(currentRisk: String, stressLevel: Float): String = when {
        stressLevel > 80f -> "LOW (STRESS CRITICO)"
        stressLevel > 70f -> when(currentRisk) {
            "HIGH" -> "MEDIUM (STRESS ALTO)"
            "MEDIUM" -> "LOW (STRESS ALTO)"
            else -> "LOW (JA BAIXO)"
        }
        else -> currentRisk
    }
}
