package com.samim.jarvis.automation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AutomationSettingsScreen(vm: AutomationViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Automation & Reminders")
        LazyColumn {
            items(state.reminders) { r ->
                Text("${r.title} at ${r.timeEpochMs}")
                Button(onClick = { scope.launch { vm.cancelReminder(r.id) } }) { Text("Cancel") }
            }
        }
        Button(onClick = { scope.launch { vm.createSampleReminder() } }) { Text("Create Sample Reminder") }
        Text(state.statusMessage)
    }
}
