package com.dutra.agente.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * MessageInputField - Composable component for message input with send button
 * 
 * @param value Current text field value
 * @param onValueChange Callback when text value changes
 * @param onSendClick Callback when send button is clicked
 * @param modifier Modifier for the composable
 * @param enabled Whether the input field is enabled
 * @param placeholder Placeholder text for the input field
 */
@Composable
fun MessageInputField(
  value: TextFieldValue,
  onValueChange: (TextFieldValue) -> Unit,
  onSendClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  placeholder: String = "Digite sua mensagem..."
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(8.dp)
      .background(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(24.dp)
      )
      .padding(4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    TextField(
      value = value,
      onValueChange = onValueChange,
      modifier = Modifier
        .weight(1f)
        .padding(start = 12.dp),
      placeholder = {
        Text(
          text = placeholder,
          color = Color.Gray
        )
      },
      colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
      ),
      singleLine = true,
      enabled = enabled,
      maxLines = 1
    )

    IconButton(
      onClick = onSendClick,
      modifier = Modifier
        .padding(end = 4.dp),
      enabled = enabled && value.text.isNotBlank()
    ) {
      Icon(
        imageVector = Icons.Filled.Send,
        contentDescription = "Enviar mensagem",
        tint = if (enabled && value.text.isNotBlank()) 
          Color(0xFF6200EE) 
        else 
          Color.LightGray
      )
    }
  }
}

@Preview
@Composable
fun MessageInputFieldPreview() {
  MessageInputField(
    value = TextFieldValue(""),
    onValueChange = {},
    onSendClick = {}
  )
}
