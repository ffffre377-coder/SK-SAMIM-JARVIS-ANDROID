package com.samim.jarvis.memory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun MemorySettingsScreen(vm: MemoryViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    Column {
        Text("Saved Memories")
        LazyColumn {
            items(state.entries) { entry ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "${entry.key}: ${entry.value}")
                    Button(onClick = { scope.launch { vm.clearKey(entry.key) } }) { Text("Clear") }
                }
            }
        }
        Button(onClick = { scope.launch { vm.clearAll() } }) { Text("Clear All Memories") }
        Text(state.statusMessage)
    }
}
