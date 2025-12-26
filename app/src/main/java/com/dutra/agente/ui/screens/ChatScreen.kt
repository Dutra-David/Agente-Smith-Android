package com.dutra.agente.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutra.agente.presentation.viewmodel.ChatViewModel

/**
 * ChatScreen - Tela principal de chat
 *
 * IMPORTANTE: Recebe viewModel como PARAMETRO (NAO cria novo via hiltViewModel)
 * Isso evita duplicacao e travamentos!
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel  // Recebe como parametro - SEM hiltViewModel()
) {
    Log.d("ChatScreen", "ChatScreen renderizado")
    
    val messages by viewModel.chatMessages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        ChatHeader()

        // Verificacao de erro
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    errorMessage ?: "Erro desconhecido",
                    color = Color(0xFFC62828),
                    fontSize = 14.sp
                )
            }
        }

        // Area de mensagens
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFFAFAFA))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (messages.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        "Bem-vindo ao Agente Smith",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Nenhuma mensagem ainda. Comece digitando!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // Mostrar mensagens (simplificado)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    messages.forEach { message ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    if (message.isFromUser) Color(0xFF2196F3) else Color(0xFFE0E0E0),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            contentAlignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Text(
                                message.text,
                                color = if (message.isFromUser) Color.White else Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Input field
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            InputArea(
                inputText = inputText,
                onInputChange = { viewModel.updateInputText(it) },
                onSendClick = { 
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                    }
                }
            )
        }
    }
}

@Composable
fun ChatHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Agente Smith",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun InputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = { Text("Digite sua mensagem...") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color.White
            )
        )
        Button(
            onClick = onSendClick,
            modifier = Modifier
                .height(56.dp)
                .width(56.dp)
        ) {
            Text(">", fontSize = 20.sp)
        }
    }
}
