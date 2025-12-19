package com.dutra.agente.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutra.agente.dados.banco.entidades.HistoricoChat
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar histórico de chat
 * Responsabilidades: Gerenciar conversas, estado emocional, métricas de satisfação
 */
class HistoricoChatViewModel : ViewModel() {
    
    // LiveData para lista de mensagens
    private val _chatHistory = MutableLiveData<List<HistoricoChat>>(emptyList())
    val chatHistory: LiveData<List<HistoricoChat>> = _chatHistory
    
    // LiveData para mensagem atual
    private val _currentMessage = MutableLiveData<String>()
    val currentMessage: LiveData<String> = _currentMessage
    
    // LiveData para estado de carregamento
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData para mensagens de erro
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // LiveData para satisfação média
    private val _satisfacaoMedia = MutableLiveData<Float>(0f)
    val satisfacaoMedia: LiveData<Float> = _satisfacaoMedia
    
    // LiveData para contagem de mensagens
    private val _totalMensagens = MutableLiveData<Int>(0)
    val totalMensagens: LiveData<Int> = _totalMensagens
    
    /**
     * Carrega histórico de chat para um usuário
     */
    fun carregarHistorico(usuarioId: Long, limite: Int = 100) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                // Simular carregamento do banco
                // Em implementação real, usar repository
                _chatHistory.value = emptyList()
                _totalMensagens.value = 0
                _isLoading.value = false
                
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Erro ao carregar histórico: ${e.message}"
            }
        }
    }
    
    /**
     * Adiciona nova mensagem ao histórico
     */
    fun adicionarMensagem(
        usuarioId: Long,
        mensagemUsuario: String,
        respostaAgente: String,
        estadoEmocional: String,
        nivelSatisfacao: Int
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Criar nova mensagem
                val novaMsg = HistoricoChat(
                    usuarioId = usuarioId,
                    mensagemUsuario = mensagemUsuario,
                    respostaAgente = respostaAgente,
                    estadoEmocionalUsuario = estadoEmocional,
                    nivelSatisfacao = nivelSatisfacao
                )
                
                // Adicionar à lista
                val listaAtual = _chatHistory.value?.toMutableList() ?: mutableListOf()
                listaAtual.add(novaMsg)
                _chatHistory.value = listaAtual
                
                // Atualizar total
                _totalMensagens.value = listaAtual.size
                
                // Atualizar satisfação média
                calcularSatisfacaoMedia(listaAtual)
                
                _currentMessage.value = ""
                _isLoading.value = false
                
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Erro ao adicionar mensagem: ${e.message}"
            }
        }
    }
    
    /**
     * Atualiza mensagem atual no editor
     */
    fun atualizarMensagemAtual(msg: String) {
        _currentMessage.value = msg
    }
    
    /**
     * Calcula satisfação média
     */
    private fun calcularSatisfacaoMedia(mensagens: List<HistoricoChat>) {
        if (mensagens.isEmpty()) {
            _satisfacaoMedia.value = 0f
            return
        }
        
        val somaSatisfacao = mensagens
            .mapNotNull { it.nivelSatisfacao }
            .sum()
        
        val media = somaSatisfacao.toFloat() / mensagens.size
        _satisfacaoMedia.value = media
    }
    
    /**
     * Limpa histórico de chat
     */
    fun limparHistorico() {
        viewModelScope.launch {
            try {
                _chatHistory.value = emptyList()
                _totalMensagens.value = 0
                _satisfacaoMedia.value = 0f
                _currentMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao limpar histórico: ${e.message}"
            }
        }
    }
    
    /**
     * Limpa mensagens de erro
     */
    fun limparErro() {
        _errorMessage.value = null
    }
}
