package com.dutra.agente.data.banco.entidades
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Chat History Entity - Room Database
 * Stores all chat interactions for context and learning
 */
@Entity(
    tableName = "historico_chat",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioPerfil::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HistoricoChat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val usuarioId: Long,
    val mensagemUsuario: String,
    val respostaAgente: String,
    val dataHora: LocalDateTime = LocalDateTime.now(),
    
    // Emotional Context
    val estadoEmocionalUsuario: String? = null, // Detected emotion
    val nivelSatisfacao: Int? = null, // 1-5 rating
    val topico: String? = null, // Topic being discussed
    
    // Interaction Quality
    val tempoResposta: Long = 0, // milliseconds
    val efetivoParaUsuario: Boolean = false,
    val requereFollowUp: Boolean = false,
    
    // Metadata
    val contextoAnterior: String? = null, // Reference to previous message
    val versaoModelo: String = "1.0",
    val tags: String? = null // JSON array
)
