package com.dutra.agente.data.banco.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade: Perfil do Usuario
 * Tabela: usuario_perfil
 * Descricao: Armazena dados do perfil e preferencias do usuario
 */
@Entity(tableName = "usuario_perfil")
data class UsuarioPerfil(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Informacoes Basicas
    val nome: String,
    
    val email: String? = null,
    
    val dataCriacao: String? = null,
    
    // Status
    val ativo: Boolean = true,
    
    // Preferencias
    val preferenciasEmocao: String? = null,
    
    val preferenciasNotificacao: Boolean = true,
    
    // Contexto
    val ultimoAcesso: String? = null,
    
    val versaoAppUltimo: String? = null
)
