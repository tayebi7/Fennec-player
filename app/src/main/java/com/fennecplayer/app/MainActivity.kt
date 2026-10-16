// Fennec-player-main/app/src/main/java/com/fennecplayer/app/MainActivity.kt

package com.fennecplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.fennecplayer.app.ui.HomeScreen
import com.fennecplayer.app.ui.VideoPlayerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("home") }
            var selectedMedia by remember { mutableStateOf("") }

            when (currentScreen) {
                "home" -> HomeScreen { route ->
                    if (route == "live" || route == "movies" || route == "series") {
                        selectedMedia = "عينة محتوى $route"
                        currentScreen = "player"
                    }
                }
                "player" -> VideoPlayerScreen(mediaName = selectedMedia) {
                    currentScreen = "home"
                }
            }
        }
    }
}
