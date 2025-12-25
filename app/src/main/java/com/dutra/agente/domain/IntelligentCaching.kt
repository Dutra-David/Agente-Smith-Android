package com.dutra.agente.domain

/**
 * IMPROVEMENT 13: Intelligent Caching
 */
class IntelligentCaching {
    private val cache = mutableMapOf<String, CacheData>()
    data class CacheData(val value: Any, val expiry: Long)
    fun cache(key: String, value: Any, ttl: Long = 3600000) {
        cache[key] = CacheData(value, System.currentTimeMillis() + ttl)
    }
}
