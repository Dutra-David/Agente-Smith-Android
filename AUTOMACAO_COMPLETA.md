# 탐 Automação Completa - Agente-Smith-Android

## 🚀 Guia de Execução Rápida

Este repositório está 100% automatizado. Você pode clonar e começar em **UM comando**.

### Opção 1: Automação Completa com Build e Run

```bash
git clone https://github.com/Dutra-David/Agente-Smith-Android.git
cd Agente-Smith-Android
bash setup.sh --build --run
```

### Opção 2: Abrir no Android Studio

```bash
bash open-studio.sh
```

### Opção 3: Setup Manual

```bash
bash setup.sh --sync-only
bash setup.sh --build-only
bash setup.sh --run-only
```

## 💾 Estrutura de Automação

### setup.sh - Automação Completa

- Valida Android SDK e Java JDK
- Sincroniza Gradle
- Compila APK
- Inicia emulador
- Executa a app

### open-studio.sh - Abrir no Android Studio

- Detecta SO (Windows/Mac/Linux)
- Encontra Android Studio automaticamente
- Abre o projeto

## 🔏 Configuração do Ambiente

### Pré-requisitos

1. **Android Studio** - https://developer.android.com/studio
2. **Android SDK 34+**
3. **Java JDK 11+**
4. **Git**

## ✅ Verificação Pós-Instalação

```bash
./gradlew clean build
adb devices
```

## 📋 Funcionalidades Implementadas

- ✅ Chat Interface (Jetpack Compose)
- ✅ ViewModel com Estado Reativo
- ✅ Hilt Dependency Injection
- ✅ Retrofit + OkHttp Network
- ✅ FastText NLP Integration
- ✅ MLEnhancedNLP Processing
- ✅ Android 34+ Suporte

## 🔠 Customização

### Alterar API Backend

Em: `app/src/main/java/com/dutra/agente/data/remote/RetrofitClient.kt`

```kotlin
private const val BASE_URL = "https://seu-servidor.com/api/"
```

## 💡 Dicas e Truques

### Se o setup.sh não funciona:

```bash
chmod +x setup.sh
chmod +x open-studio.sh
bash setup.sh --build --run
```

### Se tiver erro de Gradle:

```bash
./gradlew clean
./gradlew sync
bash setup.sh --build-only
```

## 🔐 Segurança & Produção

1. Altere a API URL para servidor real
2. Implemente autenticação (Bearer Token, OAuth)
3. Remova logs sensíveis
4. Configure HTTPS obrigatório
5. Gere APK assinada para release

## 📝 Resumo Rápido

| Opção | Comando | Tempo |
|---------|---------|-------|
| **Rápida** | `bash setup.sh --build --run` | ~5-10 min |
| **Android Studio** | `bash open-studio.sh` | ~2 min |
| **Manual** | `bash setup.sh --sync-only` | ~2 min |

## 📄 Versão do Projeto

- **Versão:** 1.0 (MVP)
- **Android:** 34+
- **Kotlin:** 1.9+
- **Jetpack Compose:** Latest

---

**Criado com ❤️ por Dutra-David**
