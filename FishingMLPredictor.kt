package com.dutra.agente.domain.ml

import com.dutra.agente.domain.models.FishingSession
import com.dutra.agente.domain.models.EnvironmentalCondition
import com.dutra.agente.domain.repositories.FishingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.*

/**
 * MACHINE LEARNING ENGINE - Coração do Sistema Inteligente
 * 
 * Responsabilidades:
 * 1. Analisar padrões históricos do usuário
 * 2. Detectar anomalias nas condições
 * 3. Prever janelas ótimas de pesca
 * 4. Melhorar recomendações a cada sessão
 */

data class AnomalyReport(
    val detected: Boolean,
    val anomalies: List<String>,
    val severity: AnomalySeverity,
    val recommendation: String
)

enum class AnomalySeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class EnvironmentalPattern(
    val factorName: String,
    val value: Double,
    val successRate: Double, // Taxa de sucesso com esse fator
    val frequency: Int, // Quantas vezes esse padrão ocorreu
    val confidence: Double // 0-100, quanto confiante estamos
)

class FishingMLPredictor(
    private val fishingRepository: FishingRepository
) {
    private val _patterns = MutableStateFlow<List<EnvironmentalPattern>>(emptyList())
    val patterns: StateFlow<List<EnvironmentalPattern>> = _patterns
    
    /**
     * Fase 1: Análise de Padrões
     * Encontra as COMBINAÇÕES que resultam em melhor pesca
     */
    suspend fun findWinningPatterns(): List<EnvironmentalPattern> {
        val history = fishingRepository.getUserFishingHistory(limit = 200)
        if (history.isEmpty()) return emptyList()
        
        val patterns = mutableListOf<EnvironmentalPattern>()
        val successfulSessions = history.filter { it.catchCount > 0 }
        
        // Padrão 1: Lua
        successfulSessions
            .groupBy { it.moonPhase }
            .forEach { (moonPhase, sessions) ->
                val successRate = (sessions.size.toDouble() / successfulSessions.size) * 100
                patterns.add(
                    EnvironmentalPattern(
                        factorName = "Lua: $moonPhase",
                        value = successRate,
                        successRate = successRate,
                        frequency = sessions.size,
                        confidence = calculateConfidence(sessions.size)
                    )
                )
            }
        
        // Padrão 2: Maré
        successfulSessions
            .groupBy { it.tideType }
            .forEach { (tideType, sessions) ->
                val successRate = (sessions.size.toDouble() / successfulSessions.size) * 100
                patterns.add(
                    EnvironmentalPattern(
                        factorName = "Maré: $tideType",
                        value = successRate,
                        successRate = successRate,
                        frequency = sessions.size,
                        confidence = calculateConfidence(sessions.size)
                    )
                )
            }
        
        // Padrão 3: Vento
        successfulSessions
            .groupBy { it.windDirection }
            .forEach { (windDir, sessions) ->
                val successRate = (sessions.size.toDouble() / successfulSessions.size) * 100
                patterns.add(
                    EnvironmentalPattern(
                        factorName = "Vento: $windDir",
                        value = successRate,
                        successRate = successRate,
                        frequency = sessions.size,
                        confidence = calculateConfidence(sessions.size)
                    )
                )
            }
        
        // Padrão 4: Horário do Dia
        successfulSessions
            .groupBy { it.startTime.hour }
            .forEach { (hour, sessions) ->
                val successRate = (sessions.size.toDouble() / successfulSessions.size) * 100
                patterns.add(
                    EnvironmentalPattern(
                        factorName = "Hora: ${hour}h",
                        value = hour.toDouble(),
                        successRate = successRate,
                        frequency = sessions.size,
                        confidence = calculateConfidence(sessions.size)
                    )
                )
            }
        
        _patterns.emit(patterns.sortedByDescending { it.confidence })
        return patterns
    }
    
    /**
     * Fase 2: Detecção de Anomalias
     * Identifica quando as condições são ANORMAIS
     */
    suspend fun detectUnusualConditions(current: EnvironmentalCondition): AnomalyReport {
        val history = fishingRepository.getUserFishingHistory(limit = 100)
        val patterns = findWinningPatterns()
        
        val anomalies = mutableListOf<String>()
        var severity = AnomalySeverity.LOW
        
        // Verificar Lua
        val moonPattern = patterns.find { it.factorName.contains("Lua") && it.factorName.contains(current.moonPhase) }
        if (moonPattern == null || moonPattern.confidence < 30) {
            anomalies.add("Lua em ${current.moonPhase} não é típica para seu sucesso")
            severity = AnomalySeverity.MEDIUM
        }
        
        // Verificar Vento
        val windPattern = patterns.find { it.factorName.contains("Vento") && it.factorName.contains(current.windDirection) }
        if (windPattern == null) {
            anomalies.add("Vento ${current.windDirection} é raro nas suas melhores pesca")
            severity = AnomalySeverity.HIGH
        }
        
        // Verificar Maré
        val tidePattern = patterns.find { it.factorName.contains("Maré") && it.factorName.contains(current.tideType) }
        if (tidePattern == null) {
            anomalies.add("Maré ${current.tideType} não combina com seu padrão")
            severity = AnomalySeverity.CRITICAL
        }
        
        val recommendation = when {
            anomalies.isEmpty() -> "Condições perfeitas! Tudo alinhado com seu padrão."
            severity == AnomalySeverity.CRITICAL -> "Espere melhores condições. Risco muito alto."
            severity == AnomalySeverity.HIGH -> "Possível, mas condições não ideais. Baixa probabilidade de sucesso."
            else -> "Condições aceitáveis, mas não ótimas. Taxa de sucesso reduzida."
        }
        
        return AnomalyReport(
            detected = anomalies.isNotEmpty(),
            anomalies = anomalies,
            severity = severity,
            recommendation = recommendation
        )
    }
    
    /**
     * Fase 3: Previsão Inteligente
     * Combina padrões + condições atuais = probabilidade de sucesso
     */
    suspend fun predictSuccessProbability(conditions: EnvironmentalCondition): Double {
        val patterns = findWinningPatterns()
        var probability = 50.0 // Base
        
        // Encontrar padrões que combinam com as condições atuais
        val matchingPatterns = patterns.filter { pattern ->
            when {
                pattern.factorName.contains("Lua") && pattern.factorName.contains(conditions.moonPhase) -> true
                pattern.factorName.contains("Vento") && pattern.factorName.contains(conditions.windDirection) -> true
                pattern.factorName.contains("Maré") && pattern.factorName.contains(conditions.tideType) -> true
                pattern.factorName.contains("Hora") && pattern.factorName.contains(conditions.hour.toString()) -> true
                else -> false
            }
        }
        
        // Adicionar probabilidade baseada em confiança
        matchingPatterns.forEach { pattern ->
            probability += (pattern.successRate * pattern.confidence / 100)
        }
        
        return probability.coerceIn(0.0, 100.0)
    }
    
    /**
     * Fase 4: Aprendizado Contínuo
     * Melhora com cada nova sessão de pesca
     */
    suspend fun recordFishingSession(session: FishingSession) {
        // Registra a sessão no banco
        fishingRepository.saveFishingSession(session)
        
        // Recalcula padrões (ML melhora com mais dados)
        findWinningPatterns()
    }
    
    /**
     * Calcula confiança baseado em quantidade de dados
     * Quanto mais evidências, mais confiante
     */
    private fun calculateConfidence(sampleSize: Int): Double {
        // Confiança cresce logaritmicamente com n de amostras
        return when {
            sampleSize < 3 -> 20.0
            sampleSize < 10 -> 40.0
            sampleSize < 20 -> 60.0
            sampleSize < 50 -> 80.0
            else -> 95.0
        }
    }
    
    /**
     * Score de Combinação Perfeita
     * Quando TODOS os fatores se alinham
     */
    suspend fun calculatePerfectAlignmentScore(conditions: EnvironmentalCondition): Double {
        val patterns = findWinningPatterns()
        val topPatterns = patterns.take(5) // Top 5 padrões
        
        var alignmentScore = 0.0
        var alignedFactors = 0
        
        topPatterns.forEach { pattern ->
            if ((pattern.factorName.contains("Lua") && pattern.factorName.contains(conditions.moonPhase)) ||
                (pattern.factorName.contains("Vento") && pattern.factorName.contains(conditions.windDirection)) ||
                (pattern.factorName.contains("Maré") && pattern.factorName.contains(conditions.tideType)) ||
                (pattern.factorName.contains("Hora") && pattern.factorName.contains(conditions.hour.toString())))
            {
                alignmentScore += pattern.confidence
                alignedFactors++
            }
        }
        
        // Score final: média ponderada dos fatores alinhados
        return if (alignedFactors > 0) {
            (alignmentScore / alignedFactors).coerceIn(0.0, 100.0)
        } else {
            0.0
        }
    }
}
