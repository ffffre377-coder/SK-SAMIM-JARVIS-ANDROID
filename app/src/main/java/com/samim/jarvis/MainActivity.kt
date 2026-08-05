package com.samim.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dagger.hilt.android.AndroidEntryPoint
import com.samim.jarvis.ui.chat.ChatScreen
import com.samim.jarvis.ui.voice.VoiceAssistantScreen
import com.samim.jarvis.ui.settings.SettingsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JarvisApp()
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Chat : Screen("chat", "Chat")
    object Voice : Screen("voice", "Voice")
    object Settings : Screen("settings", "Settings")
}

@Composable
fun JarvisApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(title = { Text("SK-SAMIM JARVIS") }) },
        bottomBar = { AppBottomBar(navController = navController) }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.Chat.route, modifier = Modifier.padding(padding)) {
            composable(Screen.Chat.route) {
                ChatScreen()
            }
            composable(Screen.Voice.route) {
                VoiceAssistantScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun AppBottomBar(navController: NavHostController) {
    val items = listOf(Screen.Chat, Screen.Voice, Screen.Settings)
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Text(screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination to avoid building a large back stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
