package com.dutra.agente.apresentacao.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dutra.agente.apresentacao.viewmodel.ChatViewModel
import com.dutra.agente.dominio.MessageSender

@Composable
fun ChatScreen(
  viewModel: ChatViewModel = hiltViewModel(),
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()
  var inputText by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(8.dp)
  ) {
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      reverseLayout = true,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(messages.reversed()) { message ->
        MessageBubble(
          content = message.content,
          isUser = message.sender == MessageSender.USER,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      TextField(
        value = inputText,
        onValueChange = { inputText = it },
        modifier = Modifier.weight(1f),
        placeholder = { Text("Digite sua mensagem...") },
        enabled = !isLoading
      )

      Button(
        onClick = {
          if (inputText.isNotBlank()) {
            viewModel.sendMessage(inputText)
            inputText = ""
          }
        },
        enabled = !isLoading && inputText.isNotBlank()
      ) {
        Text(if (isLoading) "Enviando..." else "Enviar")
      }
    }
  }
}

@Composable
fun MessageBubble(
  content: String,
  isUser: Boolean,
  modifier: Modifier = Modifier
) {
  val backgroundColor = if (isUser) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant
  val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

  Box(
    modifier = modifier
      .padding(horizontal = 8.dp)
      .wrapContentWidth(
        if (isUser) Alignment.End else Alignment.Start
      )
  ) {
    Surface(
      color = backgroundColor,
      shape = MaterialTheme.shapes.medium,
      modifier = Modifier
        .widthIn(max = 300.dp)
        .padding(8.dp)
    ) {
      Text(
        text = content,
        color = textColor,
        modifier = Modifier.padding(12.dp),
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}
