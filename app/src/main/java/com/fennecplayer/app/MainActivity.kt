// Fennec-player-main/app/src/main/java/com/fennecplayer/app/MainActivity.kt

package com.fennecplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("home") }
            var activeCategory by remember { mutableStateOf("Live") }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0F0C1B)
            ) {
                when (currentScreen) {
                    "home" -> MainDashboard(
                        onSelectCategory = { category ->
                            activeCategory = category
                            currentScreen = "player"
                        }
                    )
                    "player" -> MediaViewerScreen(
                        categoryName = activeCategory,
                        onBack = { currentScreen = "home" }
                    )
                }
            }
        }
    }
}

@Composable
fun MainDashboard(onSelectCategory: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "IBO STYLE PLAYER",
                color = Color.Red,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard("Live Channels") { onSelectCategory("Live Channels") }
                DashboardCard("Movies") { onSelectCategory("Movies") }
                DashboardCard("Series") { onSelectCategory("Series") }
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(150.dp, 100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun MediaViewerScreen(categoryName: String, onBack: () -> Unit) {
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        Box(modifier = Modifier.align(Alignment.Center)) {
            Text(text = "عرض محتوى: $categoryName", color = Color.White)
        }

        if (showControls) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text("رجوع للقائمة")
            }
        }
    }
}
