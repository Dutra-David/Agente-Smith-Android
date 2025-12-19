# ROOM DATABASE GUIDE - Agente Smith Android

## Overview
This guide documents the Room Database implementation for the Agente Smith Android application, providing local data persistence for user profiles and interaction history.

## Database Architecture

### Entities (Tabelas)

#### 1. UsuarioPerfil (User Profile)
Stores comprehensive user psychological and behavioral data.

**Fields:**
- `id`: Primary key (auto-generated)
- `nome`: User name
- `genero`: Gender (MASCULINO, FEMININO, OUTRO)
- `idade`: Age
- `personalidadeTipo`: MBTI personality type
- `nomeEmocionalAtual`: Current emotional state
- `nivelEstresse`: Stress level (0-1 scale)
- `nivelAnsiedade`: Anxiety level (0-1 scale)
- `idiomaPreferido`: Preferred language (pt-BR default)
- `tomusuarioclareComumicacao`: Communication tone (formal/informal/friendly)
- `totalInteracoes`: Total interactions count
- `tempoMedioInteracao`: Average interaction time
- `horasSono`: Sleep hours
- `nivelAtividadeFisica`: Physical activity level
- `notificacoesAtivas`: Notifications enabled

#### 2. HistoricoChat (Chat History)
Tracks all chat interactions with emotional context.

**Fields:**
- `id`: Primary key
- `usuarioId`: Foreign key to UsuarioPerfil
- `mensagemUsuario`: User message
- `respostaAgente`: Agent response
- `dataHora`: Interaction timestamp
- `estadoEmocionalUsuario`: Detected emotion
- `nivelSatisfacao`: User satisfaction (1-5 rating)
- `topico`: Discussion topic
- `tempoResposta`: Response time (ms)
- `efetivoParaUsuario`: Effectiveness flag
- `requereFollowUp`: Follow-up required flag

#### 3. HistoricoInteracao (Interaction History)
Comprehensive interaction metrics and behavioral patterns.

**Fields:**
- `id`: Primary key
- `usuarioId`: Foreign key to UsuarioPerfil
- `tipoInteracao`: Type (CHAT, CONSULTA, FEEDBACK, CONFIGURACAO)
- `categoria`: Topic category
- `nivelEngajamento`: Engagement level (0-1)
- `emocaoDetectada`: Detected emotion
- `impactoPositivo`: Positive impact flag
- `nivelSatisfacaoPos`: Post-interaction satisfaction
- `qualidadeResposta`: Response quality (0-1)
- `empatiaDetectada`: Empathy detected flag
- `requereAcompanhamento`: Follow-up required flag
- `statusAcompanhamento`: Follow-up status

## Data Access Objects (DAOs)

### UsuarioPerfilDao
**Operations:**
- `inserir()`: Insert new user profile
- `atualizar()`: Update user profile
- `obterPorId()`: Get profile by ID
- `obterUltimo()`: Get most recent profile
- `atualizarEstadoPsicologico()`: Update emotional state
- `incrementarContadorInteracoes()`: Increment interaction counter

### HistoricoChatDao
**Operations:**
- `inserir()`: Insert chat message
- `obterPorUsuario()`: Get chat history for user
- `obterSatisfacaoMedia()`: Get average satisfaction
- `contarPorUsuario()`: Count messages per user
- `deletarAntigosAlAutos()`: Delete old messages (90+ days)

### HistoricoInteracaoDao
**Operations:**
- `inserir()`: Insert interaction
- `obterPorUsuario()`: Get interactions by user
- `obterPorTipo()`: Get interactions by type
- `obterEngajamentoMedio()`: Get average engagement
- `contarResolvidos()`: Count resolved interactions
- `atualizarStatus()`: Update follow-up status

## Database Configuration

### AppDatabase.kt
Central Room database configuration using singleton pattern.

**Features:**
- Singleton instance management
- Thread-safe database access
- Automatic schema fallback migration
- All DAOs accessible through single instance

**Usage:**
```kotlin
val db = AppDatabase.getInstance(context)
val usuarioDao = db.usuarioPerfilDao()
val chatDao = db.historicoChatDao()
val interacaoDao = db.historicoInteracaoDao()
```

## Database Schema

- **Database Name:** agente_smith_database
- **Version:** 1
- **Tables:** 3 (usuario_perfil, historico_chat, historico_interacao)
- **Foreign Keys:** Enabled (CASCADE delete)

## Psychological Data Integration

The Room Database stores comprehensive psychological data for empathy-based interactions:
- Emotional state tracking
- Stress and anxiety monitoring
- Satisfaction metrics
- Engagement levels
- Behavioral patterns

## Next Steps (Fase 2 & 3)

1. **MVVM Integration:** Create ViewModels for UI binding
2. **Security Enhancement:** Add encryption for sensitive data
3. **Query Optimization:** Implement complex queries for analytics
4. **Offline Sync:** Implement data sync with backend API
