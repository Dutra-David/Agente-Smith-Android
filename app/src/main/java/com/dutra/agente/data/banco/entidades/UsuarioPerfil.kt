package com.dutra.agente.dados.banco.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * User Profile Entity - Room Database
 * Stores comprehensive user psychological and behavioral data for empathy-based interactions
 */
@Entity(tableName = "usuario_perfil")
data class UsuarioPerfil(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Personal Information
    val nome: String,
    val genero: String? = null, // MASCULINO, FEMININO, OUTRO
    val idade: Int? = null,
    val dataRegistro: LocalDateTime = LocalDateTime.now(),
    
    // Psychological Profile
    val personalidadeTipo: String? = null, // MBTI tipo (INTJ, ENFP, etc.)
    val nomeEmocionalAtual: String = "neutro", // Current emotional state
    val nivelEstresse: Float = 0.5f, // 0-1 scale
    val nivelAnsiedade: Float = 0.5f, // 0-1 scale
    
    // Interaction Preferences
    val idiomaPreferido: String = "pt-BR",
    val tomusuarioclareComumicacao: String = "formal", // formal, informal, amigavel
    val velocidadeLeitura: Float = 1.0f, // 0.5 - 2.0 (slow to fast)
    
    // Behavioral Data
    val totalInteracoes: Int = 0,
    val tempoMedioInteracao: Long = 0, // in milliseconds
    val topicos Favoritos: String? = null, // JSON array
    val objetivosUsuario: String? = null, // JSON array
    
    // Health & Wellness
    val horasSono: Float? = null,
    val nivelAtividadeFisica: Float? = null, // 0-1 scale
    val registroHumorDiario: String? = null, // JSON history
    
    // Preferences & Settings
    val notificacoesAtivas: Boolean = true,
    val modoEscuro: Boolean = false,
    val ultimaAtualizacao: LocalDateTime = LocalDateTime.now()
)
