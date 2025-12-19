package com.dutra.agente.psychology.engine

import com.dutra.agente.psychology.models.*
import kotlin.math.abs

class EmpathyEngine {
    
    // Dicionário de palavras-chave para detecção de emoções
    private val emotionKeywords = mapOf(
        EmotionalState.HAPPY to listOf("feliz", "alegre", "contente", "excelente", "amor", "adorei"),
        EmotionalState.SAD to listOf("triste", "infeliz", "deprimido", "chorando", "mal", "pior"),
        EmotionalState.ANXIOUS to listOf("ansioso", "nervoso", "preocupado", "tenso", "medo"),
        EmotionalState.FRUSTRATED to listOf("frustrado", "irritado", "bravo", "raiva", "chato"),
        EmotionalState.CALM to listOf("calmo", "paz", "tranquilo", "relaxado", "sereno"),
        EmotionalState.STRESSED to listOf("estressado", "sobrecarregado", "cansado", "esgotado")
    )
    
    // Palavras de alerta para saúde mental
    private val mentalHealthTriggers = mapOf(
        "depression" to listOf("suicídio", "morte", "nunca melhorar", "sem esperança"),
        "anxiety" to listOf("pânico", "ataque", "descontrole", "louco"),
        "self_harm" to listOf("machucar", "corte", "autoflagelação")
    )
    
    /**
     * Analisa o sentimento de uma mensagem do usuário
     */
    fun analyzeSentiment(message: String): SentimentAnalysis {
        val lowerMessage = message.lowercase()
        val emotions = mutableMapOf<String, Float>()
        var dominantEmotion = EmotionalState.NEUTRAL
        var maxScore = 0f
        
        // Calcular scores para cada emoção
        for ((emotion, keywords) in emotionKeywords) {
            val score = keywords.count { lowerMessage.contains(it) }.toFloat() / keywords.size
            emotions[emotion.name] = score
            
            if (score > maxScore) {
                maxScore = score
                dominantEmotion = emotion
            }
        }
        
        // Detectar se é urgente
        val isUrgent = detectedUrgent(lowerMessage)
        val needsSupport = detectNeedForSupport(lowerMessage)
        
        return SentimentAnalysis(
            sentiment = dominantEmotion,
            confidence = maxScore.coerceIn(0f, 1f),
            emotionScores = emotions,
            keywords = extractKeywords(lowerMessage),
            isUrgent = isUrgent,
            needsSupport = needsSupport
        )
    }
    
    /**
     * Gera uma resposta empática baseada no sentimento
     */
    fun generateEmpathyResponse(
        sentiment: SentimentAnalysis,
        userProfile: UserPsychologicalProfile
    ): EmpathyResponse {
        val acknowledgment = getAcknowledgment(sentiment.sentiment, userProfile.personalityType)
        val validation = getValidation(sentiment.sentiment)
        val support = getSupport(sentiment.sentiment, userProfile.responsePreference)
        val solution = getSolution(sentiment.sentiment, sentiment.keywords)
        val encouragement = getEncouragement(userProfile.personalityType)
        
        return EmpathyResponse(
            acknowledgment = acknowledgment,
            validation = validation,
            support = support,
            solution = solution,
            encouragement = encouragement,
            toneTags = generateToneTags(userProfile.responsePreference)
        )
    }
    
