package com.dutra.agente.psicologia

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Data class representing behavioral patterns
 */
data class BehavioralPattern(
    val patternId: String,
    val patternType: String, // EMOTIONAL, ENGAGEMENT, COMMUNICATION, RESPONSE_TIME
    val frequency: Int,
    val averageValue: Double,
    val trend: String, // INCREASING, DECREASING, STABLE
    val lastOccurrence: String,
    val confidence: Double // 0.0 to 1.0
)

/**
 * Data class for pattern analysis results
 */
data class PatternAnalysisResult(
    val userId: String,
    val patterns: List<BehavioralPattern>,
    val dominantPattern: BehavioralPattern?,
    val riskPatterns: List<BehavioralPattern>,
    val positivePatterns: List<BehavioralPattern>,
    val analysisTimestamp: String,
    val confidence: Double
)

/**
 * PatternAnalyzer - Analyzes behavioral patterns from user interaction data
 * Detects trends, anomalies, and dominant behaviors
 */
class PatternAnalyzer {

    /**
     * Analyzes behavioral patterns from historical data
     */
    suspend fun analyzePatterns(
        userId: String,
        emotionHistory: List<Int>,
        engagementHistory: List<Double>,
        responseTimeHistory: List<Long>,
        communicationHistory: List<String>
    ): PatternAnalysisResult = withContext(Dispatchers.Default) {
        val patterns = mutableListOf<BehavioralPattern>()

        // Analyze emotional patterns
        patterns.addAll(analyzeEmotionalPatterns(emotionHistory))

        // Analyze engagement patterns
        patterns.addAll(analyzeEngagementPatterns(engagementHistory))

        // Analyze response time patterns
        patterns.addAll(analyzeResponseTimePatterns(responseTimeHistory))

        // Analyze communication patterns
        patterns.addAll(analyzeCommunicationPatterns(communicationHistory))

        // Identify dominant pattern
        val dominantPattern = patterns.maxByOrNull { it.frequency }

        // Separate risk and positive patterns
        val riskPatterns = patterns.filter { isRiskPattern(it) }
        val positivePatterns = patterns.filter { !isRiskPattern(it) }

        // Calculate overall confidence
        val confidence = patterns.map { it.confidence }.average()

        PatternAnalysisResult(
            userId = userId,
            patterns = patterns,
            dominantPattern = dominantPattern,
            riskPatterns = riskPatterns,
            positivePatterns = positivePatterns,
            analysisTimestamp = LocalDateTime.now().toString(),
            confidence = confidence
        )
    }

    /**
     * Analyzes emotional patterns
     */
    private fun analyzeEmotionalPatterns(emotionHistory: List<Int>): List<BehavioralPattern> {
        if (emotionHistory.isEmpty()) return emptyList()

        val patterns = mutableListOf<BehavioralPattern>()
        val grouped = emotionHistory.groupingBy { it }.eachCount()

        grouped.forEach { (emotion, count) ->
            patterns.add(
                BehavioralPattern(
                    patternId = "emotional_${emotion}",
                    patternType = "EMOTIONAL",
                    frequency = count,
                    averageValue = emotion.toDouble(),
                    trend = detectTrend(emotionHistory.takeLast(10).map { it.toDouble() }),
                    lastOccurrence = LocalDateTime.now().toString(),
                    confidence = calculateConfidence(count, emotionHistory.size)
                )
            )
        }

        return patterns
    }

    /**
     * Analyzes engagement patterns
     */
    private fun analyzeEngagementPatterns(engagementHistory: List<Double>): List<BehavioralPattern> {
        if (engagementHistory.isEmpty()) return emptyList()

        val patterns = mutableListOf<BehavioralPattern>()
        val average = engagementHistory.average()
        val buckets = mapOf(
            "LOW" to (0.0..0.33),
            "MEDIUM" to (0.33..0.66),
            "HIGH" to (0.66..1.0)
        )

        buckets.forEach { (level, range) ->
            val count = engagementHistory.count { it in range }
            if (count > 0) {
                patterns.add(
                    BehavioralPattern(
                        patternId = "engagement_$level",
                        patternType = "ENGAGEMENT",
                        frequency = count,
                        averageValue = engagementHistory.filter { it in range }.average(),
                        trend = detectTrend(engagementHistory.takeLast(10)),
                        lastOccurrence = LocalDateTime.now().toString(),
                        confidence = calculateConfidence(count, engagementHistory.size)
                    )
                )
            }
        }

        return patterns
    }

