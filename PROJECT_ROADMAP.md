# Roadmap - Agente Smith Android

## Fases de Desenvolvimento

### Fase 1: Setup Inicial (ATUAL)
**Status:** ✅ Em Progresso

- [x] Criar repositório GitHub
- [x] Documentação de setup
- [ ] Criar estrutura Gradle
- [ ] Configurar dependências

### Fase 2: UI Base (Próximas 2 semanas)
- [ ] Tela de chat com Jetpack Compose
- [ ] Componente de entrada de mensagem

### Fase 3: Backend Integration (Semana 3-4)
- [ ] Configurar Retrofit
- [ ] Models de requisição/resposta

## Tech Stack

- **Kotlin** - Linguagem principal
- **Jetpack Compose** - UI moderna
- **Retrofit** - Cliente HTTP
- **Room** - Banco de dados local
- **Coroutines** - Programação assíncrona
- **Hilt** - Injeção de dependência

## Dependências Principais

```gradle
// Jetpack
implementation("androidx.compose.ui:ui:1.6.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")

// Network
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")

// Database
implementation("androidx.room:room-runtime:2.5.2")
kapt("androidx.room:room-compiler:2.5.2")

// DI
implementation("com.google.dagger:hilt-android:2.46")
```

---

**Última atualização:** Dec 2024
