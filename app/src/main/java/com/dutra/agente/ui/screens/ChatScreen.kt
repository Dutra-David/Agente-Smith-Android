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
import androidx.hilt.navigation.compose.hiltViewModel
import com.dutra.agente.data.models.Message
import com.dutra.agente.ui.components.ChatMessageItem
import com.dutra.agente.ui.components.MessageInputField
import com.dutra.agente.ui.theme.AgentTheme
import com.dutra.agente.viewmodels.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var inputValue by remember { mutableStateOf(TextFieldValue("")) }

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
                ChatMessageItem(message = message)
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        MessageInputField(
            value = inputValue,
            onValueChange = { inputValue = it },
            onSendClick = {
                if (inputValue.text.isNotBlank()) {
                    viewModel.sendMessage(inputValue.text)
                    inputValue = TextFieldValue("")
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
        color = AgentTheme.colors.primary,
        elevation = 4.dp
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
