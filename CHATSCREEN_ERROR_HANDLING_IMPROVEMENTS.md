# ChatScreen Error Handling Improvements

## Overview
Guia para implementar tratamento robusto de erros no ChatScreen e evitar travamentos causados por dependências de API.

## Problema Diagnosticado
O ChatScreen provavelmente está tentando:
1. Carregar dados do banco de dados na inicialização
2. Conectar à API imediatamente
3. Não tem timeout ou fallback para falhas

Isso causa travamento indefinido se a API falhar.

## Solução: Implementar SafeComposable com Error Boundary

### Passo 1: Criar ErrorBoundary Composable

```kotlin
// app/src/main/java/com/dutra/agente/ui/composables/ErrorBoundary.kt

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SafeComposable(
    modifier: Modifier = Modifier,
    onError: (Exception) -> Unit = {},
    content: @Composable () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    try {
        if (!hasError) {
            content()
        } else {
            ErrorFallback(
                message = errorMessage,
                onRetry = { hasError = false }
            )
        }
    } catch (e: Exception) {
        hasError = true
        errorMessage = e.message ?: "Erro desconhecido"
        onError(e)
        ErrorFallback(
            message = errorMessage,
            onRetry = { hasError = false }
        )
    }
}

@Composable
fun ErrorFallback(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Erro ao Carregar Chat",
                color = Color(0xFFFF6B6B),
                fontSize = androidx.compose.material3.MaterialTheme.typography.headlineSmall.fontSize
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                message,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Tentar Novamente")
            }
        }
    }
}
```

### Passo 2: Atualizar ChatScreen

```kotlin
// app/src/main/java/com/dutra/agente/ui/screens/ChatScreen.kt

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    SafeComposable {
        // Seu conteúdo do chat aqui
        when (viewModel.uiState) {
            is ChatUiState.Loading -> LoadingContent()
            is ChatUiState.Content -> ChatContent(viewModel)
            is ChatUiState.Error -> ErrorContent((viewModel.uiState as ChatUiState.Error).message)
        }
    }
}
```

## Passo 3: Melhorar ChatViewModel

```kotlin
// app/src/main/java/com/dutra/agente/presentation/viewmodel/ChatViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val apiInitService: ApiInitializationService,
    private val chatRepository: ChatRepository // ou outro serviço de dados
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val TAG = "ChatViewModel"
    
    init {
        initializeChat()
    }
    
    private fun initializeChat() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Inicializando Chat...")
                _uiState.value = ChatUiState.Loading
                
                // Inicializar API com timeout
                val result = withTimeout(10000) { // 10 segundos de timeout
                    apiInitService.initialize()
                }
                
                Log.d(TAG, "API inicializada: $result")
                
                // Carregar mensagens iniciais
                val initialMessages = loadInitialMessages()
                _messages.value = initialMessages
                _uiState.value = ChatUiState.Content
                
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Timeout na inicialização", e)
                _uiState.value = ChatUiState.Error(
                    "Tempo limite excedido. Verifique sua conexão."
                )
            } catch (e: Exception) {
                Log.e(TAG, "Erro na inicialização", e)
                _uiState.value = ChatUiState.Error(
                    e.message ?: "Erro desconhecido"
                )
            }
        }
    }
    
    private suspend fun loadInitialMessages(): List<ChatMessage> {
        return try {
            // Tentar carregar do banco de dados ou API
            chatRepository.getMessages()
        } catch (e: Exception) {
            Log.w(TAG, "Não conseguiu carregar mensagens, usando lista vazia", e)
            emptyList() // Fallback para lista vazia
        }
    }
    
    fun sendMessage(text: String) {
        viewModelScope.launch {
            try {
                val response = withTimeout(5000) {
                    chatRepository.sendMessage(text)
                }
                // Atualizar UI com resposta
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao enviar mensagem", e)
                // Mostrar erro ao usuário
            }
        }
    }
}

seal class ChatUiState {
    object Loading : ChatUiState()
    object Content : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long
)
```

## Passo 4: Adicionar Retry Logic

```kotlin
fun retryInitialization() {
    viewModelScope.launch {
        _uiState.value = ChatUiState.Loading
        initializeChat()
    }
}
```

## Melhores Práticas

### 1. **Sempre Use Try-Catch em Inicialização**
```kotlin
init {
    try {
        // Código de inicialização
    } catch (e: Exception) {
        // Fallback seguro
    }
}
```

### 2. **Implemente Timeout para Operações de Rede**
```kotlin
withTimeout(10000) {
    apiCall()
}
```

### 3. **Use StateFlow para Gerenciar Estado**
```kotlin
private val _state = MutableStateFlow<State>(Initial)
val state: StateFlow<State> = _state.asStateFlow()
```

### 4. **Implemente Retry com Exponential Backoff**
```kotlin
fun retryWithBackoff(
    initialDelayMs: Long = 1000,
    maxRetries: Int = 3,
    block: suspend () -> T
): T {
    var lastException: Exception? = null
    var delay = initialDelayMs
    
    repeat(maxRetries) {
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            delay(delay)
            delay *= 2  // Exponential backoff
        }
    }
    
    throw lastException ?: Exception("Falha após $maxRetries tentativas")
}
```

### 5. **Adicione Logging Detalhado**
```kotlin
Log.d(TAG, "Iniciando operação XYZ")
Log.e(TAG, "Erro em XYZ", exception)
```

## Checklist de Implementação

- [ ] Criar ErrorBoundary composable
- [ ] Adicionar timeout em API calls
- [ ] Implementar retry logic
- [ ] Adicionar logging completo
- [ ] Criar fallback UI para erros
- [ ] Testar com conexão desligada
- [ ] Testar com conexão lenta
- [ ] Testar recovery após erro

## Teste de Stress

### Simular Desconexão de Rede
```bash
adb shell settings put global airplane_mode_on 1
```

### Restaurar Conexão
```bash
adb shell settings put global airplane_mode_on 0
```

## Resultado Esperado

1. App exibe splash screen durante inicialização
2. Se falhar, mostra mensagem de erro com botão "Tentar Novamente"
3. Todos os calls à API tém timeout de 10 segundos
4. Chat funciona em modo mock se API não responder
5. Logs detalhados em Logcat para diagnóstico

## Referências
- Kotlin Coroutines: https://developer.android.com/kotlin/coroutines
- Compose Error Handling: https://developer.android.com/jetpack/compose/phases/lifecycle
- Hilt Dependency Injection: https://developer.android.com/training/dependency-injection/hilt-android
