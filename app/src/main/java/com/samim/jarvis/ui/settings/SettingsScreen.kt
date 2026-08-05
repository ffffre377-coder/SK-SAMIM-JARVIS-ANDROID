package com.samim.jarvis.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "AI Providers", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.padding(8.dp))

        state.providers.forEach { provider ->
            Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(provider.displayName, style = MaterialTheme.typography.subtitle1, modifier = Modifier.weight(1f))
                        Switch(checked = provider.enabled, onCheckedChange = { viewModel.setProviderEnabled(provider.id, it) })
                    }

                    OutlinedTextField(
                        value = provider.apiKey,
                        onValueChange = { viewModel.updateApiKey(provider.id, it) },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (provider.visible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.toggleVisibility(provider.id) }) {
                                Icon(imageVector = if (provider.visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                            }
                        }
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { viewModel.saveApiKey(provider.id) }, modifier = Modifier.padding(end = 8.dp)) {
                            Text("Save")
                        }
                        Button(onClick = { viewModel.testProvider(provider.id) }) {
                            Text("Test API")
                        }
                        when (val status = provider.status) {
                            is ProviderStatus.Testing -> Text(" Testing...", modifier = Modifier.padding(start = 8.dp))
                            is ProviderStatus.Success -> Text(" Success: ${status.message}", color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 8.dp))
                            is ProviderStatus.Failure -> Text(" Failure: ${status.message}", color = MaterialTheme.colors.error, modifier = Modifier.padding(start = 8.dp))
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
