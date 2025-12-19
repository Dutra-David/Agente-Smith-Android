# 🧠 Guia Completo: Sistema de Empatia e Psicologia - Agente Smith

## 📋 Visão Geral

Este documento descreve o sistema de **Empatia e Psicologia Humana** integrado ao Agente Smith Android. O objetivo é tornar o agente mais humano, empático e eficiente ao detectar e responder aos estados emocionais dos usuários.

## 🎯 Objetivo Principal

Criar um banco de dados psicológico que:
- Detecta e compreende o estado emocional do usuário
- Fornece respostas empáticas e personalizadas
- Aprende com as interações para melhorar continuamente
- Detecta sinais de alerta de saúde mental
- Recomenda atividades de auto-cuidado
- Oferece suporte genuíno e compassivo

## 🏗️ Arquitetura do Sistema

### 1. **Camada de Modelos** (`psychology/models/`)

#### Dados Psicológicos Principais:

```kotlin
// Estado Emocional
enum class EmotionalState {
    HAPPY, SAD, ANXIOUS, FRUSTRATED, CALM, CONFUSED, EXCITED, NEUTRAL, TIRED, STRESSED
}

// Tipo de Personalidade
enum class UserPersonality {
    INTROVERT, EXTROVERT, ANALYTICAL, CREATIVE, PRAGMATIC, IDEALISTIC
}
```

#### Classes de Dados:

1. **UserPsychologicalProfile**
   - ID do usuário
   - Tipo de personalidade
   - Estado emocional atual
   - Nível de estresse (0-10)
   - Humor (-10 a 10)
   - Estilo de comunicação
   - Preferências de resposta
   - Gatilhos de trauma
   - Palavras de reforço positivo

2. **SentimentAnalysis**
   - Sentimento detectado
   - Confiança da análise (0-1)
   - Scores de emoções
   - Palavras-chave identificadas
   - Indicador de urgência
   - Indicador de necessidade de suporte

3. **EmpathyResponse**
   - Reconhecimento emocional
   - Validação dos sentimentos
   - Oferta de suporte
   - Solução/orientação
   - Encorajamento
   - Tags de tom

4. **InteractionHistory**
   - Histórico de interações
   - Antes e depois do estado emocional
   - Efetividade da resposta
   - Contexto da conversa

5. **MentalHealthAlert**
   - Alertas de saúde mental
   - Tipo de alerta (depressão, ansiedade, auto-agressão, crise)
   - Nível de severidade
   - Recursos profissionais

### 2. **Camada de Engine** (`psychology/engine/`)

O **EmpathyEngine** implementa as lógicas principais:

#### Funções Principais:

```kotlin
// Analisa sentimento da mensagem
fun analyzeSentiment(message: String): SentimentAnalysis

// Gera resposta empática
fun generateEmpathyResponse(
    sentiment: SentimentAnalysis,
    userProfile: UserPsychologicalProfile
): EmpathyResponse

// Atualiza perfil do usuário
fun updatePsychologicalProfile(
    profile: UserPsychologicalProfile,
    sentiment: SentimentAnalysis,
    responseEffectiveness: Float
): UserPsychologicalProfile

// Detecta alertas de saúde mental
fun detectMentalHealthAlerts(message: String): List<MentalHealthAlert>

// Recomenda auto-cuidado
fun recommendSelfCare(profile: UserPsychologicalProfile): List<SelfCareRecommendation>
```

## 💬 Exemplo de Uso

### Cenário 1: Usuário Triste

```kotlin
val empathyEngine = EmpathyEngine()
val userMessage = "Estou me sentindo muito triste e sozinho"

// Analisar sentimento
val sentiment = empathyEngine.analyzeSentiment(userMessage)
// → EmotionalState.SAD, confiança: 0.85, necessita suporte: true

// Gerar resposta empática
val empathyResponse = empathyEngine.generateEmpathyResponse(sentiment, userProfile)

// Resultado:
// Acknowledgment: "Entendo que você está se sentindo triste..."
// Validation: "Seus sentimentos são válidos e importantes..."
// Support: "Estou aqui para ajudar e apoiar você..."
// Solution: "Vamos pensar juntos em como melhorar..."
// Encouragement: "Você é mais forte do que pensa..."
```

### Cenário 2: Detecção de Alerta de Saúde Mental

```kotlin
val userMessage = "Não aguento mais, não vejo esperança"

// Detectar alertas
val alerts = empathyEngine.detectMentalHealthAlerts(userMessage)

// Resultado:
// Alert Type: DEPRESSION
// Severity: 8/10
// Recommended Action: "Procure ajuda profissional imediatamente"
// Resources: ["CVV: 188", "SAMU: 192", "Psicólogo/Psiquiatra"]
```

### Cenário 3: Recomendação de Auto-Cuidado

```kotlin
if (userProfile.stressLevel > 7) {
    val selfCareRecommendations = empathyEngine.recommendSelfCare(userProfile)
    // → Meditação de 10 min, Caminhada, Exercício
}
```

## 🔍 Detecção de Emoções

O sistema utiliza análise de palavras-chave em português:

### Mapeamento de Emoções:

| Emoção | Palavras-Chave |
|--------|----------------|
| HAPPY | feliz, alegre, contente, excelente, amor, adorei |
| SAD | triste, infeliz, deprimido, chorando, mal, pior |
| ANXIOUS | ansioso, nervoso, preocupado, tenso, medo |
| FRUSTRATED | frustrado, irritado, bravo, raiva, chato |
| CALM | calmo, paz, tranquilo, relaxado, sereno |
| STRESSED | estressado, sobrecarregado, cansado, esgotado |

