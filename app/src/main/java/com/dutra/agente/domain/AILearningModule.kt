package com.dutra.agente.domain

import kotlin.math.min

/**
 * IMPROVEMENT 2: AI Learning Enhancement Module
 * Enables continuous learning and pattern recognition
 */
data class LearningPattern(
    val pattern: String,
    val frequency: Int,
    val successRate: Float,
    val lastUpdated: Long
)

data class AIDecision(
    val action: String,
    val confidence: Float,
    val reasoning: String
)

class AILearningModule {
    
    private val learnedPatterns = mutableMapOf<String, LearningPattern>()
    private val decisionHistory = mutableListOf<AIDecision>()
    private var learningRate = 0.1f
    private var maxPatterns = 1000
    
    /**
     * Learn from experience and update patterns
     */
    fun learnFromExperience(pattern: String, successful: Boolean) {
        val existing = learnedPatterns[pattern]
        
        if (existing != null) {
            val newFrequency = existing.frequency + 1
            val newSuccessRate = if (successful) {
                (existing.successRate * existing.frequency + 1) / newFrequency
            } else {
                (existing.successRate * existing.frequency) / newFrequency
            }
            
            learnedPatterns[pattern] = existing.copy(
                frequency = newFrequency,
                successRate = newSuccessRate,
                lastUpdated = System.currentTimeMillis()
            )
        } else if (learnedPatterns.size < maxPatterns) {
            learnedPatterns[pattern] = LearningPattern(
                pattern = pattern,
                frequency = 1,
                successRate = if (successful) 1f else 0f,
                lastUpdated = System.currentTimeMillis()
            )
        } else {
            pruneWeakPatterns()
            learnedPatterns[pattern] = LearningPattern(
                pattern = pattern,
                frequency = 1,
                successRate = if (successful) 1f else 0f,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
    
    /**
     * Make AI decision based on learned patterns
     */
    fun makeDecision(context: String): AIDecision {
        val bestPattern = learnedPatterns.values
            .filter { it.frequency > 2 }
            .maxByOrNull { it.successRate * it.frequency }
        
        return if (bestPattern != null && bestPattern.successRate > 0.5f) {
            AIDecision(
                action = bestPattern.pattern,
                confidence = min(bestPattern.successRate, 0.99f),
                reasoning = "Based on ${bestPattern.frequency} successful experiences"
            )
        } else {
            AIDecision(
                action = "EXPLORE",
                confidence = 0.5f,
                reasoning = "Insufficient data, exploring new strategies"
            )
        }
    }
    
    /**
     * Prune weak patterns to free space
     */
    private fun pruneWeakPatterns() {
        val sorted = learnedPatterns.values
            .sortedWith(compareBy({ it.frequency }, { it.successRate }))
        
        val toRemove = sorted.take((maxPatterns * 0.1).toInt())
        toRemove.forEach { learnedPatterns.remove(it.pattern) }
    }
    
    /**
     * Get learning statistics
     */
    fun getLearningStats(): String {
        val totalPatterns = learnedPatterns.size
        val avgFrequency = learnedPatterns.values.map { it.frequency }.average()
        val avgSuccessRate = learnedPatterns.values.map { it.successRate }.average()
        
        return """
            Learning Statistics:
            Total Patterns: $totalPatterns
            Avg Frequency: $avgFrequency
            Avg Success Rate: ${(avgSuccessRate * 100).toInt()}%
            Decision History: ${decisionHistory.size}
        """.trimIndent()
    }
    
    /**
     * Boost learning rate for accelerated improvement
     */
    fun boostLearningRate(multiplier: Float) {
        learningRate *= multiplier
        println("Learning rate boosted to: $learningRate")
    }
    
    /**
     * Get top performing patterns
     */
    fun getTopPatterns(count: Int): List<LearningPattern> {
        return learnedPatterns.values
            .sortedByDescending { it.successRate * it.frequency }
            .take(count)
    }
