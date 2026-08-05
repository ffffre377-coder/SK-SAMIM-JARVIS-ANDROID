package com.samim.jarvis.voice.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ContactSelectionScreen(results: List<Pair<String, String>>, onSelect: (String) -> Unit, onCancel: () -> Unit) {
    Column {
        Text("Select a contact")
        LazyColumn {
            items(results) { item ->
                Row(modifier = Modifier.fillMaxWidth().clickable { onSelect(item.second) }) {
                    Text(item.first)
                    Button(onClick = { onSelect(item.second) }) { Text("Select") }
                }
            }
        }
        Button(onClick = onCancel) { Text("Cancel") }
    }
}
