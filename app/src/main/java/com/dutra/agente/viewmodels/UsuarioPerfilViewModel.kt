package com.dutra.agente.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutra.agente.dados.banco.entidades.UsuarioPerfil
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar estado de Perfil do Usuário
 * Responsabilidades: Gerenciar estado da UI, expor dados via LiveData, operações assíncronas
 */
class UsuarioPerfilViewModel : ViewModel() {
    
    // LiveData para perfil do usuário
    private val _usuarioPerfil = MutableLiveData<UsuarioPerfil?>()
    val usuarioPerfil: LiveData<UsuarioPerfil?> = _usuarioPerfil
    
    // LiveData para estado de carregamento
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData para mensagens de erro
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // LiveData para sucesso de operações
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    // Cache em memória
    private var cachedPerfil: UsuarioPerfil? = null
    private var lastLoadTime: Long = 0
    private val CACHE_DURATION = 5 * 60 * 1000 // 5 minutos
    
    /**
     * Carrega perfil do usuário por ID
     */
    fun carregarPerfil(usuarioId: Long) {
        // Verifica cache
        if (isCacheValido() && cachedPerfil?.id == usuarioId) {
            _usuarioPerfil.value = cachedPerfil
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                // Simular carregamento do banco de dados
                // Em implementação real, usar repository
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Erro ao carregar perfil: ${e.message}"
            }
        }
    }
    
    /**
     * Atualiza estado emocional do usuário
     */
    fun atualizarEstadoEmocional(
        usuarioId: Long,
        emocao: String,
        nivelEstresse: Float,
        nivelAnsiedade: Float
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Atualizar estado
                cachedPerfil = cachedPerfil?.copy(
                    nomeEmocionalAtual = emocao,
                    nivelEstresse = nivelEstresse,
                    nivelAnsiedade = nivelAnsiedade
                )
                
                _usuarioPerfil.value = cachedPerfil
                _successMessage.value = "Estado emocional atualizado com sucesso"
                lastLoadTime = System.currentTimeMillis()
                
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao atualizar estado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Incrementa contador de interações
     */
    fun incrementarInteracoes(usuarioId: Long) {
        viewModelScope.launch {
            try {
                cachedPerfil = cachedPerfil?.copy(
                    totalInteracoes = (cachedPerfil?.totalInteracoes ?: 0) + 1
                )
                _usuarioPerfil.value = cachedPerfil
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao atualizar interações: ${e.message}"
            }
        }
    }
    
    /**
     * Atualiza tempo médio de interação
     */
    fun atualizarTempoMedio(usuarioId: Long, novoTempo: Long) {
        viewModelScope.launch {
            try {
                val totalInteracoes = cachedPerfil?.totalInteracoes ?: 1
                val tempoAnterior = cachedPerfil?.tempoMedioInteracao ?: 0L
                
                val tempoMedioNovo = (
                    (tempoAnterior * (totalInteracoes - 1) + novoTempo) / totalInteracoes
                )
                
                cachedPerfil = cachedPerfil?.copy(
                    tempoMedioInteracao = tempoMedioNovo
                )
                _usuarioPerfil.value = cachedPerfil
                
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao atualizar tempo: ${e.message}"
            }
        }
    }
    
    /**
     * Limpa mensagens de erro
     */
    fun limparErro() {
        _errorMessage.value = null
    }
    
    /**
     * Limpa mensagens de sucesso
     */
    fun limparSucesso() {
        _successMessage.value = null
    }
    
    /**
     * Verifica se cache ainda está válido
     */
    private fun isCacheValido(): Boolean {
        val agora = System.currentTimeMillis()
        return (agora - lastLoadTime) < CACHE_DURATION
    }
    
    /**
     * Invalida o cache
     */
    fun invalidarCache() {
        lastLoadTime = 0
        cachedPerfil = null
    }
}
