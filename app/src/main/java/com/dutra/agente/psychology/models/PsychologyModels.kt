package com.dutra.agente.psychology.models

import com.google.gson.annotations.SerializedName
import java.util.Date

// Enums para emoções e estados
enum class EmotionalState {
    HAPPY, SAD, ANXIOUS, FRUSTRATED, CALM, CONFUSED, EXCITED, NEUTRAL, TIRED, STRESSED
}

enum class UserPersonality {
    INTROVERT, EXTROVERT, ANALYTICAL, CREATIVE, PRAGMATIC, IDEALISTIC
}

// Modelo de Perfil Psicológico do Usuário
data class UserPsychologicalProfile(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("personality_type")
    val personalityType: UserPersonality,
    @SerializedName("current_emotional_state")
    val currentEmotionalState: EmotionalState,
    @SerializedName("stress_level")
    val stressLevel: Int, // 0-10
    @SerializedName("mood")
    val mood: Int, // -10 to 10
    @SerializedName("communication_style")
    val communicationStyle: String,
    @SerializedName("response_preference")
    val responsePreference: ResponsePreference,
    @SerializedName("trauma_triggers")
    val traumaTriggers: List<String>,
    @SerializedName("positive_reinforcement_words")
    val positiveReinforcementWords: List<String>,
    @SerializedName("last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)

// Preferências de Resposta
data class ResponsePreference(
    @SerializedName("preferred_tone")
    val preferredTone: String, // "formal", "casual", "empathetic", "humorous"
    @SerializedName("detail_level")
    val detailLevel: String, // "brief", "moderate", "detailed"
    @SerializedName("use_emojis")
    val useEmojis: Boolean,
    @SerializedName("direct_or_indirect")
    val directOrIndirect: String // "direct", "indirect", "balanced"
)

// Análise de Sentimento da Mensagem
data class SentimentAnalysis(
    @SerializedName("sentiment")
    val sentiment: EmotionalState,
    @SerializedName("confidence")
    val confidence: Float, // 0-1
    @SerializedName("emotion_scores")
    val emotionScores: Map<String, Float>,
    @SerializedName("keywords")
    val keywords: List<String>,
    @SerializedName("is_urgent")
    val isUrgent: Boolean,
    @SerializedName("needs_support")
    val needsSupport: Boolean
)

// Resposta Empática
data class EmpathyResponse(
    @SerializedName("acknowledgment")
    val acknowledgment: String, // Reconhecer os sentimentos
    @SerializedName("validation")
    val validation: String, // Validar as emoções
    @SerializedName("support")
    val support: String, // Oferecer suporte
    @SerializedName("solution")
    val solution: String, // Sugerir solução
    @SerializedName("encouragement")
    val encouragement: String, // Encorajar
    @SerializedName("tone_tags")
    val toneTags: List<String>
)

// Histórico de Interações
data class InteractionHistory(
    @SerializedName("interaction_id")
    val interactionId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("sentiment_before")
    val sentimentBefore: EmotionalState,
    @SerializedName("sentiment_after")
    val sentimentAfter: EmotionalState,
    @SerializedName("response_effectiveness")
    val responseEffectiveness: Float, // 0-1
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("context")
    val context: String
)

// Padrões de Bem-estar
data class WellnessPattern(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("best_interaction_time")
    val bestInteractionTime: String, // hora do dia quando o usuário é mais receptivo
    @SerializedName("stress_triggers")
    val stressTriggers: List<String>,
    @SerializedName("coping_strategies")
    val copingStrategies: List<String>,
    @SerializedName("preferred_support_type")
    val preferredSupportType: String // "listening", "advice", "action", "humor"
)

// Ícones de Alerta (sinais de risco)
data class MentalHealthAlert(
    @SerializedName("alert_id")
    val alertId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("alert_type")
    val alertType: String, // "depression", "anxiety", "suicidal", "self_harm", "crisis"
    @SerializedName("severity")
    val severity: Int, // 1-10
    @SerializedName("keywords_detected")
    val keywordsDetected: List<String>,
    @SerializedName("recommended_action")
    val recommendedAction: String,
    @SerializedName("professional_resources")
    val professionalResources: List<String>,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

// Sugestões de Auto-cuidado
data class SelfCareRecommendation(
    @SerializedName("recommendation_id")
    val recommendationId: String,
    @SerializedName("category")
    val category: String, // "exercise", "sleep", "meditation", "social", "nutrition"
    @SerializedName("description")
    val description: String,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("difficulty_level")
    val difficultyLevel: String, // "easy", "medium", "challenging"
    @SerializedName("effectiveness_score")
    val effectivenessScore: Float
)
