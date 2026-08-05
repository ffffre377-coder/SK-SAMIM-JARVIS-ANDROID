package com.samim.jarvis.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ChatScreen() {
    Column {
        Text("AI Chat")
        Button(onClick = { /* TODO send message */ }) {
            Text("Send")
        }
    }
}
