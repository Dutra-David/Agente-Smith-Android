package com.smith.financial

import androidx.room.*
import java.util.*

/**
 * BANCO DE DADOS FINANCEIRO - Smith Market Intelligence
 * Analisa notícias financeiras em tempo real e detecta padrões de especulação
 */

// ==================== ENTIDADES ====================

@Entity(tableName = "market_news")
data class FinancialNewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val source: String, // Reuters, Bloomberg, CNBC, etc
    val headline: String,
    val content: String,
    val sentiment: String, // BULLISH, BEARISH, NEUTRAL
    val sentimentScore: Float, // -1.0 to 1.0
    val keywords: String, // Mercado, economia, ações, etc
    val timestamp: Long = System.currentTimeMillis(),
    val relevanceScore: Float = 0f
)

@Entity(tableName = "market_speculation_patterns")
data class SpeculationPatternEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pattern: String,
    val frequency: Int,
    val affectedAssets: String,
    val historicalAccuracy: Float,
    val riskLevel: String, // LOW, MEDIUM, HIGH
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "market_sentiment")
data class MarketSentimentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val overallSentiment: Float, // agregado de todas as notícias
    val newsCount: Int,
    val bullishCount: Int,
    val bearishCount: Int,
    val volatilityIndex: Float
)

@Entity(tableName = "financial_predictions")
data class FinancialPredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val asset: String, // PETR4, VALE3, Dólar, BTC, etc
    val shortTermPrediction: String, // ALTA, BAIXA, ESTÁVEL
    val confidence: Float, // 0-100%
    val timeframe: String, // 1H, 4H, 1D, 1W
    val reasoning: String,
    val basedOnNews: Int, // quantas notícias analisadas
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "news_sentiment_cache")
data class NewsSentimentCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val headline: String,
    val analyzedSentiment: String,
    val confidence: Float,
    val keywords: String
)

// ==================== DAOs ====================

@Dao
interface FinancialNewsDao {
    @Insert
    suspend fun insertNews(news: FinancialNewsEntity)
    
    @Query("SELECT * FROM market_news WHERE timestamp > :timeThreshold ORDER BY timestamp DESC")
    suspend fun getRecentNews(timeThreshold: Long): List<FinancialNewsEntity>
    
    @Query("SELECT * FROM market_news WHERE keywords LIKE '%' || :keyword || '%' ORDER BY sentimentScore DESC LIMIT 50")
    suspend fun searchNewsByKeyword(keyword: String): List<FinancialNewsEntity>
    
    @Query("SELECT AVG(sentimentScore) as avgSentiment, COUNT(*) as count FROM market_news WHERE timestamp > :timeThreshold")
    suspend fun getMarketSentimentTrend(timeThreshold: Long): SentimentTrend
}

@Dao
interface SpeculationPatternDao {
    @Insert
    suspend fun insertPattern(pattern: SpeculationPatternEntity)
    
    @Query("SELECT * FROM market_speculation_patterns WHERE riskLevel = :riskLevel ORDER BY frequency DESC")
    suspend fun getPatternsByRiskLevel(riskLevel: String): List<SpeculationPatternEntity>
    
    @Query("SELECT * FROM market_speculation_patterns WHERE affectedAssets LIKE '%' || :asset || '%' ORDER BY historicalAccuracy DESC")
    suspend fun getPatternsForAsset(asset: String): List<SpeculationPatternEntity>
}

@Dao
interface MarketSentimentDao {
    @Insert
    suspend fun insertSentiment(sentiment: MarketSentimentEntity)
    
    @Query("SELECT * FROM market_sentiment ORDER BY timestamp DESC LIMIT 100")
    suspend fun getSentimentHistory(): List<MarketSentimentEntity>
    
    @Query("SELECT * FROM market_sentiment WHERE timestamp > :timeThreshold ORDER BY timestamp DESC")
    suspend fun getRecentSentiment(timeThreshold: Long): List<MarketSentimentEntity>
}

@Dao
interface FinancialPredictionDao {
    @Insert
    suspend fun insertPrediction(prediction: FinancialPredictionEntity)
    
    @Query("SELECT * FROM financial_predictions WHERE asset = :asset AND timeframe = :timeframe ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestPrediction(asset: String, timeframe: String): FinancialPredictionEntity?
    
    @Query("SELECT * FROM financial_predictions WHERE timestamp > :since ORDER BY confidence DESC")
    suspend fun getRecentPredictions(since: Long): List<FinancialPredictionEntity>
}

// ==================== DATABASE ====================

@Database(
    entities = [
        FinancialNewsEntity::class,
        SpeculationPatternEntity::class,
        MarketSentimentEntity::class,
        FinancialPredictionEntity::class,
        NewsSentimentCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinancialMarketDatabase : RoomDatabase() {
    abstract fun financialNewsDao(): FinancialNewsDao
    abstract fun speculationPatternDao(): SpeculationPatternDao
    abstract fun marketSentimentDao(): MarketSentimentDao
    abstract fun financialPredictionDao(): FinancialPredictionDao
}

data class SentimentTrend(
    val avgSentiment: Float,
    val count: Long
)
