package com.dutra.agente.data.database

import androidx.room.*
import java.util.*

/**
 * 🗄️ BANCO DE DADOS PARA AUTO-MELHORIA DO SMITH
 * Armazena versões de código, métricas e histórico de melhorias
 * COMANDANTE: General (3 Estrelas)
 */

// ==================== ENTIDADES ====================

@Entity(tableName = "code_versions")
data class CodeVersionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "version") val version: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "code_content") val codeContent: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "performance_score") val performanceScore: Float,
    @ColumnInfo(name = "bugs_fixed") val bugsFixed: Int = 0,
    @ColumnInfo(name = "created_by") val createdBy: String, // "AI" ou "HUMAN"
    @ColumnInfo(name = "description") val description: String = ""
)

@Entity(tableName = "performance_metrics")
data class PerformanceMetricEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "version_id") val versionId: String,
    @ColumnInfo(name = "memory_usage_mb") val memoryUsageMb: Float,
    @ColumnInfo(name = "execution_time_ms") val executionTimeMs: Long,
    @ColumnInfo(name = "crash_count") val crashCount: Int = 0,
    @ColumnInfo(name = "overall_score") val overallScore: Float,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "improvements")
data class ImprovementEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "version_id") val versionId: String,
    @ColumnInfo(name = "improvement_type") val improvementType: String, // "PERFORMANCE", "SECURITY", "READABILITY"
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "impact_score") val impactScore: Float,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "commander_log")
data class CommanderLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "commander_name") val commanderName: String,
    @ColumnInfo(name = "commander_rank") val commanderRank: String,
    @ColumnInfo(name = "command_issued") val commandIssued: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "status") val status: String // "PENDING", "IN_PROGRESS", "COMPLETED"
)

// ==================== DAOs ====================

@Dao
interface CodeVersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(codeVersion: CodeVersionEntity)

    @Query("SELECT * FROM code_versions ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestVersion(): CodeVersionEntity?

    @Query("SELECT * FROM code_versions WHERE file_name = :fileName ORDER BY timestamp DESC")
    suspend fun getVersionHistory(fileName: String): List<CodeVersionEntity>

    @Query("SELECT * FROM code_versions ORDER BY timestamp DESC")
    suspend fun getAllVersions(): List<CodeVersionEntity>

    @Delete
    suspend fun delete(codeVersion: CodeVersionEntity)
}

@Dao
interface PerformanceMetricDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric: PerformanceMetricEntity)

    @Query("SELECT * FROM performance_metrics WHERE version_id = :versionId")
    suspend fun getMetricsByVersion(versionId: String): PerformanceMetricEntity?

    @Query("SELECT AVG(overall_score) FROM performance_metrics ORDER BY timestamp DESC LIMIT 10")
    suspend fun getAverageScore(): Float

    @Query("SELECT * FROM performance_metrics ORDER BY overall_score DESC LIMIT 5")
    suspend fun getTopPerformingVersions(): List<PerformanceMetricEntity>
}

@Dao
interface ImprovementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(improvement: ImprovementEntity)

    @Query("SELECT * FROM improvements WHERE version_id = :versionId")
    suspend fun getImprovementsByVersion(versionId: String): List<ImprovementEntity>

    @Query("SELECT SUM(impact_score) FROM improvements WHERE version_id = :versionId")
    suspend fun getTotalImpactScore(versionId: String): Float
}

@Dao
interface CommanderLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: CommanderLogEntity)

    @Query("SELECT * FROM commander_log ORDER BY timestamp DESC")
    suspend fun getCommandLog(): List<CommanderLogEntity>

    @Query("SELECT * FROM commander_log WHERE commander_rank = :rank ORDER BY timestamp DESC")
    suspend fun getLogByRank(rank: String): List<CommanderLogEntity>

    @Query("UPDATE commander_log SET status = :status WHERE id = :logId")
    suspend fun updateStatus(logId: String, status: String)
}

// ==================== DATABASE ====================

@Database(
    entities = [
        CodeVersionEntity::class,
        PerformanceMetricEntity::class,
        ImprovementEntity::class,
        CommanderLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CodeVersionDatabase : RoomDatabase() {
    abstract fun codeVersionDao(): CodeVersionDao
    abstract fun performanceMetricDao(): PerformanceMetricDao
    abstract fun improvementDao(): ImprovementDao
    abstract fun commanderLogDao(): CommanderLogDao

    companion object {
        @Volatile
        private var instance: CodeVersionDatabase? = null

        fun getInstance(context: android.content.Context): CodeVersionDatabase {
            return instance ?: synchronized(this) {
                val newInstance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    CodeVersionDatabase::class.java,
                    "smith_code_version_db"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}
