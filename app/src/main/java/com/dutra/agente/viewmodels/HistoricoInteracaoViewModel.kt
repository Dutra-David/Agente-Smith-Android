package com.dutra.agente.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutra.agente.dados.banco.entidades.HistoricoInteracao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gerenciar histórico de interações
 * Responsabilidades: Métricas comportamentais, engajamento, impacto psicológico
 */
@HiltViewModel
class HistoricoInteracaoViewModel @Inject constructor() : ViewModel() {
    
    private val _interacoes = MutableLiveData<List<HistoricoInteracao>>(emptyList())
    val interacoes: LiveData<List<HistoricoInteracao>> = _interacoes
    
    private val _engajamentoMedio = MutableLiveData<Float>(0f)
    val engajamentoMedio: LiveData<Float> = _engajamentoMedio
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    fun carregarInteracoes(usuarioId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
            }
        }
    }
    
    fun adicionarInteracao(interacao: HistoricoInteracao) {
        viewModelScope.launch {
            try {
                val lista = _interacoes.value?.toMutableList() ?: mutableListOf()
                lista.add(interacao)
                _interacoes.value = lista
                calcularEngajamento(lista)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    private fun calcularEngajamento(interacoes: List<HistoricoInteracao>) {
        if (interacoes.isEmpty()) return
        val media = interacoes.map { it.nivelEngajamento }.average().toFloat()
        _engajamentoMedio.value = media
    }
    
    fun limparErro() {
        _errorMessage.value = null
    }
}