    /**
     * Atualiza o perfil psicológico do usuário
     */
    fun updatePsychologicalProfile(
        profile: UserPsychologicalProfile,
        sentiment: SentimentAnalysis,
        responseEffectiveness: Float
    ): UserPsychologicalProfile {
        // Atualizar estado emocional
        val newMood = calculateNewMood(profile.mood, sentiment.sentiment)
        
        // Atualizar nível de estresse se houver padrões
        val newStressLevel = updateStressLevel(profile.stressLevel, sentiment.sentiment)
        
        return profile.copy(
            currentEmotionalState = sentiment.sentiment,
            mood = newMood,
            stressLevel = newStressLevel,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Detecta alertas de saúde mental
     */
    fun detectMentalHealthAlerts(message: String): List<MentalHealthAlert> {
        val alerts = mutableListOf<MentalHealthAlert>()
        val lowerMessage = message.lowercase()
        
        for ((alertType, keywords) in mentalHealthTriggers) {
            if (keywords.any { lowerMessage.contains(it) }) {
                alerts.add(MentalHealthAlert(
                    alertId = "alert_${System.currentTimeMillis()}",
                    userId = "",
                    alertType = alertType,
                    severity = calculateSeverity(lowerMessage, keywords),
                    keywordsDetected = keywords.filter { lowerMessage.contains(it) },
                    recommendedAction = getRecommendedAction(alertType),
                    professionalResources = getProfessionalResources(alertType)
                ))
            }
        }
        
        return alerts
    }
    
    /**
     * Recomenda atividades de auto-cuidado
     */
    fun recommendSelfCare(profile: UserPsychologicalProfile): List<SelfCareRecommendation> {
        val recommendations = mutableListOf<SelfCareRecommendation>()
        
        // Recomendar com base no nível de estresse
        if (profile.stressLevel > 7) {
            recommendations.add(SelfCareRecommendation(
                recommendationId = "care_stress_${System.currentTimeMillis()}",
                category = "meditation",
                description = "Passe 10 minutos em meditação para reduzir o estresse",
                durationMinutes = 10,
                difficultyLevel = "easy",
                effectivenessScore = 0.85f
            ))
        }
        
        // Recomendar com base no humor
        if (profile.mood < -5) {
            recommendations.add(SelfCareRecommendation(
                recommendationId = "care_mood_${System.currentTimeMillis()}",
                category = "exercise",
                description = "Faça uma caminhada rápida para melhorar seu humor",
                durationMinutes = 30,
                difficultyLevel = "easy",
                effectivenessScore = 0.78f
            ))
        }
        
        return recommendations
    }
    
    // Helper methods
    private fun getAcknowledgment(emotion: EmotionalState, personality: UserPersonality): String {
        return when (emotion) {
            EmotionalState.HAPPY -> "Que legal! Fico feliz em ouvir isso!"
            EmotionalState.SAD -> "Entendo que você está se sentindo triste..."
            EmotionalState.ANXIOUS -> "Vejo que você está preocupado..."
            EmotionalState.FRUSTRATED -> "Compreendo sua frustração..."
            else -> "Obrigado por compartilhar isso comigo..."
        }
    }
    
    private fun getValidation(emotion: EmotionalState): String {
        return "Seus sentimentos são válidos e importantes. Tudo bem sentir dessa forma."
    }
    
    private fun getSupport(emotion: EmotionalState, preference: ResponsePreference): String {
        return "Estou aqui para ajudar e apoiar você no que precisar."
    }
    
    private fun getSolution(emotion: EmotionalState, keywords: List<String>): String {
        return "Vamos pensar juntos em como podemos melhorar essa situação."
    }
    
    private fun getEncouragement(personality: UserPersonality): String {
        return "Você é mais forte do que pensa. Acredite em si mesmo!"
    }
    
    private fun generateToneTags(preference: ResponsePreference): List<String> {
        return listOf(preference.preferredTone, "supportive", "caring")
    }
    
    private fun detectedUrgent(message: String): Boolean {
        val urgentKeywords = listOf("ajuda", "emergência", "agora", "risco", "perigo")
        return urgentKeywords.any { message.contains(it) }
    }
    
    private fun detectNeedForSupport(message: String): Boolean {
        val supportKeywords = listOf("ajuda", "preciso", "não aguento", "cansado", "difícil")
        return supportKeywords.any { message.contains(it) }
    }
    
    private fun extractKeywords(message: String): List<String> {
        return message.split(" ").filter { it.length > 4 }.take(5)
    }
    
    private fun calculateNewMood(currentMood: Int, emotion: EmotionalState): Int {
        val moodChange = when (emotion) {
            EmotionalState.HAPPY -> 3
            EmotionalState.SAD -> -3
            EmotionalState.CALM -> 2
            EmotionalState.STRESSED -> -4
            else -> 0
        }
        return (currentMood + moodChange).coerceIn(-10, 10)
    }
    
    private fun updateStressLevel(currentLevel: Int, emotion: EmotionalState): Int {
        val change = when (emotion) {
            EmotionalState.STRESSED -> 2
            EmotionalState.ANXIOUS -> 1
            EmotionalState.CALM -> -2
            EmotionalState.HAPPY -> -1
            else -> 0
        }
        return (currentLevel + change).coerceIn(0, 10)
    }
    
    private fun calculateSeverity(message: String, keywords: List<String>): Int {
        val keywordCount = keywords.count { message.contains(it) }
        return minOf(10, keywordCount * 2)
    }
    
    private fun getRecommendedAction(alertType: String): String {
        return when (alertType) {
            "depression" -> "Procure por ajuda profissional imediatamente"
            "anxiety" -> "Experimente técnicas de respiração e contate um profissional"
            else -> "Procure apoio profissional especializado"
        }
    }
    
    private fun getProfessionalResources(alertType: String): List<String> {
        return listOf(
            "CVV - Central de Valorização da Vida: 188",
            "SAMU: 192",
            "Procure um psicólogo ou psiquiatra"
        )
    }
}
