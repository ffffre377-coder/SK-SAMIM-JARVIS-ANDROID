package com.samim.jarvis.voice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppSelectionScreen(results: List<Pair<String, String>>, onSelect: (String) -> Unit, onCancel: () -> Unit) {
    Column {
        Text("Select an app")
        LazyColumn {
            items(results) { item ->
                Button(onClick = { onSelect(item.second) }, modifier = Modifier.fillMaxWidth()) {
                    Text(item.first)
                }
            }
        }
        Button(onClick = onCancel) { Text("Cancel") }
    }
}
