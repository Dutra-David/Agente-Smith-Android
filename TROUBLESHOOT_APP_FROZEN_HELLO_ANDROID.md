# TROUBLESHOOT: App Travado em "Hello Android"

## 🔴 Problema
O app abre e fica preso na tela "Hello Android" e não responde a nenhum comando.

## ⚠️ Causas Mais Comuns

1. **MainActivity bloqueada esperando resposta da API**
   - Servidor não está respondendo
   - Endpoint URL incorreta
   - Conexão de rede bloqueada

2. **Banco de dados Room travando**
   - Erro ao inicializar BD
   - Migrations faltando

3. **Injeção de dependências (Hilt) com erro**
   - Módulos não configurados corretamente
   - Faltam dependências

4. **Thread UI bloqueada**
   - Operação síncrona na thread principal
   - Falta de coroutines/async

---

## ✅ SOLUÇÃO RÁPIDA (Teste Isto Primeiro)

### Passo 1: Abra o Logcat no Android Studio

```
Alt + 6 (Windows/Linux)
Cmd + 6 (Mac)
```

OU: **View → Tool Windows → Logcat**

### Passo 2: Procure por ERROS (vermelho)

Procure por um destes padrões:
- `E/ Exception`
- `E/ Error`
- `E/ Fatal`
- `NullPointerException`
- `NetworkException`
- `SocketTimeoutException`

### Passo 3: Identifique o Erro

**Erro: "Failed to connect to backend API"**
→ Solução: Ver passo "Corrigir Conexão API" abaixo

**Erro: "Cannot create instance of XXX"**
→ Solução: Ver passo "Corrigir Hilt" abaixo

**Erro: "Database error" ou "Migration error"**
→ Solução: Ver passo "Corrigir Banco de Dados" abaixo

**Erro: "ANR (Application Not Responding)"**
→ Solução: Ver passo "Corrigir Thread UI" abaixo

---

## 🔧 SOLUÇÕES ESPECÍFICAS

### Solução 1: Corrigir Conexão API

**Problema:** O app não consegue conectar ao servidor backend

**Arquivo:** `app/src/main/java/com/dutra/agente/data/remote/RetrofitClient.kt`

**Alteração:**

```kotlin
object RetrofitClient {
    // MUDE ISTO:
    private const val BASE_URL = "https://seu-servidor-real.com/api/"
    
    // PARA ISTO (teste local primeiro):
    private const val BASE_URL = "http://10.0.2.2:8000/api/" // Emulador localhost
    // OU para dispositivo real:
    // private const val BASE_URL = "http://seu-ip-local:8000/api/"
    
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

**Depois:**
```bash
./gradlew clean
./gradlew build
```

---

### Solução 2: Corrigir Hilt (Injeção de Dependências)

**Erro Típico:** `RuntimeException: Cannot create an instance of class XXX`

**Arquivo:** `app/src/main/java/com/dutra/agente/di/AppModule.kt`

**Verifique se existe:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

**E em MainActivity.kt:**

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val viewModel: ChatViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatScreen(viewModel)
        }
    }
}
```

---

### Solução 3: Corrigir Banco de Dados (Room)

**Erro:** `Database corruption` ou `Migration error`

**Fix Rápido:**

1. Abra `app/src/main/java/com/dutra/agente/data/banco/AppDatabase.kt`

2. Incremente a versão do banco:

```kotlin
@Database(
    entities = [HistoricoChat::class, UsuarioPerfil::class],
    version = 2  // AUMENTOU de 1 para 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historicoDao(): HistoricoChatDao
}
```

3. Limpe o banco:

```bash
# No Android Studio terminal:
adb shell pm clear com.dutra.agente  # Para emulador
```

OU desinstale o app manualmente e reconstrói.

---

### Solução 4: Corrigir Thread UI (ANR)

**Erro:** App congela por 5+ segundos (ANR - Application Not Responding)

**Problema:** Operação síncrona na thread principal

**Arquivo:** `app/src/main/java/com/dutra/agente/ui/viewmodel/ChatViewModel.kt`

**Corrija assim:**

```kotlin
class ChatViewModel(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _chatState = MutableStateFlow<ChatUIState>(ChatUIState.Idle)
    val chatState = _chatState.asStateFlow()
    
    fun sendMessage(message: String) {
        // ERRADO - vai travar:
        // val response = apiService.sendMessage(message) // Síncrono!
        
        // CORRETO - usa coroutine:
        viewModelScope.launch {
            try {
                _chatState.value = ChatUIState.Loading
                val response = apiService.sendMessage(message) // Assíncrono em IO thread
                _chatState.value = ChatUIState.Success(response.reply)
            } catch (e: Exception) {
                _chatState.value = ChatUIState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

---

## 🚀 TESTE RÁPIDO (Emulador)

### 1. Reinicie o Emulador
```bash
emulator -avd Pixel_6_API_34 -wipe-data
```

### 2. Reconstrua a aplicação
```bash
./gradlew clean
./gradlew build
```

### 3. Rode novamente
```bash
./gradlew installDebug
./gradlew runDebug
```

### 4. Abra Logcat e procure por erros

---

## 📋 CHECKLIST DE DIAGNÓSTICO

- [ ] Verificou Logcat para erros vermelhos?
- [ ] Anotou a mensagem de erro exata?
- [ ] Testou com BASE_URL = "http://10.0.2.2:8000/api/"?
- [ ] Verificou se o servidor backend está rodando?
- [ ] Limpou cache/build do Android Studio?
- [ ] Desinstalou a app anterior antes de testar?
- [ ] Testou em emulador (não em dispositivo físico)?

---

## 🆘 SE AINDA NÃO FUNCIONAR

### Colete informações do erro:

```bash
# No Android Studio terminal:
adb logcat | grep -i error
```

### Copie TODA a saída do erro
### Crie uma issue no repositório com:
- Saída completa do logcat
- Versão do Android Studio
- Versão do Android no emulador
- Android SDK version
- Java version

---

## 💡 DICA FINAL

O app foi feito para rodar com um **servidor backend FastText + MLEnhancedNLP**.

Se você não tem o backend rodando, o app vai travar tentando se conectar.

**Solução rápida:** Configure a URL do backend para um servidor de teste ou use mock data.
