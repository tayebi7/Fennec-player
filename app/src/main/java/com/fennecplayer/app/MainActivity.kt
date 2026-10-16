package com.fennecplayer.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.fennecplayer.app.data.ChannelEntity
import com.fennecplayer.app.data.FennecDatabase
import com.fennecplayer.app.data.StreamType
import com.fennecplayer.app.parser.MassivePlaylistEngine
import com.fennecplayer.app.ui.*
import kotlinx.coroutines.launch

enum class AppScreen { HOME, LIVE, MOVIES, SERIES, PLAYLISTS, FULLSCREEN_PLAYER }

class MainActivity : ComponentActivity() {

    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exoPlayer = ExoPlayer.Builder(this).build()
        val database = FennecDatabase.getDatabase(this)
        val dao = database.channelDao()
        val playlistEngine = MassivePlaylistEngine(dao)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
                var activeStreamType by remember { mutableStateOf(StreamType.LIVE) }
                var selectedCategory by remember { mutableStateOf("All") }
                var currentChannel by remember { mutableStateOf<ChannelEntity?>(null) }

                var isLoading by remember { mutableStateOf(false) }
                var loadedCount by remember { mutableStateOf(0) }

                val categories by dao.getCategoriesByType(activeStreamType).collectAsState(initial = emptyList())
                val itemsFlow = remember(activeStreamType, selectedCategory) {
                    if (selectedCategory == "Favorites") {
                        dao.getFavoritesByType(activeStreamType)
                    } else {
                        dao.getChannelsByGroup(activeStreamType, selectedCategory)
                    }
                }
                val channelList by itemsFlow.collectAsState(initial = emptyList())

                fun playChannel(channel: ChannelEntity) {
                    currentChannel = channel
                    exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(channel.streamUrl)))
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }

                when (currentScreen) {
                    AppScreen.HOME -> IboLauncherScreen(
                        onNavigateLive = {
                            activeStreamType = StreamType.LIVE
                            selectedCategory = "All"
                            currentScreen = AppScreen.LIVE
                        },
                        onNavigateMovies = {
                            activeStreamType = StreamType.MOVIE
                            selectedCategory = "All"
                            currentScreen = AppScreen.MOVIES
                        },
                        onNavigateSeries = {
                            activeStreamType = StreamType.SERIES
                            selectedCategory = "All"
                            currentScreen = AppScreen.SERIES
                        },
                        onNavigatePlaylists = { currentScreen = AppScreen.PLAYLISTS },
                        onNavigateAccount = { currentScreen = AppScreen.PLAYLISTS },
                        onReload = { currentScreen = AppScreen.PLAYLISTS },
                        onExit = { finish() }
                    )

                    AppScreen.LIVE -> IboLiveTvScreen(
                        categories = categories,
                        channels = channelList,
                        selectedCategory = selectedCategory,
                        selectedChannel = currentChannel,
                        exoPlayer = exoPlayer,
                        onSelectCategory = { selectedCategory = it },
                        onSelectChannel = { playChannel(it) },
                        onToggleFavorite = { ch ->
                            lifecycleScope.launch { dao.setFavorite(ch.id, !ch.isFavorite) }
                        },
                        onOpenFullScreen = { currentScreen = AppScreen.FULLSCREEN_PLAYER },
                        onNavigateMovies = {
                            exoPlayer.stop()
                            activeStreamType = StreamType.MOVIE
                            selectedCategory = "All"
                            currentScreen = AppScreen.MOVIES
                        },
                        onNavigateSeries = {
                            exoPlayer.stop()
                            activeStreamType = StreamType.SERIES
                            selectedCategory = "All"
                            currentScreen = AppScreen.SERIES
                        },
                        onBack = {
                            exoPlayer.stop()
                            currentScreen = AppScreen.HOME
                        }
                    )

                    AppScreen.MOVIES -> IboVodGridScreen(
                        title = "Movies - الأفلام",
                        categories = categories,
                        items = channelList,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        onSelectVod = { vod ->
                            playChannel(vod)
                            currentScreen = AppScreen.FULLSCREEN_PLAYER
                        },
                        onBack = { currentScreen = AppScreen.HOME }
                    )

                    AppScreen.SERIES -> IboVodGridScreen(
                        title = "Series - المسلسلات",
                        categories = categories,
                        items = channelList,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        onSelectVod = { series ->
                            playChannel(series)
                            currentScreen = AppScreen.FULLSCREEN_PLAYER
                        },
                        onBack = { currentScreen = AppScreen.HOME }
                    )

                    AppScreen.FULLSCREEN_PLAYER -> {
                        val active = currentChannel
                        if (active != null) {
                            FullScreenPlayerWithAutoHideControls(
                                channel = active,
                                exoPlayer = exoPlayer,
                                onNextChannel = {
                                    val idx = channelList.indexOfFirst { it.id == active.id }
                                    if (idx != -1 && idx + 1 < channelList.size) {
                                        playChannel(channelList[idx + 1])
                                    }
                                },
                                onPrevChannel = {
                                    val idx = channelList.indexOfFirst { it.id == active.id }
                                    if (idx > 0) {
                                        playChannel(channelList[idx - 1])
                                    }
                                },
                                onExitFullScreen = {
                                    currentScreen = when (activeStreamType) {
                                        StreamType.LIVE -> AppScreen.LIVE
                                        StreamType.MOVIE -> AppScreen.MOVIES
                                        StreamType.SERIES -> AppScreen.SERIES
                                    }
                                }
                            )
                        }
                    }

                    AppScreen.PLAYLISTS -> IboPlaylistAndQrScreen(
                        isLoading = isLoading,
                        loadedCount = loadedCount,
                        onLoadXtream = { host, user, pass ->
                            lifecycleScope.launch {
                                isLoading = true
                                loadedCount = 0
                                try {
                                    playlistEngine.loadXtreamPlaylist(host, user, pass) { count ->
                                        loadedCount = count
                                    }
                                    currentScreen = AppScreen.HOME
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        onLoadM3u = { url ->
                            lifecycleScope.launch {
                                isLoading = true
                                loadedCount = 0
                                try {
                                    playlistEngine.downloadAndParseM3U(url) { count ->
                                        loadedCount = count
                                    }
                                    currentScreen = AppScreen.HOME
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
