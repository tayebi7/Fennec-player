// Fennec-player-main/app/src/main/java/com/fennecplayer/app/ui/FennecScreens.kt

package com.fennecplayer.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1B)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuButton(title = "Live", subtitle = "Channels") { onNavigate("live") }
            MenuButton(title = "Movies", subtitle = "VOD") { onNavigate("movies") }
            MenuButton(title = "Series", subtitle = "Shows") { onNavigate("series") }
            MenuButton(title = "Settings", subtitle = "Account") { onNavigate("settings") }
        }
    }
}

@Composable
fun MenuButton(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun VideoPlayerScreen(mediaName: String, onBack: () -> Unit) {
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000) // إخفاء الأزرار تلقائياً بعد 4 ثوانٍ
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        // محاكي مشغل الفيديو
        Box(modifier = Modifier.align(Alignment.Center)) {
            Text(text = "جارٍ تشغيل: $mediaName", color = Color.White)
        }

        if (showControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /* تغيير القناة أو رجوع */ }) {
                    Text("السابق / قناة أخرى")
                }
                Text(text = mediaName, color = Color.White)
                Button(onClick = { /* تسريع أو تحكم بالفيديو */ }) {
                    Text("تسريع / تقديم")
                }
            }
        }
    }
}
