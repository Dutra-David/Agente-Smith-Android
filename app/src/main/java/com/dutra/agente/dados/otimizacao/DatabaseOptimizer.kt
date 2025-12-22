package com.dutra.agente.dados.otimizacao

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.system.measureTimeMillis

/**
 * DatabaseOptimizer - Otimiza performance do banco de dados Room
 * 
 * Aplica indices, batch operations e outras otimizacoes
 * para melhorar velocidade de leitura/escrita
 */
class DatabaseOptimizer {
    
    /**
     * Aplica indices ao banco de dados para otimizar queries
     * 
     * Indices melhoram performance em colunas frequentemente consultadas
     */
    fun createIndexes(database: SupportSQLiteDatabase) {
        // Indice para tabela de mensagens por usuario
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_mensagens_usuario ON mensagens(usuario_id)"
        )
        
        // Indice para tabela de padroes por tipo
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_padroes_tipo ON padroes(tipo)"
        )
        
        // Indice composto para sincronizacao
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_sync_status ON sync_queue(status, timestamp)"
        )
        
        // Indice para buscas rapidas
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_historico_timestamp ON historico(timestamp DESC)"
        )
    }
    
    /**
     * Implementa batch operations para insercoes em massa
     * 
     * Mais eficiente que insercoes individuais
     */
    suspend inline fun <T> batchInsert(
        items: List<T>,
        batchSize: Int = 500,
        crossinline insertBlock: suspend (List<T>) -> Unit
    ) {
        items.chunked(batchSize).forEach { batch ->
            insertBlock(batch)
        }
    }
    
    /**
     * Otimiza queries com VACUUM para reclamar espaco
     */
    fun optimizeDatabase(database: SupportSQLiteDatabase) {
        database.execSQL("VACUUM")
    }
    
    /**
     * Analisa performance de queries
     */
    fun analyzeQueryPerformance(
        database: SupportSQLiteDatabase,
        query: String
    ): QueryPerformanceMetrics {
        val executionTime = measureTimeMillis {
            database.query(query)
        }
        
        return QueryPerformanceMetrics(
            query = query,
            executionTimeMs = executionTime,
            isOptimal = executionTime < 100 // Menos de 100ms e considerado otimo
        )
    }
    
    /**
     * Retorna configuracao recomendada para producao
     */
    fun getProductionConfig(): DatabaseOptimizationConfig {
        return DatabaseOptimizationConfig(
            enableWAL = true,
            enableForeignKeys = true,
            cacheSize = 10000,
            pageSize = 4096,
            journalMode = "WAL",
            syncMode = "NORMAL"
        )
    }
}

/**
 * Metricas de performance de query
 */
data class QueryPerformanceMetrics(
    val query: String,
    val executionTimeMs: Long,
    val isOptimal: Boolean
)

/**
 * Configuracao de otimizacao de banco de dados
 */
data class DatabaseOptimizationConfig(
    val enableWAL: Boolean = true, // Write-Ahead Logging
    val enableForeignKeys: Boolean = true,
    val cacheSize: Int = 10000,
    val pageSize: Int = 4096,
    val journalMode: String = "WAL",
    val syncMode: String = "NORMAL"
)

/**
 * Estrategia de otimizacao
 */
enum class OptimizationStrategy {
    AGGRESSIVE,   // Maxima velocidade
    BALANCED,     // Equilibrio entre velocidade e seguranca
    CONSERVATIVE  // Maxima seguranca
}
