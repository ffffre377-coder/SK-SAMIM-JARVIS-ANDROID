package com.samim.jarvis.voice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samim.jarvis.voice.assistants.SecureSnippetRepository
import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.launch

@Composable
fun CodingAssistantScreen(secureStorage: SecureStorage) {
    val repo = SecureSnippetRepository(secureStorage)
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Saved Code Snippets")
        LazyColumn {
            val items = listOf<Pair<String, String>>()
            items(items) { s ->
                Text("${s.first}")
            }
        }
        Button(onClick = { scope.launch { /* placeholder */ } }) { Text("New Snippet") }
    }
}
