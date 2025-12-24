package com.dutra.agente.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.dutra.agente.data.models.Message
import java.text.SimpleDateFormat
import java.util.*

/**
 * ChatMessageItem - Composable component for displaying individual chat messages
 * 
 * @param message The message object to display
 * @param modifier Modifier for the composable
 */
@Composable
fun ChatMessageItem(
  message: Message,
  modifier: Modifier = Modifier
) {
  val isUserMessage = message.isFromUser
  
  Box(
    modifier = modifier.fillMaxWidth(),
    contentAlignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .padding(
          start = if (isUserMessage) 48.dp else 8.dp,
          end = if (isUserMessage) 8.dp else 48.dp,
          top = 4.dp,
          bottom = 4.dp
        ),
      color = if (isUserMessage) 
        Color(0xFF6200EE)  // Purple for user
      else 
        Color(0xFFE8EAF6),  // Light purple for agent
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUserMessage) 16.dp else 0.dp,
        bottomEnd = if (isUserMessage) 0.dp else 16.dp
      )
    ) {
      Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = message.content,
          color = if (isUserMessage) Color.White else Color.Black,
          fontSize = 14.sp,
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
        )
        
        // Display timestamp
        Text(
          text = formatMessageTime(message.timestamp),
          color = if (isUserMessage) Color.White.copy(alpha = 0.7f) else Color.Gray,
          fontSize = 11.sp,
          textAlign = TextAlign.End,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

/**
 * Format message timestamp for display
 */
private fun formatMessageTime(timestamp: Long): String {
  return try {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    sdf.format(Date(timestamp))
  } catch (e: Exception) {
    "--:--"
  }
}

@Preview
@Composable
fun ChatMessageItemPreview() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    ChatMessageItem(
      message = Message(
        id = "1",
        content = "Olá, como posso ajudar?",
        isFromUser = false,
        timestamp = System.currentTimeMillis()
      )
    )
    
    ChatMessageItem(
      message = Message(
        id = "2",
        content = "Olá! Preciso de ajuda com um projeto.",
        isFromUser = true,
        timestamp = System.currentTimeMillis()
      )
    )
  }
}
