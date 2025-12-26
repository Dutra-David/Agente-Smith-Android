package com.smith.anomaly

import kotlinx.coroutines.*
import kotlin.math.abs

/**
 * DETECCAO DE ANOMALIAS COMPORTAMENTAIS - Smith Phase 5.3
 * Identifica quando usuario esta agindo diferente de seu padrao normal
 */

data class AnomalyAlert(
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val description: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserBehaviorBaseline(
    val averageTradeFrequency: Float,
    val averageTradeSize: Float,
    val averageRiskLevel: Float,
    val usualTradingHours: String,
    val typicalEmotionalState: String,
    val averageStressLevel: Float
)

class BehavioralAnomalyDetection {
    
    private var userBaseline: UserBehaviorBaseline? = null
    
    fun establishBaseline(
        tradesPerDay: Float,
        avgTradeSize: Float,
        riskLevel: Float,
        tradingHours: String,
        emotionalState: String,
        stressLevel: Float
    ) {
        userBaseline = UserBehaviorBaseline(
            averageTradeFrequency = tradesPerDay,
            averageTradeSize = avgTradeSize,
            averageRiskLevel = riskLevel,
            usualTradingHours = tradingHours,
            typicalEmotionalState = emotionalState,
            averageStressLevel = stressLevel
        )
    }
    
    suspend fun detectAnomalies(
        currentTradeFrequency: Float,
        currentTradeSize: Float,
        currentRiskLevel: Float,
        currentHour: Int,
        currentEmotionalState: String,
        currentStressLevel: Float
    ): List<AnomalyAlert> = withContext(Dispatchers.Default) {
        
        val alerts = mutableListOf<AnomalyAlert>()
        val baseline = userBaseline ?: return@withContext alerts
        
        // Deteccao de frequencia anormal
        if (isFrequencyAnomaly(currentTradeFrequency, baseline.averageTradeFrequency)) {
            alerts.add(AnomalyAlert(
                severity = "MEDIUM",
                description = "Voce esta tradando muito mais frequentemente que o normal!",
                action = "PAUSE e pense racionalmente"
            ))
        }
        
        // Deteccao de tamanho anormal de trade
        if (isTradeSize Anomaly(currentTradeSize, baseline.averageTradeSize)) {
            alerts.add(AnomalyAlert(
                severity = "HIGH",
                description = "Voce esta tentando investir muito mais que o habitual!",
                action = "REVISE sua decisao"
            ))
        }
        
        // Deteccao de risco anormal
        if (isRiskAnomaly(currentRiskLevel, baseline.averageRiskLevel, currentStressLevel)) {
            alerts.add(AnomalyAlert(
                severity = "CRITICAL",
                description = "Voce esta tomando risco MUITO maior que o normal, principalmente enquanto estressado!",
                action = "CANCELE imediatamente"
            ))
        }
        
        // Deteccao de mudanca emocional radical
        if (isEmotionalShift(currentEmotionalState, baseline.typicalEmotionalState)) {
            alerts.add(AnomalyAlert(
                severity = "HIGH",
                description = "Sua emocao mudou drasticamente. Voce nao esta como normalmente.",
                action = "RESPIRE e espere voltar ao normal"
            ))
        }
        
        // Deteccao de stress critico
        if (currentStressLevel > 80f && currentStressLevel > baseline.averageStressLevel + 40f) {
            alerts.add(AnomalyAlert(
                severity = "CRITICAL",
                description = "Seu stress esta CRITICO. Nao deveria estar tomando decisoes financeiras agora!",
                action = "PAUSE TUDO"
            ))
        }
        
        // Deteccao de timing anormal
        if (isUnusualTradingTime(currentHour)) {
            alerts.add(AnomalyAlert(
                severity = "LOW",
                description = "Voce esta tradando em horario incomum para voce.",
                action = "Apenas notifique-se"
            ))
        }
        
        alerts
    }
    
    private fun isFrequencyAnomaly(current: Float, baseline: Float): Boolean {
        return current > baseline * 1.5f
    }
    
    private fun isTradeSizeAnomaly(current: Float, baseline: Float): Boolean {
        return current > baseline * 2f
    }
    
    private fun isRiskAnomaly(current: Float, baseline: Float, stress: Float): Boolean {
        return (current > baseline * 1.5f && stress > 60f) || (current > baseline * 2f)
    }
    
    private fun isEmotionalShift(current: String, baseline: String): Boolean {
        return current != baseline
    }
    
    private fun isUnusualTradingTime(hour: Int): Boolean {
        return hour < 9 || hour > 18 // Fora das horas normais 9-18
    }
}
