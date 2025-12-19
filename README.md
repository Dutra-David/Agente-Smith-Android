# 탐 Agente-Smith-Android

**Agente de IA inteligente para Android** - Versão móvel do Agent-S. Chat interativo com IA, integrado com backend FastText + MLEnhancedNLP. Sem dependências externas, otimizado para Android Studio.

## 🚀 Início Rápido (1 Comando!)

Este repositório está **100% automatizado**. Escolha uma opção:

### Opção 1: Executação Automática Completa

```bash
git clone https://github.com/Dutra-David/Agente-Smith-Android.git
cd Agente-Smith-Android
bash setup.sh --build --run
```

**Resultado:** App rodando no emulador em ~5-10 minutos

### Opção 2: Abrir no Android Studio

```bash
git clone https://github.com/Dutra-David/Agente-Smith-Android.git
cd Agente-Smith-Android
bash open-studio.sh
```

**Resultado:** Projeto aberto no Android Studio, pronto para clicar RUN

### Opção 3: Manual/Desenvolvimento

```bash
# Apenas sincronizar dependencias
bash setup.sh --sync-only

# Apenas compilar
bash setup.sh --build-only

# Apenas executar (requer emulador já aberto)
bash setup.sh --run-only
```

---

## 💾 Documentação Completa de Automação

**Para detalhes completos sobre todas as opções de automacão:**

📌 [**AUTOMACAO_COMPLETA.md**](./AUTOMACAO_COMPLETA.md) - Guia completo com:
- 3 Opções de execução
- Setup.sh e open-studio.sh detalhes
- Configuração de ambiente
- Troubleshooting
- Customização de API
- Dicas e truques

## 📝 Pré-requisitos

- **Android Studio** (versão 2023.1+)
- **Android SDK 34+** (API Tiramisu ou superior)
- **Java JDK 11+** (incluído no Android Studio)
- **Git** (para clonar o repositório)

## 📋 Funcionalidades Implementadas

### Core Features
- ✅ **UI Chat** com Jetpack Compose
- ✅ **ViewModel Reativo** com StateFlow
- ✅ **Hilt Dependency Injection**
- ✅ **Retrofit + OkHttp** para networking
- ✅ **FastText NLP** integration
- ✅ **MLEnhancedNLP** processing
- ✅ **Android 34+** API support

### Automação
- ✅ **setup.sh** - Build + Run automático
- ✅ **open-studio.sh** - Abrir Android Studio automático
- ✅ **Gradle** - Todas as dependências configuradas
- ✅ **Hilt AppModule** - Injeção de dependências pronta
- ✅ **Android Emulator** - Suporte completo

## 📁 Estrutura do Projeto

```
Agente-Smith-Android/
├── app/
│   ├── src/main/java/com/dutra/agente/
│   │   ├── ui/
│   │   │   ├── ChatScreen.kt
│   │   │   ├── ChatViewModel.kt
│   │   │   ├── MessageInputField.kt
│   │   │   └── ChatMessageItem.kt
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   └── Message.kt
│   │   │   └── remote/
│   │   │       └── RetrofitClient.kt
│   │   ├── di/
│   │   │   └── AppModule.kt
│   │   └── AgentSmithApplication.kt
│   ├── build.gradle.kts
│   └── AndroidManifest.xml
├── build.gradle.kts
├── setup.sh
├── open-studio.sh
├── AUTOMACAO_COMPLETA.md
├── SETUP_RAPIDO.md
└── README.md
```

## 🔠 Customização

### Alterar URL da API

**Ficheiro:** `app/src/main/java/com/dutra/agente/data/remote/RetrofitClient.kt`

```kotlin
private const val BASE_URL = "https://seu-servidor.com/api/"
```

Recompile:
```bash
bash setup.sh --build-only
```

## 📚 Documentação Adicional

- 📌 [AUTOMACAO_COMPLETA.md](./AUTOMACAO_COMPLETA.md) - Guia completo de automacão
- 📌 [SETUP_RAPIDO.md](./SETUP_RAPIDO.md) - Quickstart com troubleshooting
- 📌 [ARQUITETURA.md](./ARQUITETURA.md) - Detalhes de arquitetura
- 📌 [ROTEIRO DO PROJETO.md](./ROTEIRO_DO_PROJETO.md) - Roadmap

## 🔐 Deploy em Produção

Antes de fazer upload para Google Play Store:

1. Altere a API URL para seu servidor real
2. Implemente autenticação (Bearer Token, OAuth2)
3. Remova logs de debug
4. Gere APK assinada:

```bash
./gradlew assembleRelease
```

## 😸 Versão Atual

- **Versão:** 1.0.0 (MVP)
- **Android Minimo:** 34 (Tiramisu)
- **Android Target:** 34
- **Kotlin:** 1.9+
- **Java:** 11+

## 🤝 Contribuindo

1. Faça um Fork
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.

## 📧 Suporte

Para suporte, questoes ou bugs:

1. Abra uma **Issue** no repositório
2. Consulte **AUTOMACAO_COMPLETA.md** para troubleshooting
3. Verifique **SETUP_RAPIDO.md** para soluções rápidas

---

**Criado com ❤️ por Dutra-David**

Para mais informações sobre automação, veja [AUTOMACAO_COMPLETA.md](./AUTOMACAO_COMPLETA.md)
