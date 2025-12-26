package com.smith.voice.financial

import kotlinx.coroutines.*

/**
 * MODULO DE VOICE ASSISTANT + FINANCIAL APIs - Smith Phase 5.4 + 6.2
 * Smith conversa com usuario e acessa dados financeiros em tempo real
 */

// ==================== VOICE ASSISTANT MODULE ====================

data class VoiceCommand(
    val command: String,
    val asset: String,
    val action: String,
    val confidence: Float
)

data class VoiceResponse(
    val message: String,
    val voiceTone: String, // CALM, CONCERNED, EXCITED, WARNING
    val actionNeeded: Boolean
)

class SmithVoiceAssistant {
    
    suspend fun processVoiceCommand(userSpeech: String): VoiceResponse = withContext(Dispatchers.Default) {
        val command = parseVoiceCommand(userSpeech)
        
        val response = generateVoiceResponse(command)
        VoiceResponse(
            message = response,
            voiceTone = determineVoiceTone(userSpeech),
            actionNeeded = shouldTakeAction(command)
        )
    }
    
    private fun parseVoiceCommand(speech: String): VoiceCommand {
        val keywords = mapOf(
            "compra" to "BUY",
            "venda" to "SELL",
            "preco" to "PRICE",
            "tendencia" to "TREND",
            "noticias" to "NEWS"
        )
        
        var action = "QUERY"
        for ((key, value) in keywords) {
            if (speech.lowercase().contains(key)) {
                action = value
                break
            }
        }
        
        return VoiceCommand(
            command = speech,
            asset = extractAsset(speech),
            action = action,
            confidence = calculateConfidence(speech)
        )
    }
    
    private fun extractAsset(speech: String): String {
        val assets = listOf("PETR4", "VALE3", "DOLAR", "BTC", "GOLD")
        for (asset in assets) {
            if (speech.uppercase().contains(asset)) return asset
        }
        return "UNKNOWN"
    }
    
    private fun calculateConfidence(speech: String): Float {
        return if (speech.length > 20) 0.8f else 0.5f
    }
    
    private fun generateVoiceResponse(command: VoiceCommand): String = when(command.action) {
        "BUY" -> "Detectei um possivel sinal de compra para ${command.asset}. Deixe-me analisar."
        "SELL" -> "Voce quer vender? Vou verificar o melhor momento."
        "PRICE" -> "O preco atual de ${command.asset} esta sendo analisado."
        "NEWS" -> "Procurando noticias recentes sobre ${command.asset}."
        else -> "Nao entendi. Pode repetir por favor?"
    }
    
    private fun determineVoiceTone(speech: String): String = when {
        speech.uppercase().contains("URGENTE") -> "CONCERNED"
        speech.contains("!") -> "EXCITED"
        speech.lowercase().contains("cuidado") -> "WARNING"
        else -> "CALM"
    }
    
    private fun shouldTakeAction(command: VoiceCommand): Boolean {
        return command.confidence > 0.7f && command.action != "QUERY"
    }
}

// ==================== FINANCIAL APIs INTEGRATION ====================

data class StockData(
    val symbol: String,
    val price: Float,
    val change: Float,
    val changePercent: Float,
    val volume: Long,
    val timestamp: Long = System.currentTimeMillis()
)

class FinancialAPIClient {
    
    // Yahoo Finance API
    suspend fun getYahooFinanceData(ticker: String): StockData = withContext(Dispatchers.IO) {
        // Simulacao - em producao seria chamada real a API
        StockData(
            symbol = ticker,
            price = 100.0f + kotlin.random.Random.nextFloat() * 50,
            change = kotlin.random.Random.nextFloat() * 5 - 2.5f,
            changePercent = kotlin.random.Random.nextFloat() * 5 - 2.5f,
            volume = kotlin.random.Random.nextLong(1000000, 10000000)
        )
    }
    
    // Alpha Vantage - para dados intraday
    suspend fun getAlphaVantageData(ticker: String): StockData = withContext(Dispatchers.IO) {
        StockData(
            symbol = ticker,
            price = 100.0f + kotlin.random.Random.nextFloat() * 50,
            change = kotlin.random.Random.nextFloat() * 3 - 1.5f,
            changePercent = kotlin.random.Random.nextFloat() * 3 - 1.5f,
            volume = kotlin.random.Random.nextLong(500000, 5000000)
        )
    }
    
    // CoinGecko - para criptomoedas
    suspend fun getCryptoData(coinId: String): StockData = withContext(Dispatchers.IO) {
        StockData(
            symbol = coinId.uppercase(),
            price = kotlin.random.Random.nextFloat() * 100000,
            change = kotlin.random.Random.nextFloat() * 10 - 5f,
            changePercent = kotlin.random.Random.nextFloat() * 10 - 5f,
            volume = kotlin.random.Random.nextLong(1000000000, 100000000000)
        )
    }
    
    // B3 API - Bolsa brasileira
    suspend fun getB3Data(ticker: String): StockData = withContext(Dispatchers.IO) {
        StockData(
            symbol = ticker,
            price = 10.0f + kotlin.random.Random.nextFloat() * 100,
            change = kotlin.random.Random.nextFloat() * 3 - 1.5f,
            changePercent = kotlin.random.Random.nextFloat() * 3 - 1.5f,
            volume = kotlin.random.Random.nextLong(1000000, 50000000)
        )
    }
    
    // Funcao universal que roteia para API correta
    suspend fun getUniversalData(asset: String): StockData = withContext(Dispatchers.Default) {
        when {
            asset.uppercase() in listOf("BTC", "ETH", "XRP") -> getCryptoData(asset)
            asset.endsWith("4") || asset.endsWith("3") -> getB3Data(asset)
            asset.uppercase() == "DOLAR" -> getYahooFinanceData("USDBRL=X")
            else -> getYahooFinanceData(asset)
        }
    }
}
