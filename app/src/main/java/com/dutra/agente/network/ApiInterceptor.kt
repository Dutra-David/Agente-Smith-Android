package com.dutra.agente.network

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * ApiInterceptor - Interceptador de requisicoes HTTP
 *
 * Responsabilidades:
 * - Logging de requisicoes e respostas
 * - Retry automatico com backoff exponencial
 * - Adicionar headers customizados
 * - Tratar timeouts e erros de conexao
 * - Monitorar latencia de requisicoes
 */
class ApiInterceptor : Interceptor {

    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 1000L
        private const val MAX_DELAY_MS = 10000L
        private const val BACKOFF_MULTIPLIER = 2.0
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var delayMs = INITIAL_DELAY_MS
        var lastException: IOException? = null

        while (attempt < MAX_RETRIES) {
            try {
                val originalRequest = chain.request()
                
                // Log da requisicao
                Timber.d("[${originalRequest.url}] Tentativa ${attempt + 1}/$MAX_RETRIES")
                val startTime = System.currentTimeMillis()

                val requestWithHeaders = originalRequest.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "Agente-Smith-Android/1.0")
                    .build()

                val response = chain.proceed(requestWithHeaders)
                val duration = System.currentTimeMillis() - startTime

                // Log da resposta
                Timber.i("[${response.request.url}] Status: ${response.code} (${duration}ms)")

                if (response.isSuccessful) {
                    return response
                } else if (response.code == 408 || response.code == 429 || response.code >= 500) {
                    // Retry em caso de timeout, rate limit ou erro servidor
                    response.close()
                    if (attempt < MAX_RETRIES - 1) {
                        Timber.w("Erro ${response.code}, aguardando ${delayMs}ms para retry...")
                        Thread.sleep(delayMs)
                        delayMs = (delayMs * BACKOFF_MULTIPLIER).toLong().coerceAtMost(MAX_DELAY_MS)
                        attempt++
                        continue
                    }
                }
                return response
            } catch (e: IOException) {
                lastException = e
                if (attempt < MAX_RETRIES - 1) {
                    Timber.w(e, "Erro de conexao na tentativa ${attempt + 1}, retry em ${delayMs}ms...")
                    try {
                        Thread.sleep(delayMs)
                    } catch (interruptedException: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw e
                    }
                    delayMs = (delayMs * BACKOFF_MULTIPLIER).toLong().coerceAtMost(MAX_DELAY_MS)
                    attempt++
                } else {
                    Timber.e(e, "Erro na tentativa ${attempt + 1}/$MAX_RETRIES, desistindo...")
                    throw e
                }
            }
        }

        throw lastException ?: IOException("Falha apos $MAX_RETRIES tentativas")
    }
}
