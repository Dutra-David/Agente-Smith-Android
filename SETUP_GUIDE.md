# Agente Smith - Android App

## Setup Inicial

Guia completo para configurar o projeto Agente-Smith-Android no seu Android Studio.

### Pré-requisitos

- **Android Studio** (versão Flamingo ou superior)
- **Java 17+** instalado
- **Android SDK** versão 33 ou superior
- **Git** para clonar o repositório

### Passos de Instalação

#### 1. Clonar o Repositório

```bash
git clone https://github.com/Dutra-David/Agente-Smith-Android.git
cd Agente-Smith-Android
```

#### 2. Abrir no Android Studio

1. Abra o Android Studio
2. Clique em "File" → "Open"
3. Selecione a pasta do projeto
4. Aguarde enquanto Gradle sincroniza

#### 3. Sincronizar Gradle

- Android Studio sincronizará automaticamente
- Se necessário, clique em "Sync Now" quando solicitado

#### 4. Executar o App

```bash
./gradlew assembleDebug
# ou usar a interface do Android Studio
```

### Arquitetura do Projeto

```
Agente-Smith-Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dutra/agente/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── ui/
│   │   │   │   ├── data/
│   │   │   │   ├── domain/
│   │   │   │   └── viewmodel/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

### Configuração do Backend

O app conectará ao backend Agent-S via API HTTP.

**URL padrão (desenvolvimento):**
```
http://localhost:5000/api
```

**Endpoints principais:**
- `POST /chat` - Enviar mensagem para IA
- `GET /status` - Verificar status do servidor

### Tecnologias Utilizadas

- **Kotlin** - Linguagem principal
- **Jetpack Compose** - UI moderna
- **Retrofit** - Cliente HTTP
- **Room** - Banco de dados local
- **Coroutines** - Programação assíncrona
- **Hilt** - Injeção de dependência

### Troubleshooting

**Problema:** Gradle sync falha
- **Solução:** `./gradlew clean` e tente novamente

**Problema:** SDK version mismatch
- **Solução:** Atualize o Android SDK via SDK Manager

### Próximos Passos

1. Configurar Activity principal
2. Implementar tela de chat
3. Integrar com API do Agent-S
4. Adicionar persistência local
5. Implementar autenticação

---

**Mais informações:** Veja o README.md para detalhes do projeto
