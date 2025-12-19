package com.dutra.agente.dados.banco.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dutra.agente.dados.banco.entidades.UsuarioPerfil

/**
 * Data Access Object for UsuarioPerfil entity
 * Handles all database operations related to user profiles
 */
@Dao
interface UsuarioPerfilDao {
    
    @Insert
    suspend fun inserir(usuario: UsuarioPerfil): Long
    
    @Update
    suspend fun atualizar(usuario: UsuarioPerfil)
    
    @Query("SELECT * FROM usuario_perfil WHERE id = :id")
    suspend fun obterPorId(id: Long): UsuarioPerfil?
    
    @Query("SELECT * FROM usuario_perfil ORDER BY dataRegistro DESC LIMIT 1")
    suspend fun obterUltimo(): UsuarioPerfil?
    
    @Query("SELECT * FROM usuario_perfil ORDER BY totalInteracoes DESC LIMIT :limite")
    suspend fun obterTodosOrdenadosPorInteracoes(limite: Int = 100): List<UsuarioPerfil>
    
    @Query("UPDATE usuario_perfil SET nomeEmocionalAtual = :emocao, nivelEstresse = :estresse, nivelAnsiedade = :ansiedade WHERE id = :id")
    suspend fun atualizarEstadoPsicologico(id: Long, emocao: String, estresse: Float, ansiedade: Float)
    
    @Query("UPDATE usuario_perfil SET totalInteracoes = totalInteracoes + 1, ultimaAtualizacao = datetime('now') WHERE id = :id")
    suspend fun incrementarContadorInteracoes(id: Long)
    
    @Query("SELECT COUNT(*) FROM usuario_perfil")
    suspend fun contar(): Int
}
