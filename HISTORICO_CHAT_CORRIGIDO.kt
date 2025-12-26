package com.dutra.agente.data.banco.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade para armazenar historico de mensagens do chat
 * Tabela: historico_chat
 * 
 * Campos:
 * - id: Identificador unico da mensagem
 * - usuarioId: ID do usuario que enviou
 * - mensagem: Conteudo da mensagem
 * - dataHora: Data e hora da mensagem
 * - tipoEmocao: Tipo de emocao detectada na mensagem
 */
@Entity(tableName = "historico_chat")
data class HistoricoChat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val usuarioId: Int,
    
    val mensagem: String,
    
    val dataHora: String,
    
    val tipoEmocao: String? = null
)

/**
 * Constantes da entidade (se necessario usar em queries)
 */
object HistoricoChatConstants {
    const val TABLE_NAME = "historico_chat"
    const val COLUMN_ID = "id"
    const val COLUMN_USUARIO_ID = "usuarioId"
    const val COLUMN_MENSAGEM = "mensagem"
    const val COLUMN_DATA_HORA = "dataHora"
    const val COLUMN_TIPO_EMOCAO = "tipoEmocao"
}
