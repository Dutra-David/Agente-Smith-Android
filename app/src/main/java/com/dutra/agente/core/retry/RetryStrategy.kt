package com.dutra.agente.core.retry

import kotlin.math.min
import kotlin.random.Random
import kotlinx.coroutines.delay

/**
 * Define estratégias de retry com suporte a exponential backoff e jitter
 */
sealed class RetryStrategy {
    /**
     * Estratégia de Exponential Backoff com Jitter
     */
    data class Exponential(
        val initialDelayMs: Long = 100L,
        val maxDelayMs: Long = 30_000L,
        val multiplier: Double = 2.0,
        val jitter: Boolean = true
    ) : RetryStrategy()
}

/**
 * Resultado de uma operação com retry
 */
sealed class RetryResult<T> {
    data class Success<T>(val value: T, val attempts: Int) : RetryResult<T>()
    data class Failure<T>(val exception: Exception, val attempts: Int) : RetryResult<T>()
}

/**
 * Executa um bloco de código com retry logic
 */
suspend fun <T> executeWithRetry(
    maxRetries: Int = 3,
    strategy: RetryStrategy = RetryStrategy.Exponential(),
    block: suspend () -> T
): RetryResult<T> {
    var lastException: Exception? = null

    repeat(maxRetries) { attempt ->
        try {
            val result = block()
            return RetryResult.Success(result, attempt + 1)
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxRetries - 1) {
                val delayMs = calculateDelay(strategy, attempt)
                delay(delayMs)
            }
        }
    }

    return RetryResult.Failure(lastException ?: Exception("Unknown error"), maxRetries)
}

private fun calculateDelay(strategy: RetryStrategy, attempt: Int): Long {
    return when (strategy) {
        is RetryStrategy.Exponential -> {
            var delay = (strategy.initialDelayMs * Math.pow(strategy.multiplier, attempt.toDouble())).toLong()
            delay = min(delay, strategy.maxDelayMs)
            if (strategy.jitter) {
                val jitterAmount = (delay * 0.1).toLong()
                delay += Random.nextLong(-jitterAmount, jitterAmount)
                delay = maxOf(strategy.initialDelayMs, delay)
            }
            delay
        }
    }
}
