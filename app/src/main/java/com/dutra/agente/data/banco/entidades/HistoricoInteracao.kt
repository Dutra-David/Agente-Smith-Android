package com.dutra.agente.dados.banco.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Interaction History Entity - Room Database
 * Tracks comprehensive interaction metrics and behavioral patterns
 */
@Entity(
    tableName = "historico_interacao",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioPerfil::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HistoricoInteracao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val usuarioId: Long,
    val dataHora: LocalDateTime = LocalDateTime.now(),
    val duracao: Long, // milliseconds
    
    // Interaction Type
    val tipoInteracao: String, // CHAT, CONSULTA, FEEDBACK, CONFIGURACAO
    val categoria: String? = null, // Topic category
    
    // Emotional & Behavioral Metrics
    val emocaoDetectada: String? = null,
    val nivelEngajamento: Float = 0.5f, // 0-1 scale
    val tentativasResolucao: Int = 1,
    val resolvido: Boolean = false,
    
    // Psychological Impact
    val impactoPositivo: Boolean? = null,
    val nivelSatisfacaoPos: Int? = null, // 1-5 post-interaction
    val mudancaNivelEstresse: Float = 0f, // delta
    val mudancaNivelAnsiedade: Float = 0f, // delta
    
    // Quality Metrics
    val qualidadeResposta: Float? = null, // 0-1 scale
    val relevancia: Float? = null, // 0-1 scale
    val empatiaDetectada: Boolean = false,
    
    // Learning Data
    val palavrasChave: String? = null, // JSON array
    val padroesComportamento: String? = null, // JSON
    val areasMelhoria: String? = null, // JSON array
    
    // Follow-up
    val requereAcompanhamento: Boolean = false,
    val sugestaoAcompanhamento: String? = null,
    val statusAcompanhamento: String = "PENDENTE" // PENDENTE, EM_PROGRESSO, CONCLUIDO
)
