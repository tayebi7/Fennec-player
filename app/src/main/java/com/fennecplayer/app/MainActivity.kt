package com.fennecplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.fennecplayer.app.data.ContentType
import com.fennecplayer.app.data.FennecDatabase
import com.fennecplayer.app.parser.MassivePlaylistEngine
import com.fennecplayer.app.ui.*
import kotlinx.coroutines.launch

enum class ScreenRoute {
    ACTIVATION_QR,
    DASHBOARD,
    LIVE_TV,
    MOVIES,
    SERIES
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FennecDatabase.getInstance(this)
        val dao = db.dao()
        val playlistEngine = MassivePlaylistEngine(dao)

        setContent {
            var currentScreen by remember { mutableStateOf(ScreenRoute.ACTIVATION_QR) }
            var showAddDialog by remember { mutableStateOf(false) }
            var loadingCount by remember { mutableStateOf<Int?>(null) }

            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    ScreenRoute.ACTIVATION_QR -> {
                        PlaylistActivationScreen(
                            onOpenPlaylist = { currentScreen = ScreenRoute.DASHBOARD },
                            onAddManualClick = { showAddDialog = true }
                        )
                    }
                    ScreenRoute.DASHBOARD -> {
                        MainDashboardScreen(
                            onNavigateLive = { currentScreen = ScreenRoute.LIVE_TV },
                            onNavigateMovies = { currentScreen = ScreenRoute.MOVIES },
                            onNavigateSeries = { currentScreen = ScreenRoute.SERIES },
                            onChangePlaylist = { currentScreen = ScreenRoute.ACTIVATION_QR },
                            onReloadClick = { showAddDialog = true },
                            onExitClick = { finish() }
                        )
                    }
                    ScreenRoute.LIVE_TV -> LiveChannelsScreen(dao = dao)
                    ScreenRoute.MOVIES -> VodMoviesGridScreen(dao = dao, contentType = ContentType.MOVIE)
                    ScreenRoute.SERIES -> VodMoviesGridScreen(dao = dao, contentType = ContentType.SERIES)
                }

                if (showAddDialog) {
                    AddPlaylistDialog(
                        onDismiss = { showAddDialog = false },
                        onLoadM3U = { url ->
                            showAddDialog = false
                            loadingCount = 0
                            lifecycleScope.launch {
                                playlistEngine.loadM3UPlaylist(url) { count ->
                                    loadingCount = count
                                }
                                loadingCount = null
                                currentScreen = ScreenRoute.DASHBOARD
                            }
                        },
                        onLoadXtream = { host, user, pass ->
                            showAddDialog = false
                            loadingCount = 0
                            lifecycleScope.launch {
                                playlistEngine.loadXtreamCodes(host, user, pass) { count ->
                                    loadingCount = count
                                }
                                loadingCount = null
                                currentScreen = ScreenRoute.DASHBOARD
                            }
                        }
                    )
                }

                if (loadingCount != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = FennecGold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "جاري تحميل وفهرسة القنوات في قاعدة البيانات... ($loadingCount قناة)",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPlaylistDialog(
    onDismiss: () -> Unit,
    onLoadM3U: (String) -> Unit,
    onLoadXtream: (String, String, String) -> Unit
) {
    var mode by remember { mutableStateOf("M3U") }
    var m3uUrl by remember { mutableStateOf("") }
    var xtreamHost by remember { mutableStateOf("") }
    var xtreamUser by remember { mutableStateOf("") }
    var xtreamPass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Playlist (M3U / Xtream Codes)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { mode = "M3U" }) { Text("M3U Link") }
                    Button(onClick = { mode = "XTREAM" }) { Text("Xtream Codes") }
                }
                if (mode == "M3U") {
                    OutlinedTextField(
                        value = m3uUrl,
                        onValueChange = { m3uUrl = it },
                        label = { Text("M3U URL") },
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(value = xtreamHost, onValueChange = { xtreamHost = it }, label = { Text("Server URL (http://...)") }, singleLine = true)
                    OutlinedTextField(value = xtreamUser, onValueChange = { xtreamUser = it }, label = { Text("Username") }, singleLine = true)
                    OutlinedTextField(value = xtreamPass, onValueChange = { xtreamPass = it }, label = { Text("Password") }, singleLine = true)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (mode == "M3U" && m3uUrl.isNotBlank()) onLoadM3U(m3uUrl)
                else if (mode == "XTREAM" && xtreamHost.isNotBlank()) onLoadXtream(xtreamHost, xtreamUser, xtreamPass)
            }) {
                Text("Save & Load")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
