package com.smith.repair

import androidx.room.*
import kotlinx.coroutines.*

/**
 * BANCO DE DADOS DE REPAROS - Smith iFixit Integration
 * Guia completo de reparos para equipamentos eletronicos, mecanicos e eletrodomesticos
 * Baseado na estrutura do iFixit.com
 */

// ==================== ENTIDADES ====================

@Entity(tableName = "repair_guides")
data class RepairGuideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // ELETRONICA, MECANICA, ELETRODOMESTICO, INFORMATICA
    val deviceType: String, // iPhone, Computador, Geladeira, etc
    val difficulty: String, // FACIL, MODERADO, DIFICIL
    val estimatedTime: Int, // em minutos
    val requiredTools: String, // lista de ferramentas
    val totalSteps: Int,
    val description: String,
    val successRate: Float, // 0-100 percentual de sucesso
    val viewCount: Long = 0,
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "repair_steps")
data class RepairStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val guideId: Long,
    val stepNumber: Int,
    val title: String,
    val description: String,
    val detailedInstructions: String,
    val warnings: String, // avisos de seguranca
    val tips: String, // dicas uteis
    val imageUrl: String = "",
    val videoUrl: String = ""
)

@Entity(tableName = "repair_tools")
data class ToolEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolName: String,
    val category: String, // BASICO, ESPECIALIZADO, OPCIONAL
    val description: String,
    val approximatePrice: String,
    val whereToFind: String,
    val alternatives: String // ferramentas que podem substituir
)

@Entity(tableName = "repair_problems")
data class RepairProblemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val guideId: Long,
    val problemDescription: String,
    val symptoms: String, // sintomas do problema
    val rootCause: String, // causa raiz
    val severity: String, // LEVE, MODERADA, CRITICA
    val repairDifficulty: String,
    val estimatedCost: String,
    val fixableAtHome: Boolean
)

@Entity(tableName = "repair_parts")
data class PartEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val guideId: Long,
    val partName: String,
    val partNumber: String,
    val description: String,
    val approximatePrice: String,
    val whereToFind: String,
    val compatibility: String, // modelos compatíveis
    val riskLevel: String // sem risco, baixo risco, alto risco
)

@Entity(tableName = "repair_safety_warnings")
data class SafetyWarningEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val guideId: Long,
    val warningTitle: String,
    val description: String,
    val severity: String, // BAIXA, MEDIA, ALTA, CRITICA
    val precautions: String
)

// ==================== DAOs ====================

@Dao
interface RepairGuideDao {
    @Insert
    suspend fun insertGuide(guide: RepairGuideEntity)
    
    @Query("SELECT * FROM repair_guides WHERE category = :category")
    suspend fun getGuidesByCategory(category: String): List<RepairGuideEntity>
    
    @Query("SELECT * FROM repair_guides WHERE deviceType LIKE '%' || :device || '%'")
    suspend fun getGuidesByDevice(device: String): List<RepairGuideEntity>
    
    @Query("SELECT * FROM repair_guides ORDER BY successRate DESC LIMIT 10")
    suspend fun getTopRatedGuides(): List<RepairGuideEntity>
    
    @Query("SELECT * FROM repair_guides WHERE difficulty = :difficulty")
    suspend fun getGuidesByDifficulty(difficulty: String): List<RepairGuideEntity>
}

@Dao
interface RepairStepDao {
    @Insert
    suspend fun insertStep(step: RepairStepEntity)
    
    @Query("SELECT * FROM repair_steps WHERE guideId = :guideId ORDER BY stepNumber ASC")
    suspend fun getStepsByGuideId(guideId: Long): List<RepairStepEntity>
    
    @Query("SELECT * FROM repair_steps WHERE guideId = :guideId AND stepNumber = :stepNumber")
    suspend fun getSpecificStep(guideId: Long, stepNumber: Int): RepairStepEntity?
}

@Dao
interface ToolDao {
    @Insert
    suspend fun insertTool(tool: ToolEntity)
    
    @Query("SELECT * FROM repair_tools WHERE category = :category")
    suspend fun getToolsByCategory(category: String): List<ToolEntity>
    
    @Query("SELECT * FROM repair_tools")
    suspend fun getAllTools(): List<ToolEntity>
}

@Dao
interface ProblemDao {
    @Insert
    suspend fun insertProblem(problem: RepairProblemEntity)
    
    @Query("SELECT * FROM repair_problems WHERE guideId = :guideId")
    suspend fun getProblemsByGuide(guideId: Long): List<RepairProblemEntity>
}

@Dao
interface PartDao {
    @Insert
    suspend fun insertPart(part: PartEntity)
    
    @Query("SELECT * FROM repair_parts WHERE guideId = :guideId")
    suspend fun getPartsByGuide(guideId: Long): List<PartEntity>
}

@Dao
interface SafetyWarningDao {
    @Insert
    suspend fun insertWarning(warning: SafetyWarningEntity)
    
    @Query("SELECT * FROM repair_safety_warnings WHERE guideId = :guideId ORDER BY severity DESC")
    suspend fun getWarningsByGuide(guideId: Long): List<SafetyWarningEntity>
}

// ==================== DATABASE ====================

@Database(
    entities = [
        RepairGuideEntity::class,
        RepairStepEntity::class,
        ToolEntity::class,
        RepairProblemEntity::class,
        PartEntity::class,
        SafetyWarningEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RepairDatabase : RoomDatabase() {
    abstract fun repairGuideDao(): RepairGuideDao
    abstract fun repairStepDao(): RepairStepDao
    abstract fun toolDao(): ToolDao
    abstract fun problemDao(): ProblemDao
    abstract fun partDao(): PartDao
    abstract fun safetyWarningDao(): SafetyWarningDao
}
