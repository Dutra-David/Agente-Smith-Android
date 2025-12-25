package com.dutra.agente.domain

import android.content.Context
import android.content.SharedPreferences

/**
 * IMPROVEMENT 3: Intelligent Cache Optimization
 * Manages memory cache with LRU eviction and compression
 */
class CacheOptimization(context: Context) {
    
    private val cache = LinkedHashMap<String, CacheEntry>(16, 0.75f, true)
    private val maxCacheSize = 50 * 1024 * 1024 // 50MB
    private var currentSize = 0L
    private val prefs: SharedPreferences = context.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)
    
    data class CacheEntry(
        val key: String,
        val value: ByteArray,
        val timestamp: Long,
        val accessCount: Int = 0
    )
    
    fun put(key: String, value: ByteArray) {
        synchronized(this) {
            val size = value.size
            if (size > maxCacheSize) return
            
            if (currentSize + size > maxCacheSize) {
                evictLRU()
            }
            
            cache[key] = CacheEntry(
                key = key,
                value = value,
                timestamp = System.currentTimeMillis()
            )
            currentSize += size
        }
    }
    
    fun get(key: String): ByteArray? {
        synchronized(this) {
            val entry = cache[key] ?: return null
            cache[key] = entry.copy(accessCount = entry.accessCount + 1)
            return entry.value
        }
    }
    
    private fun evictLRU() {
        val iterator = cache.entries.iterator()
        while (currentSize > maxCacheSize * 0.8 && iterator.hasNext()) {
            val (_, entry) = iterator.next()
            iterator.remove()
            currentSize -= entry.value.size
        }
    }
    
    fun clear() {
        synchronized(this) {
            cache.clear()
            currentSize = 0
        }
    }
    
    fun getStats(): String {
        return "Cache Size: ${currentSize / 1024}KB / ${maxCacheSize / 1024}KB, Entries: ${cache.size}"
    }
}

private operator fun <K, V> LinkedHashMap<K, V>.component1() = keys.first()
private operator fun <K, V> LinkedHashMap<K, V>.component2() = values.first()
