package com.dutra.agente.domain

/**
 * IMPROVEMENT 5: Intelligent Error Handling
 * Provides robust error recovery and logging
 */
data class ErrorLog(
    val timestamp: Long,
    val errorType: String,
    val message: String,
    val stackTrace: String?
)

class ErrorHandler {
    
    private val errorLogs = mutableListOf<ErrorLog>()
    private val maxLogs = 500
    
    fun handleError(errorType: String, message: String, exception: Exception? = null) {
        val log = ErrorLog(
            timestamp = System.currentTimeMillis(),
            errorType = errorType,
            message = message,
            stackTrace = exception?.stackTraceToString()
        )
        
        synchronized(errorLogs) {
            errorLogs.add(log)
            if (errorLogs.size > maxLogs) {
                errorLogs.removeAt(0)
            }
        }
        
        println("[ERROR] $errorType: $message")
    }
    
    fun getErrorStats(): String {
        return "Total Errors: ${errorLogs.size}"
    }
    
    fun clearLogs() {
        errorLogs.clear()
    }
}
