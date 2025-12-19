package com.dutra.agente.dados.banco.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dutra.agente.dados.banco.entidades.HistoricoChat

@Dao
interface HistoricoChatDao {
    
    @Insert
    suspend fun inserir(chat: HistoricoChat): Long
    
    @Query("SELECT * FROM historico_chat WHERE usuarioId = :usuarioId ORDER BY dataHora DESC LIMIT :limite")
    suspend fun obterPorUsuario(usuarioId: Long, limite: Int = 100): List<HistoricoChat>
    
    @Query("SELECT * FROM historico_chat WHERE id = :id")
    suspend fun obterPorId(id: Long): HistoricoChat?
    
    @Query("SELECT AVG(nivelSatisfacao) FROM historico_chat WHERE usuarioId = :usuarioId")
    suspend fun obterSatisfacaoMedia(usuarioId: Long): Float?
    
    @Query("SELECT COUNT(*) FROM historico_chat WHERE usuarioId = :usuarioId")
    suspend fun contarPorUsuario(usuarioId: Long): Int
    
    @Query("DELETE FROM historico_chat WHERE dataHora < datetime('now', '-90 days')")
    suspend fun deletarAntigosAlAutos()
}
