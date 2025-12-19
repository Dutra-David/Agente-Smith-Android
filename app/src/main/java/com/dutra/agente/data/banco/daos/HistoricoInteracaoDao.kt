package com.dutra.agente.dados.banco.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dutra.agente.dados.banco.entidades.HistoricoInteracao

@Dao
interface HistoricoInteracaoDao {
    
    @Insert
    suspend fun inserir(interacao: HistoricoInteracao): Long
    
    @Query("SELECT * FROM historico_interacao WHERE usuarioId = :usuarioId ORDER BY dataHora DESC LIMIT :limite")
    suspend fun obterPorUsuario(usuarioId: Long, limite: Int = 50): List<HistoricoInteracao>
    
    @Query("SELECT * FROM historico_interacao WHERE tipoInteracao = :tipo ORDER BY dataHora DESC LIMIT :limite")
    suspend fun obterPorTipo(tipo: String, limite: Int = 50): List<HistoricoInteracao>
    
    @Query("SELECT AVG(nivelEngajamento) FROM historico_interacao WHERE usuarioId = :usuarioId")
    suspend fun obterEngajamentoMedio(usuarioId: Long): Float?
    
    @Query("SELECT COUNT(*) FROM historico_interacao WHERE usuarioId = :usuarioId AND resolvido = 1")
    suspend fun contarResolvidos(usuarioId: Long): Int
    
    @Query("UPDATE historico_interacao SET statusAcompanhamento = :status WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: String)
}
