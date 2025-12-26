package com.smith.stress

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import kotlin.math.abs
import kotlinx.coroutines.*

/**
 * MODULO DE DETECCAO DE STRESS - Smith Phase 5.1
 * Monitora multiplos parametros biologicos e psicologicos para detectar stress em tempo real
 */

data class StressMetrics(
    val heartRateVariability: Float,
    val facialTension: Float,
    val voicePitch: Float,
    val respiratoryRate: Float,
    val skinConductance: Float,
    val pupilDilation: Float,
    val hesitationLevel: Int,
    val stressScore: Float
)

data class BreathingExercise(
    val name: String,
    val description: String,
    val duration: Int,
    val pattern: String,
    val cycles: Int
)

class StressDetectionModule(private val context: Context) {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var heartRateSensor: Sensor? = null
    private var accelerometer: Sensor? = null
    
    init {
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    
    // ==================== CALCULO DE STRESS ====================
    
    suspend fun analyzeStressLevel(
        facialData: FacialExpressionEntity,
        voiceData: VoiceAnalysisEntity,
        bodyLanguageData: BodyLanguageEntity
    ): StressMetrics = withContext(Dispatchers.Default) {
        
        val facialTension = calculateFacialTension(facialData)
        val voicePitch = normalizeVoicePitch(voiceData.pitch)
        val respiratoryRate = estimateRespiratoryRate(facialData, bodyLanguageData)
        val heartRateVariability = estimateHeartRateVariability(voiceData)
        val skinConductance = calculateSkinConductance(facialData)
        val pupilDilation = facialData.pupilDilation
        val hesitationLevel = voiceData.hesitations
        
        val stressScore = calculateWeightedStressScore(
            facialTension = facialTension,
            voicePitch = voicePitch,
            respiratoryRate = respiratoryRate,
            heartRateVariability = heartRateVariability,
            skinConductance = skinConductance,
            pupilDilation = pupilDilation,
            hesitationLevel = hesitationLevel.toFloat()
        )
        
        StressMetrics(
            heartRateVariability = heartRateVariability,
            facialTension = facialTension,
            voicePitch = voicePitch,
            respiratoryRate = respiratoryRate,
            skinConductance = skinConductance,
            pupilDilation = pupilDilation,
            hesitationLevel = hesitationLevel,
            stressScore = stressScore
        )
    }
    
    private fun calculateFacialTension(facialData: FacialExpressionEntity): Float {
        val emotionWeights = mapOf(
            "ANGER" to 0.9f,
            "FEAR" to 0.8f,
            "SADNESS" to 0.6f,
            "NEUTRAL" to 0.2f,
            "JOY" to 0.1f,
            "SURPRISE" to 0.5f,
            "DISGUST" to 0.7f
        )
        
        val emotionScore = emotionWeights[facialData.emotion] ?: 0.3f
        return (facialData.facialTension * 0.6f + emotionScore * 0.4f)
    }
    
    private fun normalizeVoicePitch(pitch: Float): Float {
        val baselinePitch = 100f
        val deviation = abs(pitch - baselinePitch) / baselinePitch
        return minOf(deviation, 1.0f)
    }
    
    private fun estimateRespiratoryRate(
        facialData: FacialExpressionEntity,
        bodyLanguageData: BodyLanguageEntity
    ): Float {
        val fidgetingFactor = bodyLanguageData.fidgetingLevel
        val tensionFactor = facialData.facialTension
        val baslineRate = 12f
        
        val estimatedRate = baslineRate + (fidgetingFactor * 8f) + (tensionFactor * 5f)
        return estimatedRate / 30f
    }
    
    private fun estimateHeartRateVariability(voiceData: VoiceAnalysisEntity): Float {
        val hesitationFactor = voiceData.hesitations / 10f
        val volumeVariation = abs(voiceData.volume - 60f) / 60f
        val rateVariation = abs(voiceData.speechRate - 150f) / 150f
        
        return ((hesitationFactor + volumeVariation + rateVariation) / 3f)
    }
    
    private fun calculateSkinConductance(facialData: FacialExpressionEntity): Float {
        val pupilDilation = facialData.pupilDilation
        val emotionIntensity = facialData.emotionIntensity
        
        return (pupilDilation * 0.6f + emotionIntensity * 0.4f)
    }
    
    private fun calculateWeightedStressScore(
        facialTension: Float,
        voicePitch: Float,
        respiratoryRate: Float,
        heartRateVariability: Float,
        skinConductance: Float,
        pupilDilation: Float,
        hesitationLevel: Float
    ): Float {
        return (
            facialTension * 0.25f +
            voicePitch * 0.20f +
            respiratoryRate * 0.15f +
            heartRateVariability * 0.15f +
            skinConductance * 0.10f +
            pupilDilation * 0.10f +
            (hesitationLevel / 10f) * 0.05f
        ) * 100f
    }
    
    // ==================== ALERTAS DE STRESS ====================
    
    fun generateStressAlert(stressScore: Float): String = when {
        stressScore > 80f -> "CRÍTICO: Seu stress está muito alto! Faça uma pausa AGORA e respire profundamente."
        stressScore > 70f -> "AVISO: Detectei stress elevado. Recomendo uma pausa de 5 minutos."
        stressScore > 50f -> "INFO: Você está um pouco estressado. Talvez seja bom relaxar um pouco."
        else -> "Tudo bem! Seu stress está em nível normal."
    }
    
    fun shouldPauseDaycision(stressScore: Float): Boolean = stressScore > 75f
    
    // ==================== EXERCICIOS DE RESPIRACAO ====================
    
    fun getBreathingExercises(): List<BreathingExercise> = listOf(
        BreathingExercise(
            name = "Respiracao 4-7-8",
            description = "Inspire por 4, segure por 7, expire por 8",
            duration = 5,
            pattern = "4-7-8",
            cycles = 4
        ),
        BreathingExercise(
            name = "Respiracao Profunda Simples",
            description = "Inspire profundamente, mantenha, expire lentamente",
            duration = 3,
            pattern = "4-4-4",
            cycles = 10
        ),
        BreathingExercise(
            name = "Respiracao Box",
            description = "Inspire, mantenha, expire, mantenha - tudo em 4 segundos",
            duration = 4,
            pattern = "4-4-4-4",
            cycles = 5
        ),
        BreathingExercise(
            name = "Respiracao Alternada",
            description = "Respire por uma narina, expire pela outra",
            duration = 5,
            pattern = "Alternada",
            cycles = 8
        )
    )
    
    // ==================== MEDITACAO GUIADA ====================
    
    fun getGuidedMeditations(): List<String> = listOf(
        "Foque na sua respiracao. Cada inspiracao traz calma. Cada expiracao liberta tensao.",
        "Visualize um lugar seguro e pacifico. Sinta a tranquilidade envolvendo voce.",
        "Deixe seus pensamentos passarem como nuvens. Nao julgue, apenas observe.",
        "Seu corpo esta relaxado. Seus musculos estao soltos. Sua mente esta clara.",
        "Com cada respiracao, voce fica mais calmo. Com cada segundo, mais paz."
    )
    
    // ==================== RECOMENDACOES PARA BAIXAR STRESS ====================
    
    fun getStressReductionRecommendations(stressScore: Float): List<String> = when {
        stressScore > 75f -> listOf(
            "1. Pause AGORA - nao tome decisoes neste momento",
            "2. Faca uma respiracao 4-7-8 (4 ciclos)",
            "3. Saia do computador por 15 minutos",
            "4. Caminhe ao ar livre se possivel",
            "5. Beba agua e respire profundamente",
            "6. Nao tome decisoes financeiras agora"
        )
        stressScore > 50f -> listOf(
            "1. Pause por 5 minutos",
            "2. Faca uma meditacao guiada curta",
            "3. Alongue um pouco",
            "4. Beba agua",
            "5. Respire profundamente 10 vezes"
        )
        else -> listOf(
            "Voce esta em um bom estado mental.",
            "Continue assim!",
            "Aproveite este estado para tomar decisoes importantes."
        )
    }
    
    // ==================== HISTORICO DE STRESS ====================
    
    data class StressHistory(
        val timestamp: Long,
        val stressScore: Float,
        val triggerEvent: String
    )
    
    private val stressHistory = mutableListOf<StressHistory>()
    
    fun recordStressEvent(stressScore: Float, triggerEvent: String) {
        stressHistory.add(StressHistory(
            timestamp = System.currentTimeMillis(),
            stressScore = stressScore,
            triggerEvent = triggerEvent
        ))
    }
    
    fun getStressTrends(): String {
        if (stressHistory.isEmpty()) return "Nenhum dado de stress registrado ainda."
        
        val avgStress = stressHistory.map { it.stressScore }.average()
        val maxStress = stressHistory.maxOf { it.stressScore }
        val minStress = stressHistory.minOf { it.stressScore }
        
        return """ANALISE DE STRESS:
            |Stress Medio: $avgStress%
            |Stress Maximo: $maxStress%
            |Stress Minimo: $minStress%
            |Total de Eventos: ${stressHistory.size}
        """.trimMargin()
    }
}
