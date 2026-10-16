// app/src/main/java/com/fennecplayer/app/MainActivity.kt
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
            var selectedSection by remember { mutableStateOf("Live") }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0F0C1B)
            ) {
                when (currentScreen) {
                    "home" -> IboHomeDashboard(
                        onNavigate = { screen ->
                            if (screen == "settings" || screen == "account") {
                                currentScreen = "qr_setup"
                            } else {
                                selectedSection = screen
                                currentScreen = "player"
                            }
                        }
                    )
                    "player" -> IboMediaPlayerScreen(
                        sectionTitle = selectedSection,
                        onBack = { currentScreen = "home" }
                    )
                    "qr_setup" -> IboQrSetupScreen(
                        onBack = { currentScreen = "home" }
                    )
                }
            }
        }
    }
}

// 1. القائمة الرئيسية بتصميم IBO Player
@Composable
fun IboHomeDashboard(onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "IBO PLAYER PRO",
                color = Color(0xFFFF4500),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // المربع الرئيسي للبث المباشر
                DashboardButton(title = "Live", color = Color(0xFF8B0000), modifier = Modifier.size(180.dp, 120.dp)) {
                    onNavigate("Live")
                }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardButton(title = "Movies", color = Color(0xFF6A1B9A)) { onNavigate("Movies") }
                        DashboardButton(title = "Series", color = Color(0xFF6A1B9A)) { onNavigate("Series") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardButton(title = "Account", color = Color(0xFF37474F)) { onNavigate("account") }
                        DashboardButton(title = "Settings", color = Color(0xFF37474F)) { onNavigate("settings") }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardButton(title: String, color: Color, modifier: Modifier = Modifier.size(120.dp, 55.dp), onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

// 2. شاشة التشغيل مع إخفاء أزرار التحكم (تغيير القناة / التسريع) تلقائياً
@Composable
fun IboMediaPlayerScreen(sectionTitle: String, onBack: () -> Unit) {
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000) // تختفي الأزرار تلقائياً بعد 4 ثوانٍ
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
            Text(text = "جارٍ عرض محتوى: $sectionTitle", color = Color.Gray)
        }

        if (showControls) {
            // شريط التحكم السفلي الذي يختفي تلقائياً
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /* تغيير القناة */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) {
                    Text("تغيير القناة")
                }
                Text(text = "$sectionTitle - بث مباشر", color = Color.White)
                Button(onClick = { /* تسريع أو تحكم بالفيديو */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) {
                    Text("تسريع / تقديم")
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("الرجوع للقائمة")
            }
        }
    }
}

// 3. شاشة إعدادات الجهاز ورمز الاستجابة السريعة (QR Code Setup)
@Composable
fun IboQrSetupScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1B))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "إدارة قوائم التشغيل وإدخال M3U / Xtream", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Device ID: e1:c2:f8:43:fc:2c", color = Color(0xFFFFD700))
            Text(text = "Device Key: 642114", color = Color(0xFFFFD700))
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) {
                Text("رجوع")
            }
        }
    }
}
