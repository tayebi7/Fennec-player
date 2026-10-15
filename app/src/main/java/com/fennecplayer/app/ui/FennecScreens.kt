package com.fennecplayer.app.ui

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import com.fennecplayer.app.data.ContentType
import com.fennecplayer.app.data.FennecDao
import com.fennecplayer.app.data.PlaylistItemEntity
import com.fennecplayer.app.utils.QrAndDeviceAuth
import java.text.SimpleDateFormat
import java.util.*

val BgDarkPurple = Color(0xFF0E051F)
val BgMidPurple = Color(0xFF260938)
val CardCrimson = Color(0xFF5A0815)
val FennecGold = Color(0xFFFF8C00)
val TealCardBg = Color(0xFF084B5A)
val OnlineBadgeGreen = Color(0xFF006E51)

@Composable
fun PlaylistActivationScreen(
    onOpenPlaylist: () -> Unit,
    onAddManualClick: () -> Unit
) {
    val context = LocalContext.current
    val (deviceId, deviceKey) = remember { QrAndDeviceAuth.getOrCreateDeviceCredentials(context) }
    val qrBitmap = remember {
        QrAndDeviceAuth.generateActivationQrBitmap(
            portalBaseUrl = "https://fennecplayer.com/mylist",
            deviceId = deviceId,
            deviceKey = deviceKey
        )
    }
    val currentTime = remember { SimpleDateFormat("hh:mm a", Locale.US).format(Date()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(BgDarkPurple, BgMidPurple, BgDarkPurple)))
            .padding(28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Playlists", color = Color.LightGray, fontSize = 24.sp, fontWeight = FontWeight.Light)
            Text(currentTime, color = Color.LightGray, fontSize = 20.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 230.dp, height = 130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TealCardBg)
                        .clickable { onOpenPlaylist() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Fennec Default", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(OnlineBadgeGreen)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Currently Online", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.85f)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Scan QR Or Follow Instructions To Add/Manage Playlists",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onAddManualClick,
                    border = BorderStroke(1.5.dp, Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("OPEN MEDIA PLAYER FENNEC PLAYLISTS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Remote Playlist QR Code",
                        modifier = Modifier
                            .size(125.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(6.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Device ID", color = FennecGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(deviceId, color = Color.White, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Device Key:", color = FennecGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(deviceKey, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                "FENNEC PLAYER doesn't sell playlists or subscriptions",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Disclaimer: FENNEC PLAYER is a general media player and it does not include any content. You have to provide your own content.",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun MainDashboardScreen(
    onNavigateLive: () -> Unit,
    onNavigateMovies: () -> Unit,
    onNavigateSeries: () -> Unit,
    onChangePlaylist: () -> Unit,
    onReloadClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF280B30), Color(0xFF070410))))
            .padding(32.dp)
    ) {
        Text(
            text = "Current playlist expires: Unlimited",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Fennec Logo",
                tint = FennecGold,
                modifier = Modifier.size(44.dp)
            )
            Text("Fennec", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text("player", color = FennecGold, fontSize = 24.sp, fontWeight = FontWeight.Light)
        }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 36.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 240.dp, height = 236.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardCrimson)
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                    .clickable { onNavigateLive() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LiveTv, contentDescription = null, tint = Color.White, modifier = Modifier.size(84.dp))
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Live", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardSquareCard(title = "Movies", icon = Icons.Default.PlayCircleOutline, onClick = onNavigateMovies)
                DashboardSquareCard(title = "Account", icon = Icons.Default.PeopleOutline, onClick = {})
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardSquareCard(title = "Series", icon = Icons.Default.MovieFilter, onClick = onNavigateSeries)
                DashboardSquareCard(title = "Change Playlist", icon = Icons.Default.SyncAlt, onClick = onChangePlaylist)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardWideButton(title = "Settings", icon = Icons.Default.Settings, onClick = {})
                DashboardWideButton(title = "Reload", icon = Icons.Default.Refresh, onClick = onReloadClick)
                DashboardWideButton(title = "EXIT", icon = Icons.Default.ExitToApp, onClick = onExitClick)
            }
        }

        Text(
            text = "v1.0 Fennec",
            color = Color.LightGray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun DashboardSquareCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 140.dp, height = 110.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardCrimson.copy(alpha = 0.85f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(42.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun DashboardWideButton(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .size(width = 210.dp, height = 70.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = Color.White, fontSize = 15.sp)
    }
}

@Composable
fun LiveChannelsScreen(dao: FennecDao) {
    val context = LocalContext.current
    var categories by remember { mutableStateOf(listOf("All", "Favorites")) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedChannel by remember { mutableStateOf<PlaylistItemEntity?>(null) }

    LaunchedEffect(Unit) {
        val dbCats = dao.getCategories(ContentType.LIVE)
        categories = listOf("All", "Favorites") + dbCats
    }

    val pager = remember(selectedCategory) {
        Pager(PagingConfig(pageSize = 60, enablePlaceholders = false)) {
            dao.getPagedItems(ContentType.LIVE, selectedCategory, "")
        }
    }
    val lazyPagingChannels = pager.flow.collectAsLazyPagingItems()

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    LaunchedEffect(selectedChannel) {
        selectedChannel?.let { ch ->
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(ch.streamUrl)))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .background(Color(0xFF12121A))
                .padding(12.dp)
        ) {
            Text("Live Channels", color = Color.Yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Text(
                        text = cat,
                        color = if (isSelected) FennecGold else Color.White,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    )
                    Divider(color = Color.White.copy(alpha = 0.08f))
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(Color(0xFF161622))
        ) {
            items(lazyPagingChannels.itemCount) { index ->
                val channel = lazyPagingChannels[index]
                if (channel != null) {
                    val isCurrent = selectedChannel?.id == channel.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isCurrent) Color(0xFF2D68C4) else Color.Transparent)
                            .clickable { selectedChannel = channel }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${channel.channelNumber}", color = Color.LightGray, fontSize = 13.sp, modifier = Modifier.width(42.dp))
                        Icon(Icons.Default.Tv, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(channel.name, color = Color.White, fontSize = 14.sp, maxLines = 1)
                    }
                    Divider(color = Color.White.copy(alpha = 0.06f))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = selectedChannel?.name ?: "Select a channel to start streaming",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(0.35f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD))
                ) {
                    Text("Search", color = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD))
                ) {
                    Text("Favorite", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun VodMoviesGridScreen(dao: FennecDao, contentType: ContentType) {
    var categories by remember { mutableStateOf(listOf("Recently Viewed", "Favourites", "All")) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(contentType) {
        categories = listOf("Recently Viewed", "Favourites", "All") + dao.getCategories(contentType)
    }

    val pager = remember(selectedCategory, searchQuery, contentType) {
        Pager(PagingConfig(pageSize = 50, enablePlaceholders = false)) {
            dao.getPagedItems(contentType, selectedCategory, searchQuery)
        }
    }
    val lazyMovies = pager.flow.collectAsLazyPagingItems()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0716))
    ) {
        Column(
            modifier = Modifier
                .width(230.dp)
                .fillMaxHeight()
                .background(Color(0xFF140D26))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD91E36)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("F", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("FENNEC TV", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            LazyColumn {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Text(
                        text = category,
                        color = if (isSelected) Color.Yellow else Color.White,
                        fontSize = 14.sp,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = category }
                            .padding(vertical = 9.dp)
                    )
                    Divider(color = Color.White.copy(alpha = 0.08f))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    singleLine = true,
                    modifier = Modifier.width(340.dp)
                )
                Text(selectedCategory, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lazyMovies.itemCount) { index ->
                    val movie = lazyMovies[index]
                    if (movie != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF283566))
                                .clickable { }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(Color(0xFF1B223C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(movie.name, color = Color.White, fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
