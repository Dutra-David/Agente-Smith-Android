package com.dutra.agente.domain.usecases

import com.dutra.agente.domain.models.FishingSession
import com.dutra.agente.domain.models.EnvironmentalCondition
import com.dutra.agente.domain.repositories.FishingRepository
import com.dutra.agente.domain.repositories.PredictionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Use Case: Prever melhor janela de pesca nos próximos N horas
 * Entrada: Condições atuais + histórico do usuário
 * Saída: Lista de horários ótimos com pontuação
 */
data class PredictedFishingWindow(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val confidenceScore: Double, // 0-100
    val primaryFactor: String, // "Maré", "Lua", "Vento", etc
    val reasonForPrediction: String,
    val suggestedLocation: String? = null
)

class PredictFishingWindowUseCase(
    private val fishingRepository: FishingRepository,
    private val predictionRepository: PredictionRepository
) {
    
    /**
     * Analisa histórico do usuário + condições atuais
     * Usa ML para prever melhores horários
     */
    suspend fun execute(
        hoursAhead: Int = 24,
        userLocationLat: Double,
        userLocationLon: Double
    ): List<PredictedFishingWindow> {
        // 1. Buscar histórico de pesca do usuário
        val userHistory = fishingRepository.getUserFishingHistory(limit = 100)
        
        // 2. Analisar padrões: quando teve mais sucesso?
        val userPattern = analyzeUserPattern(userHistory)
        
        // 3. Prever condições ambientais para as próximas 24h
        val predictions = predictionRepository.predictWeatherPattern(
            latitude = userLocationLat,
            longitude = userLocationLon,
            hoursAhead = hoursAhead
        )
        
        // 4. Combinar: Padrões do usuário + Predições
        val optimalWindows = mutableListOf<PredictedFishingWindow>()
        
        predictions.forEach { prediction ->
            val matchScore = calculateMatchScore(
                prediction.conditions,
                userPattern
            )
            
            if (matchScore > 60) { // Threshold: 60%
                optimalWindows.add(
                    PredictedFishingWindow(
                        startTime = prediction.startTime,
                        endTime = prediction.endTime,
                        confidenceScore = matchScore,
                        primaryFactor = prediction.dominantFactor,
                        reasonForPrediction = generateReason(
                            prediction,
                            userPattern,
                            matchScore
                        )
                    )
                )
            }
        }
        
        return optimalWindows.sortedByDescending { it.confidenceScore }
    }
    
    /**
     * Analisa: Quando THIS USER teve mais sucesso?
     * Exemplo: "Dutra sempre pesca melhor quando:
     * - Lua nova
     * - Maré subindo
     * - Vento de Sudoeste
     * - Entre 6-8 da manhã"
     */
    private suspend fun analyzeUserPattern(
        history: List<FishingSession>
    ): UserFishingPattern {
        val successfulSessions = history.filter { it.catchCount > 0 }
        
        return UserFishingPattern(
            averageSuccessRate = (successfulSessions.size.toDouble() / history.size * 100),
            preferredMoonPhases = successfulSessions
                .groupingBy { it.moonPhase }
                .eachCount()
                .maxByOrNull { it.value }?.key,
            preferredWindDirection = successfulSessions
                .groupingBy { it.windDirection }
                .eachCount()
                .maxByOrNull { it.value }?.key,
            preferredTideTypes = successfulSessions
                .groupingBy { it.tideType }
                .eachCount(),
            preferredHours = successfulSessions
                .map { it.startTime.hour }
                .groupingBy { it }
                .eachCount(),
            favoriteLocations = successfulSessions
                .map { it.location }
                .distinct()
        )
    }
    
    /**
     * Calcula quanto a predição combina com padrão do usuário
     */
    private fun calculateMatchScore(
        conditions: EnvironmentalCondition,
        userPattern: UserFishingPattern
    ): Double {
        var score = 50.0 // Score base
        
        // Lua
        if (conditions.moonPhase == userPattern.preferredMoonPhases) score += 15
        
        // Vento
        if (conditions.windDirection == userPattern.preferredWindDirection) score += 15
        
        // Maré
        if (userPattern.preferredTideTypes.containsKey(conditions.tideType)) {
            score += 10
        }
        
        // Horário
        if (conditions.hour in userPattern.preferredHours.keys) score += 10
        
        return score.coerceIn(0.0, 100.0)
    }
    
    private fun generateReason(
        prediction: WeatherPrediction,
        pattern: UserFishingPattern,
        score: Double
    ): String {
        val factors = mutableListOf<String>()
        
        if (prediction.dominantFactor == "Lua") {
            factors.add("Lua em ${prediction.conditions.moonPhase}")
        }
        if (prediction.conditions.windDirection == pattern.preferredWindDirection) {
            factors.add("Vento do seu lado")
        }
        
        return "Excelentes condições: ${factors.joinToString(", ")}. Sua taxa de sucesso deve ser ~${score.toInt()}%"
    }
}

data class UserFishingPattern(
    val averageSuccessRate: Double,
    val preferredMoonPhases: String?,
    val preferredWindDirection: String?,
    val preferredTideTypes: Map<String, Int>,
    val preferredHours: Map<Int, Int>,
    val favoriteLocations: List<String>
)

data class WeatherPrediction(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val conditions: EnvironmentalCondition,
    val dominantFactor: String,
    val hour: Int
)
