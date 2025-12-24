package com.dutra.agente.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dutra.agente.data.models.Message
import com.dutra.agente.ui.components.ChatMessageItem
import com.dutra.agente.ui.components.MessageInputField
import com.dutra.agente.presentation.viewmodel.ChatViewModel // Changed import to use the correct ViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.chatMessages.collectAsState() // Changed from .messages to .chatMessages
    val inputText by viewModel.inputText.collectAsState() // Added inputText state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ChatHeader()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                // Mapear ChatMessage para Message se necessário, ou atualizar ChatMessageItem
                // Assumindo que ChatMessageItem espera Message do pacote data.models.Message
                // Precisamos adaptar o modelo de UI ou o modelo de dados.
                // Vou fazer um mapeamento simples aqui para evitar quebrar o ChatMessageItem
                
                val uiMessage = Message(
                    id = message.id,
                    content = message.text,
                    isFromUser = message.isFromUser,
                    timestamp = message.timestamp
                )
                
                ChatMessageItem(message = uiMessage)
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        // Converter String para TextFieldValue para o input
        val inputValue = TextFieldValue(inputText)

        MessageInputField(
            value = inputValue,
            onValueChange = { viewModel.updateInputText(it.text) },
            onSendClick = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText)
                    // Input clear is handled in ViewModel
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
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

@Preview
@Composable
fun ChatPreview() {
    ChatScreen()
}
