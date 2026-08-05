package com.samim.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Button
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JarvisApp()
        }
    }
}

@Composable
fun JarvisApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(title = { Text("SK-SAMIM JARVIS") }) }
    ) { padding ->
        Column {
            Button(onClick = { /* navigate to Chat */ }) { Text("AI Chat") }
            Button(onClick = { /* navigate to Voice Assistant */ }) { Text("Voice Assistant") }
            Button(onClick = { /* navigate to Settings */ }) { Text("Settings") }
        }
    }
}
