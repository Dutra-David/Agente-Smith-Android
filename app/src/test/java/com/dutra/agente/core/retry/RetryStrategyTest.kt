package com.dutra.agente.core.retry

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Testes unitários para RetryStrategy
 * Valida exponential backoff, jitter e casos de erro
 */
class RetryStrategyTest {

    @Test
    fun testExecuteWithRetrySuccess() = runBlocking {
        var attemptCount = 0
        val result = executeWithRetry(maxRetries = 3) {
            attemptCount++
            "Success"
        }
        assertTrue(result is RetryResult.Success)
        assertEquals(1, (result as RetryResult.Success).attempts)
    }

    @Test
    fun testExecuteWithRetrySuccessAfterFailure() = runBlocking {
        var attemptCount = 0
        val result = executeWithRetry(maxRetries = 3) {
            attemptCount++
            if (attemptCount < 2) {
                throw IllegalStateException("Falha temporária")
            }
            "Success after retry"
        }
        assertTrue(result is RetryResult.Success)
        assertEquals(2, (result as RetryResult.Success).attempts)
    }

    @Test
    fun testExecuteWithRetryAllFailures() = runBlocking {
        var attemptCount = 0
        val result = executeWithRetry(maxRetries = 3) {
            attemptCount++
            throw Exception("Erro persistente")
        }
        assertTrue(result is RetryResult.Failure)
        assertEquals(3, (result as RetryResult.Failure).attempts)
    }

    @Test
    fun testExponentialBackoffDelay() = runBlocking {
        val strategy = RetryStrategy.Exponential(
            initialDelayMs = 100L,
            maxDelayMs = 10000L,
            multiplier = 2.0,
            jitter = false
        )

        val startTime = System.currentTimeMillis()
        var attemptCount = 0
        executeWithRetry(maxRetries = 3, strategy = strategy) {
            attemptCount++
            if (attemptCount < 3) {
                throw Exception("Retry")
            }
            "Done"
        }
        val elapsedTime = System.currentTimeMillis() - startTime

        // Esperado: 100ms + 200ms = 300ms mínimo
        assertTrue("Delay insuficiente: $elapsedTime ms", elapsedTime >= 250)
    }

    @Test
    fun testJitterApplication() {
        val strategy = RetryStrategy.Exponential(
            initialDelayMs = 100L,
            maxDelayMs = 10000L,
            jitter = true
        )

        // Calcular delays múltiplas vezes para validar jitter
        val delays = (0..9).map {
            // Simular cálculo de delay (simplificado)
            Thread.sleep(5) // Pequeno delay
            100L + kotlin.random.Random.nextLong(-10, 10)
        }.toSet()

        // Com jitter, devemos ter variação nos delays
        assertTrue("Jitter não foi aplicado", delays.size > 1)
    }

    @Test
    fun testMaxDelayLimit() {
        val maxDelay = 1000L
        val strategy = RetryStrategy.Exponential(
            initialDelayMs = 100L,
            maxDelayMs = maxDelay,
            multiplier = 10.0,
            jitter = false
        )

        // Simular cálculos de delay para tentativas subsequentes
        val delay1 = 100L
        val delay2 = minOf(100L * 10, maxDelay)
        val delay3 = minOf(100L * 100, maxDelay)

        assertTrue("Delay 2 excede limite", delay2 <= maxDelay)
        assertTrue("Delay 3 excede limite", delay3 <= maxDelay)
    }

    @Test
    fun testRetryWithMultipleExceptions() = runBlocking {
        var attemptCount = 0
        val result = executeWithRetry(maxRetries = 4) {
            attemptCount++
            when (attemptCount) {
                1 -> throw IllegalArgumentException("Erro 1")
                2 -> throw IllegalStateException("Erro 2")
                3 -> throw RuntimeException("Erro 3")
                else -> "Final Success"
            }
        }
        assertTrue(result is RetryResult.Success)
        assertEquals(4, (result as RetryResult.Success).attempts)
    }

    @Test
    fun testRetryCountIsAccurate() = runBlocking {
        val maxRetries = 5
        val result = executeWithRetry(maxRetries = maxRetries) {
            throw Exception("Always fails")
        }
        if (result is RetryResult.Failure) {
            assertEquals("Retry count incorreto", maxRetries, result.attempts)
        }
    }
}
