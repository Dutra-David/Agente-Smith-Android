package com.dutra.agente

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dutra.agente.ui.screens.ChatScreen
import com.dutra.agente.ui.theme.AgenteSmiththeme
import com.dutra.agente.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate iniciado")
        
        setContent {
            AgenteSmiththeme {
                InitializationScreen()
            }
        }
    }
}

@Composable
fun InitializationScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    var isInitialized by remember { mutableStateOf(false) }
    var initError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            Log.d("InitScreen", "Iniciando carregamento da aplicação...")
            
            // Pequeno delay para renderizar a tela
            delay(500)
            
            // AGORA chamamos createSession após UI estar pronta
            // Não no init do ViewModel!
            viewModel.createSession()
            
            Log.d("InitScreen", "Aguardando inicialização da sessão...")
            delay(1000)
            
            isInitialized = true
            Log.d("InitScreen", "Aplicação inicializada com sucesso")
        } catch (e: Exception) {
            Log.e("InitScreen", "Erro na inicialização", e)
            initError = "Erro ao inicializar: ${e.message}"
            // Mesmo com erro, permitir que o app continue
            delay(2000)
            isInitialized = true
        }
    }

    if (isInitialized) {
        if (initError != null) {
            ErrorScreen(initError ?: "Erro desconhecido")
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ChatScreen(viewModel)
            }
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Inicializando Agente Smith...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Por favor aguarde",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
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
                "⚠️ Erro na Inicialização",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B6B)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                errorMessage,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFF222222), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "A aplicação será carregada com funcionalidades limitadas.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
