package com.samim.jarvis.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.messages) { msg ->
                Text(text = "${msg.sender}: ${msg.content}", modifier = Modifier.padding(4.dp))
            }
        }

        var text by remember { mutableStateOf("") }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (text.isNotBlank()) {
                    viewModel.sendMessage(text)
                    text = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}
