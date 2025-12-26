package com.smith.psychology

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.room.*
import kotlinx.coroutines.*

/**
 * INTEGRAÇÃO DE PSICOLOGIA HUMANA - Smith Psychology Module
 * Usa câmera e microfone para ler expressões, gestões, tom de voz e analisar padrões psicológicos
 * Permissões: CAMERA, RECORD_AUDIO, READ_PHONE_STATE
 */

// ==================== ENTIDADES DE PSICOLOGIA ====================

@Entity(tableName = "psychological_profiles")
data class PsychologicalProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val dominantPersonality: String, // ANALYTICS, DRIVER, EXPRESSIVE, AMIABLE
    val emotionalIntelligence: Float, // 0-100
    val riskTolerance: String, // CONSERVATIVE, MODERATE, AGGRESSIVE
    val decisionMakingStyle: String, // RATIONAL, INTUITIVE, IMPULSIVE
    val stressLevel: Float, // 0-100
    val confidenceScore: Float, // 0-100
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "facial_expressions_analysis")
data class FacialExpressionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val emotion: String, // JOY, SADNESS, ANGER, SURPRISE, FEAR, DISGUST, NEUTRAL
    val emotionIntensity: Float, // 0-1
    val eyeContact: Boolean,
    val pupilDilation: Float,
    val facialTension: Float, // 0-1
    val headTilt: Float // em graus
)

@Entity(tableName = "voice_analysis")
data class VoiceAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val pitch: Float, // Hz
    val volume: Float, // dB
    val speechRate: Float, // palavras por minuto
    val voiceTone: String, // CONFIDENT, NERVOUS, CALM, EXCITED
    val hesitations: Int, // quantas vezes hesitou
    val emotionalTone: String
)

@Entity(tableName = "body_language_tracking")
data class BodyLanguageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val posture: String, // UPRIGHT, SLUMPED, TENSE, RELAXED
    val gestureFrequency: Int, // gestos por minuto
    val armCrossing: Boolean,
    val handPosition: String, // VISIBLE, HIDDEN, ON_FACE, ON_DESK
    val proximityComfort: String, // TOO_CLOSE, COMFORTABLE, TOO_FAR
    val fidgetingLevel: Float // 0-1
)

@Entity(tableName = "behavioral_patterns")
data class BehavioralPatternEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pattern: String, // RISK_SEEKING, LOSS_AVERSION, OVERCONFIDENCE, etc
    val frequency: Int,
    val context: String, // FINANCIAL, PERSONAL, PROFESSIONAL
    val accuracy: Float, // quão preciso é a predição
    val potentialBias: String
)

// ==================== DAOs ====================

@Dao
interface PsychologicalProfileDao {
    @Insert
    suspend fun insertProfile(profile: PsychologicalProfileEntity)
    
    @Update
    suspend fun updateProfile(profile: PsychologicalProfileEntity)
    
    @Query("SELECT * FROM psychological_profiles WHERE userId = :userId")
    suspend fun getProfile(userId: String): PsychologicalProfileEntity?
}

@Dao
interface FacialExpressionDao {
    @Insert
    suspend fun insertExpression(expression: FacialExpressionEntity)
    
    @Query("SELECT * FROM facial_expressions_analysis WHERE timestamp > :timeThreshold ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentExpressions(timeThreshold: Long): List<FacialExpressionEntity>
    
    @Query("SELECT emotion, COUNT(*) as count FROM facial_expressions_analysis WHERE timestamp > :timeThreshold GROUP BY emotion ORDER BY count DESC")
    suspend fun getDominantEmotions(timeThreshold: Long): List<EmotionFrequency>
}

@Dao
interface VoiceAnalysisDao {
    @Insert
    suspend fun insertAnalysis(analysis: VoiceAnalysisEntity)
    
    @Query("SELECT * FROM voice_analysis WHERE timestamp > :timeThreshold ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentAnalysis(timeThreshold: Long): List<VoiceAnalysisEntity>
    
    @Query("SELECT AVG(pitch) as avgPitch, AVG(volume) as avgVolume, AVG(speechRate) as avgSpeechRate FROM voice_analysis WHERE timestamp > :timeThreshold")
    suspend fun getVoiceBaseline(timeThreshold: Long): VoiceBaseline
}

@Dao
interface BodyLanguageDao {
    @Insert
    suspend fun insertData(data: BodyLanguageEntity)
    
    @Query("SELECT * FROM body_language_tracking WHERE timestamp > :timeThreshold ORDER BY timestamp DESC LIMIT 50")
    suspend fun getRecentData(timeThreshold: Long): List<BodyLanguageEntity>
}

@Dao
interface BehavioralPatternDao {
    @Insert
    suspend fun insertPattern(pattern: BehavioralPatternEntity)
    
    @Query("SELECT * FROM behavioral_patterns WHERE context = :context ORDER BY accuracy DESC")
    suspend fun getPatternsForContext(context: String): List<BehavioralPatternEntity>
}

// ==================== DATABASE ====================

@Database(
    entities = [
        PsychologicalProfileEntity::class,
        FacialExpressionEntity::class,
        VoiceAnalysisEntity::class,
        BodyLanguageEntity::class,
        BehavioralPatternEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PsychologyDatabase : RoomDatabase() {
    abstract fun psychologicalProfileDao(): PsychologicalProfileDao
    abstract fun facialExpressionDao(): FacialExpressionDao
    abstract fun voiceAnalysisDao(): VoiceAnalysisDao
    abstract fun bodyLanguageDao(): BodyLanguageDao
    abstract fun behavioralPatternDao(): BehavioralPatternDao
}

// ==================== PERMISSÕES AUTORIZADAS ====================

object SmithPermissions {
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE
    )
    
    fun hasAllPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun getMissingPermissions(context: Context): Array<String> {
        return REQUIRED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }
}

// ==================== REAL-TIME INTEGRATION ====================

data class EmotionFrequency(
    val emotion: String,
    val count: Long
)

data class VoiceBaseline(
    val avgPitch: Float,
    val avgVolume: Float,
    val avgSpeechRate: Float
)

data class RealTimePsychologicalAnalysis(
    val currentEmotion: String,
    val emotionConfidence: Float,
    val estimatedMentalState: String, // FOCUSED, STRESSED, CONFUSED, CONFIDENT
    val recommendedAction: String,
    val trustWorthiness: Float, // quo quão confiável é a pessoa neste momento
    val timestamp: Long = System.currentTimeMillis()
)
