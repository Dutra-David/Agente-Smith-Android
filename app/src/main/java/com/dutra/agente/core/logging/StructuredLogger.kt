package com.dutra.agente.core.logging

import android.util.Log

/**
 * Eventos de log estruturado para observabilidade
 */
sealed class LogEvent {
    val timestamp: Long = System.currentTimeMillis()
    
    /**
     * Evento de sincronização com detalhes de performance
     */
    data class Sync(
        val itemCount: Int,
        val duration: Long,
        val success: Boolean,
        val errorMessage: String? = null
    ) : LogEvent()
    
    /**
     * Detecção de anomalia
     */
    data class Anomaly(
        val severity: String, // LOW, MEDIUM, HIGH
        val message: String,
        val details: Map<String, String> = emptyMap()
    ) : LogEvent()
    
    /**
     * Erro capturado
     */
    data class Error(
        val exception: Throwable,
        val context: String,
        val fatal: Boolean = false
    ) : LogEvent()
    
    /**
     * Rastreamento de performance
     */
    data class Performance(
        val name: String,
        val durationMs: Long,
        val memoryMb: Float = 0f
    ) : LogEvent()
    
    /**
     * Log genérico
     */
    data class Info(
        val message: String,
        val level: String = "INFO"
    ) : LogEvent()
}

/**
 * Interface para logging estruturado
 */
interface StructuredLogger {
    fun log(event: LogEvent)
    fun logSync(itemCount: Int, duration: Long, success: Boolean, error: String? = null)
    fun logAnomaly(severity: String, message: String, details: Map<String, String> = emptyMap())
    fun logError(exception: Throwable, context: String, fatal: Boolean = false)
    fun logPerformance(name: String, durationMs: Long)
    fun logInfo(message: String)
}

/**
 * Implementação padrão usando Android Log
 */
class AndroidStructuredLogger(
    private val tag: String = "AgentSmith"
) : StructuredLogger {
    
    override fun log(event: LogEvent) {
        when (event) {
            is LogEvent.Sync -> {
                val msg = if (event.success) {
                    "SYNC: ${event.itemCount} items em ${event.duration}ms"
                } else {
                    "SYNC FALHOU: ${event.errorMessage}"
                }
                Log.d(tag, msg)
            }
            is LogEvent.Anomaly -> {
                val level = when (event.severity) {
                    "HIGH" -> Log.e
                    "MEDIUM" -> Log.w
                    else -> Log::d
                }
                level(tag, "ANOMALIA [${event.severity}]: ${event.message}")
            }
            is LogEvent.Error -> {
                val level = if (event.fatal) Log.e else Log::w
                level(tag, "ERROR [${event.context}]: ${event.exception.message}")
                event.exception.printStackTrace()
            }
            is LogEvent.Performance -> {
                Log.d(tag, "PERF: ${event.name} = ${event.durationMs}ms")
            }
            is LogEvent.Info -> {
                Log.i(tag, event.message)
            }
        }
    }
    
    override fun logSync(itemCount: Int, duration: Long, success: Boolean, error: String?) {
        log(LogEvent.Sync(itemCount, duration, success, error))
    }
    
    override fun logAnomaly(severity: String, message: String, details: Map<String, String>) {
        log(LogEvent.Anomaly(severity, message, details))
    }
    
    override fun logError(exception: Throwable, context: String, fatal: Boolean) {
        log(LogEvent.Error(exception, context, fatal))
    }
    
    override fun logPerformance(name: String, durationMs: Long) {
        log(LogEvent.Performance(name, durationMs))
    }
    
    override fun logInfo(message: String) {
        log(LogEvent.Info(message))
    }
}

/**
 * Implementação para testes
 */
class TestStructuredLogger : StructuredLogger {
    val logs = mutableListOf<LogEvent>()
    
    override fun log(event: LogEvent) {
        logs.add(event)
    }
    
    override fun logSync(itemCount: Int, duration: Long, success: Boolean, error: String?) {
        log(LogEvent.Sync(itemCount, duration, success, error))
    }
    
    override fun logAnomaly(severity: String, message: String, details: Map<String, String>) {
        log(LogEvent.Anomaly(severity, message, details))
    }
    
    override fun logError(exception: Throwable, context: String, fatal: Boolean) {
        log(LogEvent.Error(exception, context, fatal))
    }
    
    override fun logPerformance(name: String, durationMs: Long) {
        log(LogEvent.Performance(name, durationMs))
    }
    
    override fun logInfo(message: String) {
        log(LogEvent.Info(message))
    }
    
    fun clear() = logs.clear()
    fun getLastEvent(): LogEvent? = logs.lastOrNull()
    fun getEventCount(): Int = logs.size
}
