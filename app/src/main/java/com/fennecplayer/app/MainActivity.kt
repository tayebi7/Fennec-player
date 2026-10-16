// Fennec-player-main/app/src/main/java/com/fennecplayer/app/MainActivity.kt
package com.fennecplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.fennecplayer.app.utils.QrAndDeviceAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("home") }
            var activeSection by remember { mutableStateOf("Live") }
            var xtreamUrl by remember { mutableStateOf("") }
            var xtreamUser by remember { mutableStateOf("") }
            var xtreamPass by remember { mutableStateOf("") }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0F0C1B)
            ) {
                when (currentScreen) {
                    "home" -> IboDashboard(
                        onNavigate = { screen ->
                            if (screen == "playlist_setup") {
                                currentScreen = "playlist_setup"
                            } else {
                                activeSection = screen
                                currentScreen = "media_viewer"
                            }
                        }
                    )
                    "playlist_setup" -> XtreamPlaylistSetupScreen(
                        url = xtreamUrl,
                        onUrlChange = { xtreamUrl = it },
                        user = xtreamUser,
                        onUserChange = { xtreamUser = it },
                        pass = xtreamPass,
                        onPassChange = { xtreamPass = it },
                        onBack = { currentScreen = "home" }
                    )
                    "media_viewer" -> IboMediaPlayerView(
                        categoryName = activeSection,
                        onBack = { currentScreen = "home" }
                    )
                }
            }
        }
    }
}

// 1. الواجهة الرئيسية لـ Fennec Player بروح IBO Player
@Composable
fun IboDashboard(onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1B)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FENNEC PLAYER",
                color = Color(0xFFFF4500),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Powered by IBO Player style",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuButtonLarge(title = "LIVE TV", color = Color(0xFF8B0000)) { onNavigate("Live") }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MenuButtonSmall(title = "Movies", color = Color(0xFF4A148C)) { onNavigate("Movies") }
                        MenuButtonSmall(title = "Series", color = Color(0xFF4A148C)) { onNavigate("Series") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MenuButtonSmall(title = "Playlists", color = Color(0xFF37474F)) { onNavigate("playlist_setup") }
                        MenuButtonSmall(title = "EXIT", color = Color(0xFF263238)) { /* خروج */ }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuButtonLarge(title: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(160.dp, 128.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun MenuButtonSmall(title: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(110.dp, 58.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

// 2. واجهة إدخال Xtream و M3U مع توليد QR Code
@Composable
fun XtreamPlaylistSetupScreen(
    url: String, onUrlChange: (String) -> Unit,
    user: String, onUserChange: (String) -> Unit,
    pass: String, onPassChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1B))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "إعداد قائمة Xtream Codes", color = Color.White, style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = url, onValueChange = onUrlChange, label = { Text("Server URL") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                OutlinedTextField(value = user, onValueChange = onUserChange, label = { Text("Username") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                OutlinedTextField(value = pass, onValueChange = onPassChange, label = { Text("Password") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) { Text("حفظ وتحديث") }
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) { Text("رجوع") }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Scan QR to upload playlist", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(10.dp))
                val qrBitmap = remember { QrAndDeviceAuth.generateQRCode("https://iboplayer.live/active?mac=e1:c2:f8:43:fc:2c&key=642114") }
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code to edit playists",
                        modifier = Modifier.size(160.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "MAC: e1:c2:f8:43:fc:2c", color = Color.Yellow, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Device Key: 642114", color = Color.Yellow, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// 3. مشغل الفيديو مع إخفاء الأزرار والواجهة تلقائياً بعد 4 ثوانٍ
@Composable
fun IboMediaPlayerView(categoryName: String, onBack: () -> Unit) {
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000) // تختفي الأزرار تلقائياً بعد 4 ثوانٍ لعدم الإزعاج
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
            Text(text = "جارٍ تشغيل محتوى: $categoryName", color = Color.DarkGray)
        }

        if (showControls) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("رجوع")
            }

            // أزرار تغيير القناة وتسريع الفيلم
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /* تغيير القناة */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) {
                    Text("تغيير القناة / Previous")
                }
                Text(text = "التشغيل الحالي: $categoryName", color = Color.White)
                Button(onClick = { /* تسريع الفيلم */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) {
                    Text("تسريع / تقديم Forward")
                }
            }
        }
    }
}
