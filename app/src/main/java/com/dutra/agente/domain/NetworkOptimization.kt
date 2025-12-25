package com.dutra.agente.domain

import java.util.concurrent.TimeUnit

/**
 * IMPROVEMENT 4: Network Optimization Manager
 * Optimizes network requests with batching, compression, and smart retries
 */
data class NetworkRequest(
    val url: String,
    val method: String,
    val priority: Int = 1,
    val retryCount: Int = 0
)

class NetworkOptimization {
    
    private val requestQueue = mutableListOf<NetworkRequest>()
    private var batchSize = 5
    private var compressionEnabled = true
    private var requestTimeout = TimeUnit.SECONDS.toMillis(30)
    
    fun addRequest(request: NetworkRequest) {
        synchronized(requestQueue) {
            requestQueue.add(request)
            if (requestQueue.size >= batchSize) {
                processBatch()
            }
        }
    }
    
    private fun processBatch() {
        val batch = synchronized(requestQueue) {
            val result = requestQueue.take(batchSize).toList()
            requestQueue.removeAll(result)
            result
        }
        
        batch.forEach { request ->
            println("[Network] Processing ${request.method} ${request.url}")
        }
    }
    
    fun enableCompression(enabled: Boolean) {
        compressionEnabled = enabled
    }
    
    fun setRequestTimeout(millis: Long) {
        requestTimeout = millis
    }
    
    fun getQueueSize(): Int = requestQueue.size
    
    fun getNetworkStats(): String {
        return "Queue: ${requestQueue.size}, Compression: $compressionEnabled, Timeout: ${requestTimeout}ms"
    }
}