## ⚠️ Detecção de Alertas de Saúde Mental

O sistema monitora palavras-chave de risco:

```kotlin
mentalHealthTriggers = mapOf(
    "depression" to ["suicídio", "morte", "nunca melhorar", "sem esperança"],
    "anxiety" to ["pânico", "ataque", "descontrole", "louco"],
    "self_harm" to ["machucar", "corte", "autoflagelação"]
)
```

**IMPORTANTE**: Quando um alerta é detectado, o sistema deve:
1. Não ignorar ou minimizar
2. Oferecer recursos de ajuda profissional imediatamente
3. Incluir números de emergência
4. Sugerir buscar ajuda profissional

## 🧩 Integração com o Backend

O sistema de empatia pode ser integrado com o Agent-S backend:

```kotlin
// No backend
@POST("api/v1/psychology/analyze")
fun analyzePsychology(@Body request: PsychologyRequest): Call<PsychologyResponse>

// Padrão de uso
val psychologyResult = empathyEngine.analyzeSentiment(message)
valRepository.analyzePsychology(message) { response ->
    // Combinar análise local com análise do backend
}
```

## 📊 Dados Coletados e Privacidade

### Dados Coletados:
- Histórico de mensagens
- Estados emocionais
- Efetividade das respostas
- Padrões de bem-estar

### Privacidade e Segurança:
- ✅ Dados criptografados localmente
- ✅ Armazenamento seguro em Room Database
- ✅ Conformidade com LGPD/GDPR
- ✅ Opção de deletar histórico
- ✅ Sem compartilhamento com terceiros

## 🎓 Aprendizado e Melhoria Contínua

O sistema aprende com cada interação:

```kotlin
// Armazenar efetividade da resposta
valhistory = InteractionHistory(
    message = userMessage,
    sentimentBefore = previousEmotion,
    sentimentAfter = currentEmotion,
    responseEffectiveness = userFeedback // 0-1
)

// Usar dados históricos para melhorar futuras respostas
val avgEffectiveness = calculateAverageEffectiveness(userHistory)
if (avgEffectiveness < 0.5) {
    // Ajustar estratégia de resposta
}
```

## 🛠️ Implementação Prática

### Passo 1: Inicializar Engine
```kotlin
val empathyEngine = EmpathyEngine()
```

### Passo 2: Processar Mensagem
```kotlin
val sentiment = empathyEngine.analyzeSentiment(userMessage)
```

### Passo 3: Gerar Resposta
```kotlin
val empathyResponse = empathyEngine.generateEmpathyResponse(sentiment, userProfile)
```

### Passo 4: Atualizar Perfil
```kotlin
val updatedProfile = empathyEngine.updatePsychologicalProfile(
    userProfile,
    sentiment,
    userFeedback
)
```

## 📱 Interface de Usuário

Recomendações para UI:

1. **Indicador de Emoção**
   - Mostrar estado emocional detectado com emoji/ícone
   - Escala visual do humor (0-10)

2. **Tipo de Resposta**
   - Respostas segmentadas (reconhecimento, validação, suporte, solução, encorajamento)
   - Tom adaptado às preferências do usuário

3. **Recomendações de Cuidado**
   - Sugerir meditação, exercício, etc.
   - Botões de ação rápida para auto-cuidado

4. **Alertas de Emergência**
   - Banner destacado em caso de alerta de saúde mental
   - Botão direto para contato com profissionais

## 🔗 Recursos Externos e Profissionais

O sistema fornece:
- **CVV (Central de Valorização da Vida)**: 188
- **SAMU (Serviço de Atendimento Móvel de Urgência)**: 192
- **Direcionamento para psicólogos/psiquiatras locais**
- **Linhas de suporte especializadas**

## 📈 Métricas de Sucesso

1. **Taxa de Efetividade**: Percentual de respostas que melhoram o humor do usuário
2. **Engajamento**: Frequência e duração das interações
3. **Satisfação**: Feedback dos usuários sobre qualidade de empatia
4. **Detecção de Alertas**: Precisão na identificação de sinais de risco

## 🚀 Próximos Passos

1. **Integração com Machine Learning**
   - Usar modelos NLP mais avançados
   - Treinar modelos com dados de conversa reais

2. **Expansão de Emoções**
   - Adicionar mais estados emocionais nuançados
   - Detectar múltiplas emoções em uma mensagem

3. **Personalização Avançada**
   - Perfil mais detalhado de usuário
   - Histórico de respostas efetivas por tipo de usuário

4. **Integração com Profissionais**
   - Conectar com redes de psicólogos
   - Sistema de escalação automática

## ⚖️ Considerações Éticas

- ✅ Nunca substituir profissionais de saúde mental
- ✅ Ser honesto sobre limitações do AI
- ✅ Sempre oferecer opção de contato com profissional
- ✅ Proteger privacidade e dados sensíveis
- ✅ Evitar discriminação ou julgamento
- ✅ Promover inclusão e diversidade

## 📞 Suporte e Contato

Para questões sobre o sistema de empatia:
- Entre em contato com a equipe de desenvolvimento
- Reporte problemas de detecção no repositório
- Sugira melhorias para respostas empáticas

---

**Criado com ❤️ para fazer uma diferença na vida das pessoas**
