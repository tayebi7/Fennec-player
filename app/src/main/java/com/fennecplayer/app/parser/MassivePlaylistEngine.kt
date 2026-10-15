package com.fennecplayer.app.parser

import com.fennecplayer.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * محرك فائق السرعة لقراءة ملفات M3U وحسابات Xtream Codes التي تتجاوز 60,000 قناة
 * يعتمد على التدفق المباشر (Buffered Streaming) والإدخال المجمع (Batch Transactions)
 */
class MassivePlaylistEngine(private val dao: FennecDao) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // 1. قراءة ملفات M3U الضخمة
    suspend fun loadM3UPlaylist(m3uUrl: String, onProgress: (Int) -> Unit) = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(m3uUrl).build()
        val response = httpClient.newCall(request).execute()
        val bodyStream = response.body?.byteStream() ?: return@withContext

        dao.clearAllItems()

        val reader = BufferedReader(InputStreamReader(bodyStream, Charsets.UTF_8), 65536)
        val batch = ArrayList<PlaylistItemEntity>(2000)

        var line: String?
        var currentName = ""
        var currentLogo = ""
        var currentGroup = "Uncategorized"
        var counter = 0

        val logoRegex = Regex("""tvg-logo="([^"]*)"""")
        val groupRegex = Regex("""group-title="([^"]*)"""")

        while (reader.readLine().also { line = it } != null) {
            val trimmed = line!!.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                currentName = trimmed.substringAfterLast(",", "Channel ${counter + 1}").trim()
                currentLogo = logoRegex.find(trimmed)?.groupValues?.get(1) ?: ""
                currentGroup = groupRegex.find(trimmed)?.groupValues?.get(1)?.ifEmpty { "General" } ?: "General"
            } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                counter++
                val contentType = when {
                    trimmed.contains("/movie/", ignoreCase = true) || trimmed.endsWith(".mp4") || trimmed.endsWith(".mkv") -> ContentType.MOVIE
                    trimmed.contains("/series/", ignoreCase = true) -> ContentType.SERIES
                    else -> ContentType.LIVE
                }

                batch.add(
                    PlaylistItemEntity(
                        channelNumber = counter,
                        name = currentName,
                        streamUrl = trimmed,
                        logoUrl = currentLogo,
                        categoryName = currentGroup,
                        contentType = contentType
                    )
                )

                // حفظ كل 2000 قناة دفعة واحدة لتفريغ الرام تماماً
                if (batch.size >= 2000) {
                    dao.insertChannelsBatch(batch)
                    batch.clear()
                    onProgress(counter)
                }
            }
        }

        if (batch.isNotEmpty()) {
            dao.insertChannelsBatch(batch)
            onProgress(counter)
        }
        reader.close()
    }

    // 2. قراءة حسابات Xtream Codes API (قنوات حية + أفلام + مسلسلات)
    suspend fun loadXtreamCodes(
        serverUrl: String,
        username: String,
        password: String,
        onProgress: (Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        val cleanHost = serverUrl.trimEnd('/')
        dao.clearAllItems()
        var totalInserted = 0

        // أ) جلب القنوات الحية Live Streams
        val liveApiUrl = "$cleanHost/player_api.php?username=$username&password=$password&action=get_live_streams"
        totalInserted += fetchAndInsertXtreamJson(liveApiUrl, cleanHost, username, password, ContentType.LIVE, totalInserted, onProgress)

        // ب) جلب الأفلام VOD Movies
        val vodApiUrl = "$cleanHost/player_api.php?username=$username&password=$password&action=get_vod_streams"
        totalInserted += fetchAndInsertXtreamJson(vodApiUrl, cleanHost, username, password, ContentType.MOVIE, totalInserted, onProgress)
    }

    private suspend fun fetchAndInsertXtreamJson(
        apiUrl: String,
        host: String,
        user: String,
        pass: String,
        type: ContentType,
        startCounter: Int,
        onProgress: (Int) -> Unit
    ): Int {
        val req = Request.Builder().url(apiUrl).build()
        val res = httpClient.newCall(req).execute()
        val jsonStr = res.body?.string() ?: return 0

        val jsonArray = JSONArray(jsonStr)
        val batch = ArrayList<PlaylistItemEntity>(2000)
        var count = startCounter

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val streamId = obj.optInt("stream_id")
            val name = obj.optString("name", "Stream $streamId")
            val icon = obj.optString("stream_icon", "")
            val catId = obj.optString("category_id", "General")
            val ext = obj.optString("container_extension", "ts")

            val streamUrl = if (type == ContentType.LIVE) {
                "$host/live/$user/$pass/$streamId.ts"
            } else {
                "$host/movie/$user/$pass/$streamId.$ext"
            }

            count++
            batch.add(
                PlaylistItemEntity(
                    channelNumber = count,
                    name = name,
                    streamUrl = streamUrl,
                    logoUrl = icon,
                    categoryName = "Category $catId",
                    contentType = type
                )
            )

            if (batch.size >= 2000) {
                dao.insertChannelsBatch(batch)
                batch.clear()
                onProgress(count)
            }
        }
        if (batch.isNotEmpty()) {
            dao.insertChannelsBatch(batch)
            onProgress(count)
        }
        return count - startCounter
    }
}
