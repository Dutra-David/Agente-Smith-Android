package com.dutra.agente.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutra.agente.presentation.viewmodel.ChatViewModel

/**
 * ChatScreen MINIMO - Sem dependencias externas
 * So renderiza quando recebe ViewModel como parametro
 */
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    Log.d("ChatScreen", "Renderizando ChatScreen")
    
    val messages by viewModel.chatMessages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header simples
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6200EE))
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Agente Smith", color = Color.White, fontSize = 20.sp)
        }

        // Erro
        if (errorMessage != null) {
            Text(
                "Erro: $errorMessage",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE))
                    .padding(16.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        // Area de mensagens
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (messages.isEmpty()) {
                Text("Nenhuma mensagem", fontSize = 16.sp, color = Color.Gray)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    messages.forEach { msg ->
                        Text(
                            "${if (msg.isFromUser) "Voce" else "Bot"}: ${msg.text}",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Input
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { viewModel.updateInputText(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mensagem...") }
                )
                Button(onClick = { if (inputText.isNotBlank()) viewModel.sendMessage(inputText) }) {
                    Text(">")
                }
            }
        }
    }
}
