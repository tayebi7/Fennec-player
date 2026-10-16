package com.fennecplayer.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.fennecplayer.app.data.ChannelEntity
import com.fennecplayer.app.utils.QrAndDeviceAuth
import kotlinx.coroutines.delay

val IboBgDark = Color(0xFF0B0716)
val IboBgCenter = Color(0xFF220B24)
val IboCrimson = Color(0xFF6B0918)
val IboHighlightBlue = Color(0xFF3880FF)
val IboYellow = Color(0xFFFFD700)

// 1. الواجهة الرئيسية مطابقة لتصميم IBO Player مع اسم Fennec Player
@Composable
fun IboLauncherScreen(
    onNavigateLive: () -> Unit,
    onNavigateMovies: () -> Unit,
    onNavigateSeries: () -> Unit,
    onNavigatePlaylists: () -> Unit,
    onNavigateAccount: () -> Unit,
    onReload: () -> Unit,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(IboBgCenter, IboBgDark), radius = 1100f))
            .padding(28.dp)
    ) {
        Text(
            text = "Current playlist expires: unlimited",
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // شعار Fennec Player بتصميم المثلث الأحمر مثل IBO Player
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Red, modifier = Modifier.size(42.dp))
            Text("Fennec", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(" player", color = Color.LightGray, fontSize = 22.sp)
        }

        // شبكة الأزرار المطابقة للصورة
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 36.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // مربع Live الكبير المحدد بإطار أبيض
            Box(
                modifier = Modifier
                    .size(230.dp, 210.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IboCrimson)
                    .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                    .clickable { onNavigateLive() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LiveTv, contentDescription = "Live", tint = Color.White, modifier = Modifier.size(76.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Live", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            // العمود الثاني: Movies & Account
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                IboSquareTile("Movies", Icons.Default.PlayCircleOutline, IboCrimson, onNavigateMovies)
                IboSquareTile("Account", Icons.Default.SupervisorAccount, IboCrimson, onNavigateAccount)
            }

            // العمود الثالث: Series & Change Playlist
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                IboSquareTile("Series", Icons.Default.MovieFilter, IboCrimson, onNavigateSeries)
                IboSquareTile("Change Playlist", Icons.Default.SyncAlt, IboCrimson, onNavigatePlaylists)
            }

            // العمود الرابع: Settings, Reload, EXIT
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IboWideActionBtn("Settings", Icons.Default.Settings, onNavigateAccount)
                IboWideActionBtn("Reload", Icons.Default.Refresh, onReload)
                IboWideActionBtn("EXIT", Icons.Default.ExitToApp, onExit)
            }
        }

        Text(
            text = "v3.7",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun IboSquareTile(title: String, icon: ImageVector, bgColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(145.dp, 97.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(38.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun IboWideActionBtn(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .size(185.dp, 60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2B222C).copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(14.dp))
        Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

// 2. شاشة التلفزة المباشرة مع شاشة المعاينة المصغرة (Mini Video Player)
@Composable
fun IboLiveTvScreen(
    categories: List<String>,
    channels: List<ChannelEntity>,
    selectedCategory: String,
    selectedChannel: ChannelEntity?,
    exoPlayer: ExoPlayer,
    onSelectCategory: (String) -> Unit,
    onSelectChannel: (ChannelEntity) -> Unit,
    onToggleFavorite: (ChannelEntity) -> Unit,
    onOpenFullScreen: () -> Unit,
    onNavigateMovies: () -> Unit,
    onNavigateSeries: () -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IboBgDark)
    ) {
        // الشريط العلوي مطابق للصورة (الرئيسية | مباشر | أفلام | مسلسلات + شريط البحث)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF160F29))
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                Text("الرئيسية", color = Color.White, fontSize = 16.sp, modifier = Modifier.clickable { onBack() })
                Text("مباشر", color = IboYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("أفلام", color = Color.White, fontSize = 16.sp, modifier = Modifier.clickable { onNavigateMovies() })
                Text("مسلسلات", color = Color.White, fontSize = 16.sp, modifier = Modifier.clickable { onNavigateSeries() })
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث...", color = Color.LightGray, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(300.dp)
                    .height(48.dp)
            )
        }

        // 3 أعمدة: الفئات + القنوات + شاشة معاينة القناة
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. عمود الفئات
            LazyColumn(
                modifier = Modifier
                    .weight(0.24f)
                    .fillMaxHeight()
                    .background(Color(0xFF120D22))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val allCats = listOf("All", "Favorites") + categories
                itemsIndexed(allCats) { _, cat ->
                    val isSelected = cat == selectedCategory
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF4C3B78) else Color(0xFF1F1936))
                            .clickable { onSelectCategory(cat) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (cat == "All") "الكل" else if (cat == "Favorites") "المفضلة" else cat,
                            color = if (isSelected) IboYellow else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2. عمود القنوات
            val filteredList = if (searchQuery.isEmpty()) channels else channels.filter { it.title.contains(searchQuery, true) }
            LazyColumn(
                modifier = Modifier
                    .weight(0.36f)
                    .fillMaxHeight()
                    .background(Color(0xFF17112B))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                itemsIndexed(filteredList) { idx, ch ->
                    val isPlaying = selectedChannel?.id == ch.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isPlaying) IboHighlightBlue else Color(0xFF231B3D))
                            .clickable {
                                if (isPlaying) onOpenFullScreen() else onSelectChannel(ch)
                            }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${idx + 1}", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.width(30.dp))
                        AsyncImage(
                            model = ch.logoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(34.dp, 24.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = ch.title,
                            color = if (isPlaying) IboYellow else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. العمود الأيمن: شاشة معاينة القناة المصغرة + أزرار ملء الشاشة والمفضلة
            Column(
                modifier = Modifier
                    .weight(0.40f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .clickable { if (selectedChannel != null) onOpenFullScreen() }
                    ) {
                        if (selectedChannel != null) {
                            AndroidView(
                                factory = { ctx ->
                                    PlayerView(ctx).apply {
                                        player = exoPlayer
                                        useController = false
                                    }
                                },
                                update = { view -> view.player = exoPlayer },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("اختر قناة لرؤية البث المباشر", color = Color.Gray)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = selectedChannel?.title ?: "لا توجد قناة محددة",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenFullScreen,
                        enabled = selectedChannel != null,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E4B8B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("ملء الشاشة")
                    }

                    Button(
                        onClick = { selectedChannel?.let { onToggleFavorite(it) } },
                        enabled = selectedChannel != null,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E4B8B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("إضافة للمفضلة")
                    }
                }
            }
        }
    }
}

// 3. مشغل الفيديو بملء الشاشة مع زر تغيير القناة وتسريع الفيلم والإخفاء التلقائي
@Composable
fun FullScreenPlayerWithAutoHideControls(
    channel: ChannelEntity,
    exoPlayer: ExoPlayer,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    onExitFullScreen: () -> Unit
) {
    var controlsVisible by remember { mutableStateOf(true) }

    // إخفاء الأزرار تلقائياً بعد 4 ثوانٍ
    LaunchedEffect(controlsVisible, channel) {
        if (controlsVisible) {
            delay(4000)
            controlsVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { controlsVisible = !controlsVisible }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            update = { view -> view.player = exoPlayer },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                // الشريط العلوي
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onExitFullScreen,
                        colors = ButtonDefaults.buttonColors(containerColor = IboCrimson)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("رجوع")
                    }
                    Text(channel.title, color = IboYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                // الشريط السفلي (تغيير القناة + ترجيع وتقديم الفيلم)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color(0xFF140D26).copy(alpha = 0.9f))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            controlsVisible = true
                            onPrevChannel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IboCrimson)
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("القناة السابقة")
                    }

                    Button(
                        onClick = {
                            controlsVisible = true
                            exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("ترجيع -10s")
                    }

                    IconButton(
                        onClick = {
                            controlsVisible = true
                            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        }
                    ) {
                        Icon(
                            if (exoPlayer.isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            tint = IboYellow,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Button(
                        onClick = {
                            controlsVisible = true
                            exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("تسريع +10s")
                    }

                    Button(
                        onClick = {
                            controlsVisible = true
                            onNextChannel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IboCrimson)
                    ) {
                        Text("القناة التالية")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.SkipNext, contentDescription = null)
                    }
                }
            }
        }
    }
}

// 4. شاشة شبكة الأفلام والمسلسلات مع معاينة البوستر قبل التشغيل
@Composable
fun IboVodGridScreen(
    title: String,
    categories: List<String>,
    items: List<ChannelEntity>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onSelectVod: (ChannelEntity) -> Unit,
    onBack: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var previewVod by remember { mutableStateOf<ChannelEntity?>(null) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(IboBgDark)
    ) {
        // قائمة فئات الأفلام أو المسلسلات على اليسار
        Column(
            modifier = Modifier
                .weight(0.24f)
                .fillMaxHeight()
                .background(Color(0xFF120C1E))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = IboYellow)
                Spacer(Modifier.width(8.dp))
                Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val allCats = listOf("All", "Favorites") + categories
                itemsIndexed(allCats) { _, cat ->
                    val selected = cat == selectedCategory
                    Text(
                        text = if (cat == "All") "Recently Viewed / All" else cat,
                        color = if (selected) IboYellow else Color.LightGray,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selected) IboCrimson else Color.Transparent)
                            .clickable { onSelectCategory(cat) }
                            .padding(10.dp)
                    )
                }
            }
        }

        // شبكة بوسترات الأفلام/المسلسلات
        Column(
            modifier = Modifier
                .weight(0.76f)
                .fillMaxHeight()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    singleLine = true,
                    modifier = Modifier.width(320.dp)
                )
                Text(selectedCategory, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            // نافذة معاينة المحتوى المختار قبل تشغيله
            previewVod?.let { selected ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1633))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        AsyncImage(
                            model = selected.logoUrl,
                            contentDescription = selected.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp, 95.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(selected.title, color = IboYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("الفئة: ${selected.groupName}", color = Color.LightGray, fontSize = 13.sp)
                        }
                    }
                    Button(
                        onClick = { onSelectVod(selected) },
                        colors = ButtonDefaults.buttonColors(containerColor = IboCrimson)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("مشاهدة الآن")
                    }
                }
            }

            val filtered = if (search.isEmpty()) items else items.filter { it.title.contains(search, true) }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 135.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { vod ->
                    val isSelected = previewVod?.id == vod.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF231F3A))
                            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) IboYellow else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable {
                                if (isSelected) onSelectVod(vod) else previewVod = vod
                            }
                    ) {
                        AsyncImage(
                            model = vod.logoUrl,
                            contentDescription = vod.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(175.dp)
                                .background(Color.Black)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) IboYellow else Color(0xFF2E3C7E))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = vod.title,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// 5. شاشة إدخال Xtream و M3U ورمز QR Code مطابقة لـ IBO Player