    /**
     * Analyzes response time patterns
     */
    private fun analyzeResponseTimePatterns(responseTimeHistory: List<Long>): List<BehavioralPattern> {
        if (responseTimeHistory.isEmpty()) return emptyList()

        val average = responseTimeHistory.average()
        val patterns = mutableListOf<BehavioralPattern>()

        // Categorize by response time
        val fastResponses = responseTimeHistory.count { it < average * 0.8 }
        val normalResponses = responseTimeHistory.count { it in (average * 0.8).toLong()..(average * 1.2).toLong() }
        val slowResponses = responseTimeHistory.count { it > average * 1.2 }

        patterns.add(
            BehavioralPattern(
                patternId = "response_time_average",
                patternType = "RESPONSE_TIME",
                frequency = responseTimeHistory.size,
                averageValue = average,
                trend = detectTrend(responseTimeHistory.map { it.toDouble() }),
                lastOccurrence = LocalDateTime.now().toString(),
                confidence = 0.8
            )
        )

        return patterns
    }

    /**
     * Analyzes communication patterns
     */
    private fun analyzeCommunicationPatterns(communicationHistory: List<String>): List<BehavioralPattern> {
        if (communicationHistory.isEmpty()) return emptyList()

        val patterns = mutableListOf<BehavioralPattern>()
        val wordFrequency = communicationHistory
            .flatMap { it.split(" ") }
            .filter { it.length > 3 }
            .groupingBy { it.lowercase() }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }
            .take(5)

        val average = communicationHistory.map { it.length }.average()

        patterns.add(
            BehavioralPattern(
                patternId = "communication_style",
                patternType = "COMMUNICATION",
                frequency = communicationHistory.size,
                averageValue = average,
                trend = "STABLE",
                lastOccurrence = LocalDateTime.now().toString(),
                confidence = calculateConfidence(communicationHistory.size, 100)
            )
        )

        return patterns
    }

    /**
     * Detects trend in time series data
     */
    private fun detectTrend(values: List<Double>): String {
        if (values.size < 2) return "STABLE"

        val firstHalf = values.take(values.size / 2).average()
        val secondHalf = values.drop(values.size / 2).average()
        val percentChange = ((secondHalf - firstHalf) / firstHalf) * 100

        return when {
            percentChange > 5 -> "INCREASING"
            percentChange < -5 -> "DECREASING"
            else -> "STABLE"
        }
    }

    /**
     * Calculates confidence score based on sample size
     */
    private fun calculateConfidence(sampleCount: Int, totalCount: Int): Double {
        return minOf(1.0, (sampleCount.toDouble() / totalCount) * 0.9 + 0.1)
    }

    /**
     * Determines if a pattern represents risk
     */
    private fun isRiskPattern(pattern: BehavioralPattern): Boolean {
        return when (pattern.patternType) {
            "EMOTIONAL" -> pattern.averageValue < 3.0 // Low emotional state
            "ENGAGEMENT" -> pattern.averageValue < 0.3 // Low engagement
            "RESPONSE_TIME" -> pattern.averageValue > 10000 // Very slow responses
            else -> false
        }
    }

    /**
     * Detects pattern changes over time
     */
    suspend fun detectPatternChange(
        currentPatterns: List<BehavioralPattern>,
        previousPatterns: List<BehavioralPattern>
    ): List<Pair<BehavioralPattern, String>> = withContext(Dispatchers.Default) {
        val changes = mutableListOf<Pair<BehavioralPattern, String>>()

        currentPatterns.forEach { current ->
            val previous = previousPatterns.find { it.patternId == current.patternId }
            if (previous != null) {
                val percentChange = ((current.frequency - previous.frequency).toDouble() / previous.frequency) * 100
                if (abs(percentChange) > 20) {
                    changes.add(current to "Changed by ${String.format("%.2f", percentChange)}%")
                }
            }
        }

        changes
    }
}
