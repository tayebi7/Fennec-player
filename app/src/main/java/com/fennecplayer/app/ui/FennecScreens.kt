@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
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

// ألوان IBO Player الأصلية المستخرجة من الصور المرفقة
val IboBgDark = Color(0xFF0A0714)
val IboBgGlow = Color(0xFF280D2B)
val IboCrimsonCard = Color(0xFF6E0A19)
val IboRightBtnBg = Color(0xFF2B2129)
val IboPurpleBg = Color(0xFF160E2E)
val IboPurpleCard = Color(0xFF261E47)
val IboPurpleSelected = Color(0xFF53408C)
val IboActionBtnPurple = Color(0xFF67549C)
val IboGoldText = Color(0xFFFFC928)
val IboVodTitleBar = Color(0xFF324185)
val IboVodSelectedYellow = Color(0xFFFFFF00)

// =========================================================================
// 1. الواجهة الرئيسية (مطابقة تماماً للصورة 8: IBO Player v3.7)
// =========================================================================
@Composable
fun IboLauncherScreen(
    liveCount: Int,
    movieCount: Int,
    seriesCount: Int,
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
            .background(
                Brush.radialGradient(
                    colors = listOf(IboBgGlow, IboBgDark),
                    radius = 1200f
                )
            )
            .padding(horizontal = 36.dp, vertical = 24.dp)
    ) {
        // النص العلوي على اليمين
        Text(
            text = "Current playlist expires: unlimited",
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // شعار التطبيق في الأعلى والمنتصف (مثلث أحمر + اسم التطبيق)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color(0xFFD50000),
                modifier = Modifier
                    .size(58.dp)
                    .offset(x = (-18).dp)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Fennec",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "player",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }

        // شبكة الأزرار في المنتصف (4 أعمدة مطابقة للصورة)
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 38.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. المربع الكبير (Live) مع الإطار الأبيض
            Box(
                modifier = Modifier
                    .size(235.dp, 208.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(IboCrimsonCard)
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                    .clickable { onNavigateLive() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = "Live",
                        tint = Color.White,
                        modifier = Modifier.size(82.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Live",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. العمود الثاني (Movies + Account)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                IboRedSmallTile(
                    title = "Movies",
                    icon = Icons.Default.PlayCircleOutline,
                    onClick = onNavigateMovies
                )
                IboRedSmallTile(
                    title = "Account",
                    icon = Icons.Default.PeopleOutline,
                    onClick = onNavigateAccount
                )
            }

            // 3. العمود الثالث (Series + Change Playlist)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                IboRedSmallTile(
                    title = "Series",
                    icon = Icons.Default.MovieCreation,
                    onClick = onNavigateSeries
                )
                IboRedSmallTile(
                    title = "Change Playlist",
                    icon = Icons.Default.SwapHoriz,
                    onClick = onNavigatePlaylists
                )
            }

            // 4. العمود الرابع المستطيل (Settings + Reload + EXIT)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                IboDarkHorizontalBtn(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    onClick = onNavigateAccount
                )
                IboDarkHorizontalBtn(
                    title = "Reload",
                    icon = Icons.Default.Refresh,
                    onClick = onReload
                )
                IboDarkHorizontalBtn(
                    title = "EXIT",
                    icon = Icons.Default.ExitToApp,
                    onClick = onExit
                )
            }
        }

        // رقم الإصدار في الأسفل على اليمين
        Text(
            text = "v3.7",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun IboRedSmallTile(title: String, icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(135.dp, 96.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(IboCrimsonCard)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun IboDarkHorizontalBtn(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .size(195.dp, 58.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(IboRightBtnBg.copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// =========================================================================
// 2. واجهة التلفزة المباشرة (مطابقة تماماً للصورة 3: الثيم البنفسجي مع المعاينة)
// =========================================================================
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
            .background(IboPurpleBg)
    ) {
        // الشريط العلوي (الرئيسية | مباشر | أفلام | مسلسلات + كبسولة البحث البيضاوية)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "الرئيسية",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    text = "مباشر",
                    color = IboGoldText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "أفلام",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateMovies() }
                )
                Text(
                    text = "مسلسلات",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateSeries() }
                )
            }

            // كبسولة البحث بإطار أبيض دائري مثل الصورة 3
            Box(
                modifier = Modifier
                    .width(340.dp)
                    .height(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF282042))
                    .border(1.5.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    BasicTextFieldWithHint(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        hint = "بحث"
                    )
                }
            }

            // أيقونة التلفاز الصغيرة في أقصى اليمين
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF251A3E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFF00E5FF))
            }
        }

        Divider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)

        // الأعمدة الثلاثة (المجموعات + القنوات + نافذة المشاهدة المصغرة)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. عمود الباقات مع عدد القنوات
            LazyColumn(
                modifier = Modifier
                    .weight(0.26f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val allCats = listOf("All", "Favorites") + categories
                itemsIndexed(allCats) { _, cat ->
                    val isSelected = cat == selectedCategory
                    val displayCat = when (cat) {
                        "All" -> "الكل"
                        "Favorites" -> "المفضلة"
                        else -> cat
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) IboPurpleSelected else IboPurpleCard)
                            .clickable { onSelectCategory(cat) }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = displayCat,
                            color = if (isSelected) IboGoldText else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (cat == "All") {
                            Text(
                                text = "${channels.size}",
                                color = Color.LightGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 2. عمود القنوات المرقمة مع الشعارات
            val filteredChannels = if (searchQuery.isEmpty()) channels else channels.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(0.36f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(filteredChannels) { index, channel ->
                    val isSelected = selectedChannel?.id == channel.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) IboPurpleSelected else IboPurpleCard)
                            .clickable {
                                if (isSelected) onOpenFullScreen() else onSelectChannel(channel)
                            }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            modifier = Modifier.width(28.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(38.dp, 26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = channel.logoUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = channel.title,
                            color = if (isSelected) IboGoldText else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. العمود الأيمن: شاشة رؤية القناة المصغرة + اسم القناة + الأزرار البنفسجية
            Column(
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
                    .padding(horizontal = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // شاشة الفيديو المصغرة (Mini Preview)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black)
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
                                Text("اختر قناة لمعاينتها", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // اسم القناة المختارة والوقت كما في الصورة 3
                    Text(
                        text = selectedChannel?.title ?: "لا توجد قناة محددة",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "8:00pm - Live Broadcast",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // زرا (ملء الشاشة) و (إضافة للمفضلة) بنفس تصميم ولون الصورة 3
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onOpenFullScreen,
                        enabled = selectedChannel != null,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("ملء الشاشة", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { selectedChannel?.let { onToggleFavorite(it) } },
                        enabled = selectedChannel != null,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedChannel?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("إضافة للمفضلة", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BasicTextFieldWithHint(value: String, onValueChange: (String) -> Unit, hint: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (value.isEmpty()) {
            Text(text = hint, color = Color.LightGray, fontSize = 14.sp)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// =========================================================================
// 3. شاشة الأفلام والمسلسلات (مطابقة للصورة 6: البوسترات والشريط الأصفر عند التحديد)
// =========================================================================
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
    var highlightedId by remember { mutableStateOf<Long?>(null) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0B16))
    ) {
        // القائمة الجانبية اليسرى مع الشعار الأحمر في الأعلى
        Column(
            modifier = Modifier
                .weight(0.23f)
                .fillMaxHeight()
                .background(Color(0xFF140E20))
                .padding(12.dp)
        ) {
            // شعار علوي مع زر الرجوع
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD50000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.width(10.dp))
                Text("TV", color = Color.Gray, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                val allCats = listOf("All", "Favorites") + categories
                itemsIndexed(allCats) { _, cat ->
                    val isSelected = cat == selectedCategory
                    val label = when (cat) {
                        "All" -> "Recently Viewed"
                        "Favorites" -> "Favourites"
                        else -> cat
                    }
                    Column {
                        Text(
                            text = label,
                            color = if (isSelected) IboGoldText else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCategory(cat) }
                                .padding(vertical = 10.dp, horizontal = 6.dp)
                        )
                        Divider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)
                    }
                }
            }
        }

        // القسم الأيمن: شريط البحث العلوي + شبكة البوسترات (5 أعمدة)
        Column(
            modifier = Modifier
                .weight(0.77f)
                .fillMaxHeight()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(280.dp)
                        .padding(bottom = 4.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    BasicTextFieldWithHint(
                        value = search,
                        onValueChange = { search = it },
                        hint = "Search"
                    )
                }

                Text(
                    text = if (selectedCategory == "All") title else selectedCategory,
                    color = Color.LightGray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val filtered = if (search.isEmpty()) items else items.filter {
                it.title.contains(search, ignoreCase = true)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { vod ->
                    val isFocused = highlightedId == vod.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1B192E))
                            .border(
                                width = if (isFocused) 2.dp else 0.dp,
                                color = if (isFocused) IboVodSelectedYellow else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                if (isFocused) onSelectVod(vod) else highlightedId = vod.id
                            }
                    ) {
                        AsyncImage(
                            model = vod.logoUrl,
                            contentDescription = vod.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(165.dp)
                                .background(Color.Black)
                        )
                        // الشريط السفلي للبوستر (أزرق في الوضع العادي، أصفر فاقع عند التحديد مثل الصورة 6)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .background(if (isFocused) IboVodSelectedYellow else IboVodTitleBar)
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = vod.title,
                                color = if (isFocused) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. مشغل الفيديو بملء الشاشة مع تغيير القناة وتسريع الفيلم والإخفاء التلقائي
// =========================================================================
@Composable
fun FullScreenPlayerWithAutoHideControls(
    channel: ChannelEntity,
    exoPlayer: ExoPlayer,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    onExitFullScreen: () -> Unit
) {
    var controlsVisible by remember { mutableStateOf(true) }

    // إخفاء تلقائي بعد 4 ثوانٍ
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
                        colors = ButtonDefaults.buttonColors(containerColor = IboCrimsonCard)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("رجوع")
                    }
                    Text(
                        text = channel.title,
                        color = IboGoldText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // الشريط السفلي (تغيير القناة + ترجيع وتقديم الفيلم 10 ثوانٍ)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(IboPurpleBg.copy(alpha = 0.92f))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            controlsVisible = true
                            onPrevChannel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IboCrimsonCard)
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
                        colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple)
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
                            imageVector = if (exoPlayer.isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            tint = IboGoldText,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Button(
                        onClick = {
                            controlsVisible = true
                            exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple)
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
                        colors = ButtonDefaults.buttonColors(containerColor = IboCrimsonCard)
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

// =========================================================================
// 5. واجهة إدخال Xtream / M3U و QR Code (مطابقة للصورة 4 تماماً)
// =========================================================================
@Composable
fun IboPlaylistAndQrScreen(
    isLoading: Boolean,
    loadedCount: Int,
    errorMessage: String?,
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

    var showInputModal by remember { mutableStateOf(false) }
    var tabIndex by remember { mutableStateOf(0) }
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var m3uUrl by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050408))
            .padding(28.dp)
    ) {
        // معلومات MAC & Key العلوية الخافتة على اليمين كما في الصورة 4
        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Mac Address: $macAddress", color = Color.Gray.copy(alpha = 0.5f), fontSize = 13.sp)
            Text("Device Key: $deviceKey", color = Color.Gray.copy(alpha = 0.5f), fontSize = 13.sp)
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // النصف الأيسر: إما شاشة معلومات MAC أو حقول إدخال Xtream / M3U
            Column(
                modifier = Modifier.weight(0.58f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!showInputModal) {
                    Text(
                        text = "Manage Your Playlists",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Add Xtream Codes or M3U Playlist directly or via website.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "https://iboproapp.com/",
                        color = IboGoldText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Mac Address", color = Color.White, fontSize = 15.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(macAddress, color = IboGoldText, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Device Key", color = Color.White, fontSize = 15.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(deviceKey, color = IboGoldText, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(26.dp))

                    // الأزرار البيضاوية البنفسجية المطابقة للصورة 4
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Button(
                            onClick = { showInputModal = true },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple),
                            modifier = Modifier.size(165.dp, 44.dp)
                        ) {
                            Text("ADD PLAYLIST", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onBack,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple),
                            modifier = Modifier.size(145.dp, 44.dp)
                        ) {
                            Text("CANCEL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // نموذج إدخال Xtream / M3U المباشر داخل التطبيق
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(IboPurpleCard)
                            .padding(18.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { tabIndex = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = if (tabIndex == 0) IboCrimsonCard else Color.DarkGray)
                            ) {
                                Text("Xtream Codes")
                            }
                            Button(
                                onClick = { tabIndex = 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = if (tabIndex == 1) IboCrimsonCard else Color.DarkGray)
                            ) {
                                Text("M3U Link")
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        errorMessage?.let {
                            Text(it, color = Color.Red, fontSize = 12.sp)
                        }

                        if (tabIndex == 0) {
                            OutlinedTextField(
                                value = serverUrl,
                                onValueChange = { serverUrl = it },
                                label = { Text("Server URL (http://host:port)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Username") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { if (serverUrl.isNotBlank()) onLoadXtream(serverUrl, username, password) },
                                    enabled = !isLoading,
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple)
                                ) {
                                    Text(if (isLoading) "Loading ($loadedCount)..." else "SAVE XTREAM")
                                }
                                Button(
                                    onClick = { showInputModal = false },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                ) {
                                    Text("BACK")
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = m3uUrl,
                                onValueChange = { m3uUrl = it },
                                label = { Text("M3U URL") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { if (m3uUrl.isNotBlank()) onLoadM3u(m3uUrl) },
                                    enabled = !isLoading,
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = IboActionBtnPurple)
                                ) {
                                    Text(if (isLoading) "Loading ($loadedCount)..." else "SAVE M3U")
                                }
                                Button(
                                    onClick = { showInputModal = false },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                ) {
                                    Text("BACK")
                                }
                            }
                        }
                    }
                }
            }

            // النصف الأيمن: الشعار + مربع الـ QR Code مع الشعار في وسطه تماماً مثل الصورة 4
            Column(
                modifier = Modifier.weight(0.42f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFFD50000),
                        modifier = Modifier.size(38.dp)
                    )
                    Text("Fennec", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text(" player", color = Color.LightGray, fontSize = 16.sp)
                }

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .size(185.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                    // شعار مصغر في منتصف الـ QR Code مثل الصورة 4
                    Box(
                        modifier = Modifier
                            .size(44.dp, 26.dp)
                            .background(Color.White, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Fennec", color = Color(0xFFD50000), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Scan QR to add playlist",
                    color = IboGoldText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