@Composable
fun IboPlaylistAndQrScreen(
    isLoading: Boolean,
    loadedCount: Int,
    onLoadXtream: (String, String, String) -> Unit,
    onLoadM3u: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val macAddress = remember { QrAndDeviceAuth.getVirtualMacAddress(context) }
    val deviceKey = remember { QrAndDeviceAuth.getDeviceKey(context) }
    val qrBitmap = remember {
        QrAndDeviceAuth.generateQrBitmap("https://iboproapp.com/?mac=$macAddress&key=$deviceKey")
    }

    var tabIndex by remember { mutableStateOf(0) } // 0 = Xtream, 1 = M3U
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var m3uUrl by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07050A))
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // القسم الأيسر: خانة إدخال Xtream و M3U
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Playlists Management (Xtream / M3U)", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { tabIndex = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (tabIndex == 0) IboCrimson else Color.DarkGray)
                ) {
                    Text("Xtream Codes")
                }
                Button(
                    onClick = { tabIndex = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (tabIndex == 1) IboCrimson else Color.DarkGray)
                ) {
                    Text("M3U Link")
                }
            }

            Spacer(Modifier.height(12.dp))

            if (tabIndex == 0) {
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("Server URL (http://host:port)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { if (serverUrl.isNotBlank()) onLoadXtream(serverUrl, username, password) },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E4B8B))
                    ) {
                        Text(if (isLoading) "Loading ($loadedCount)..." else "ADD XTREAM PLAYLIST")
                    }
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E4B8B))
                    ) {
                        Text("CANCEL")
                    }
                }
            } else {
                OutlinedTextField(
                    value = m3uUrl,
                    onValueChange = { m3uUrl = it },
                    label = { Text("M3U URL (http://.../get.php?...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { if (m3uUrl.isNotBlank()) onLoadM3u(m3uUrl) },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E4B8B))
                    ) {
                        Text(if (isLoading) "Loading ($loadedCount)..." else "ADD M3U PLAYLIST")
                    }
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E4B8B))
                    ) {
                        Text("CANCEL")
                    }
                }
            }
        }

        // القسم الأيمن: Mac Address + Device Key + QR Code مطابق لصورة IBO Player
        Column(
            modifier = Modifier.weight(0.42f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Red, modifier = Modifier.size(32.dp))
                Text("Fennec", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(" player", color = Color.LightGray, fontSize = 18.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text("https://iboproapp.com/", color = IboYellow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Mac Address", color = Color.White, fontSize = 13.sp)
            Text(macAddress, color = IboYellow, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Device Key", color = Color.White, fontSize = 13.sp)
            Text(deviceKey, color = IboYellow, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(6.dp))
            Text("Scan QR to add playlist", color = IboYellow, fontSize = 13.sp)
        }
    }
}
