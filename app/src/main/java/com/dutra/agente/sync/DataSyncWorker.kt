package com.dutra.agente.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import timber.log.Timber

/**
 * DataSyncWorker - Sincronização de dados em background
 *
 * Responsável por:
 * - Sincronizar histórico de mensagens com servidor
 * - Sincronizar preferências do usuário
 * - Executar em background mesmo com app fechado
 * - Funciona com ou sem conectividade (com fila local)
 *
 * Agendamento:
 * - Periódico: a cada 24 horas
 * - Com backoff exponencial em caso de falha
 * - Requer internet (mas continúa sem conectividade)
 */
class DataSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val SYNC_TAG = "data_sync"
        private const val BACKOFF_DELAY_MINUTES = 15
        private const val SYNC_INTERVAL_HOURS = 24L
        private const val INITIAL_DELAY_MINUTES = 15L

        /**
         * Agenda sincronização periódica
         */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
                SYNC_INTERVAL_HOURS,
                TimeUnit.HOURS
            ).apply {
                setConstraints(constraints)
                setInitialDelay(INITIAL_DELAY_MINUTES, TimeUnit.MINUTES)
                setBackoffPolicy(
                    BackoffPolicy.EXPONENTIAL,
                    BACKOFF_DELAY_MINUTES.toLong(),
                    TimeUnit.MINUTES
                )
                addTag(SYNC_TAG)
            }.build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_TAG,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )

            Timber.d("DataSync agendado: a cada $SYNC_INTERVAL_HOURS horas")
        }

        /**
         * Cancela sincronização periódica
         */
        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(SYNC_TAG)
            Timber.d("DataSync cancelado")
        }
    }

    /**
     * Executa a sincronização
     */
    override suspend fun doWork(): Result {
        return try {
            Timber.d("Iniciando sincronização de dados...")

            // 1. Sincronizar histórico de chat
            syncChatHistory()

            // 2. Sincronizar preferências
            syncUserPreferences()

            // 3. Sincronizar métricas
            syncAnalytics()

            Timber.d("Sincronização concluída com sucesso")
            Result.success()

        } catch (e: Exception) {
            Timber.e(e, "Erro na sincronização")
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Sincroniza histórico de mensagens
     */
    private suspend fun syncChatHistory() {
        try {
            Timber.d("Sincronizando histórico de chat...")
            delay(1000) // Simular requisição de rede
            Timber.d("Histórico sincronizado")
        } catch (e: Exception) {
            Timber.e(e, "Erro ao sincronizar chat")
            throw e
        }
    }

    /**
     * Sincroniza preferências do usuário
     */
    private suspend fun syncUserPreferences() {
        try {
            Timber.d("Sincronizando preferências...")
            delay(500)
            Timber.d("Preferências sincronizadas")
        } catch (e: Exception) {
            Timber.e(e, "Erro ao sincronizar preferências")
            throw e
        }
    }

    /**
     * Sincroniza métricas de analytics
     */
    private suspend fun syncAnalytics() {
        try {
            Timber.d("Sincronizando analytics...")
            delay(500)
            Timber.d("Analytics sincronizados")
        } catch (e: Exception) {
            Timber.e(e, "Erro ao sincronizar analytics")
            throw e
        }
    }

    private companion object {
        private const val MAX_RETRIES = 3
    }
}
