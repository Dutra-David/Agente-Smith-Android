// ============================================
// SOLUCAO DE EMERGENCIA: CHAT SCREEN FUNCIONAL
// Capitao, copie este arquivo para:
// app/src/main/java/com/dutra/agente/ui/screens/ChatScreen.kt
// ============================================

package com.dutra.agente.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun ChatScreen() {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // TITULO
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "🤖 Agente Smith - Chat IA",
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF00FF00),
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }

        // LISTA DE MENSAGENS
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // INDICADOR DE CARREGAMENTO
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF00FF00),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        // INPUT
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    placeholder = { Text("Digite sua mensagem...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Color(0xFF00FF00)
                    )
                )

                Button(
                    onClick = {
                        if (inputText.text.isNotBlank()) {
                            // Adiciona mensagem do usuario
                            messages = messages + ChatMessage(inputText.text, true)
                            val userInput = inputText.text
                            inputText = TextFieldValue("")

                            // Simula resposta
                            isLoading = true
                            // Aqui você conectaria com a API real
                            val response = simulateAIResponse(userInput)
                            messages = messages + ChatMessage(response, false)
                            isLoading = false
                        }
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00)
                    ),
                    enabled = !isLoading
                ) {
                    Text("Enviar", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(4.dp),
            color = if (message.isUser) Color(0xFF00FF00) else Color(0xFF333333),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser) Color.Black else Color.White,
                fontSize = 14.sp
            )
        }
    }
}

fun simulateAIResponse(input: String): String {
    return "Agente Smith respondendo: Você disse '${input.take(30)}...' Funcionalidade em desenvolvimento!"
}
