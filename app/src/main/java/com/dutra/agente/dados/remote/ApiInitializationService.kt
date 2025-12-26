package com.dutra.agente.dados.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serviço para inicialização da API com fallback para modo mock
 * Evita travamentos da aplicação durante o carregamento
 */
@Singleton
class ApiInitializationService @Inject constructor() {
    private val TAG = "ApiInitService"
    private var isInitialized = false
    private var isMockMode = false

    suspend fun initialize(): ApiInitResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando API...")
            
            // Tentar inicializar API real
            if (tryInitializeRealApi()) {
                isInitialized = true
                isMockMode = false
                Log.d(TAG, "API real inicializada com sucesso")
                return@withContext ApiInitResult.Success()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao inicializar API real: ${e.message}")
        }

        // Fallback para modo mock
        try {
            Log.d(TAG, "Ativando modo mock...")
            initializeMockApi()
            isInitialized = true
            isMockMode = true
            Log.d(TAG, "Modo mock ativado com sucesso")
            return@withContext ApiInitResult.SuccessWithMock("API em modo mock")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao ativar modo mock: ${e.message}", e)
            return@withContext ApiInitResult.Error("Falha na inicialização: ${e.message}")
        }
    }

    private suspend fun tryInitializeRealApi(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // Aqui você implementaria a lógica de inicialização da API real
            // Por exemplo: conectar ao servidor, verificar conexão, etc.
            // timeout de 5 segundos para evitar travamento
            
            // Se houver RetrofitClient ou similar:
            // RetrofitClient.initialize()
            // RetrofitClient.testConnection()
            
            // Simulação:
            true // Retornar true se conseguir inicializar
        } catch (e: Exception) {
            Log.e(TAG, "Erro na API real", e)
            false
        }
    }

    private fun initializeMockApi() {
        // Inicializar respostas mock
        Log.d(TAG, "Configurando respostas mock...")
        // Aqui você pode configurar Interceptors do Retrofit ou MockWebServer para testes
    }

    fun isInMockMode(): Boolean = isMockMode
    fun isReady(): Boolean = isInitialized
}

seal class ApiInitResult {
    class Success : ApiInitResult()
    data class SuccessWithMock(val message: String) : ApiInitResult()
    data class Error(val message: String) : ApiInitResult()
}
