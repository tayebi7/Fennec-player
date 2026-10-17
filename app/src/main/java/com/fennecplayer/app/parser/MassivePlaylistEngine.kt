package com.fennecplayer.app.parser

import com.fennecplayer.app.data.ChannelDao
import com.fennecplayer.app.data.ChannelEntity
import com.fennecplayer.app.data.StreamType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class MassivePlaylistEngine(private val dao: ChannelDao) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun loadXtreamPlaylist(
        hostUrl: String,
        username: String,
        password: String,
        onProgress: (Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        val cleanHost = hostUrl.trim().removeSuffix("/")
        val u = username.trim()
        val p = password.trim()
        dao.clearAll()
        var totalCount = 0

        val liveCats = fetchCategoryMap("$cleanHost/player_api.php?username=$u&password=$p&action=get_live_categories")
        totalCount += fetchXtreamSection(
            url = "$cleanHost/player_api.php?username=$u&password=$p&action=get_live_streams",
            type = StreamType.LIVE,
            categoryMap = liveCats,
            streamUrlBuilder = { id, ext -> "$cleanHost/live/$u/$p/$id.${ext.ifEmpty { "ts" }}" },
            onProgress = onProgress,
            currentTotal = totalCount
        )

        val vodCats = fetchCategoryMap("$cleanHost/player_api.php?username=$u&password=$p&action=get_vod_categories")
        totalCount += fetchXtreamSection(
            url = "$cleanHost/player_api.php?username=$u&password=$p&action=get_vod_streams",
            type = StreamType.MOVIE,
            categoryMap = vodCats,
            streamUrlBuilder = { id, ext -> "$cleanHost/movie/$u/$p/$id.${ext.ifEmpty { "mp4" }}" },
            onProgress = onProgress,
            currentTotal = totalCount
        )

        val seriesCats = fetchCategoryMap("$cleanHost/player_api.php?username=$u&password=$p&action=get_series_categories")
        fetchXtreamSection(
            url = "$cleanHost/player_api.php?username=$u&password=$p&action=get_series",
            type = StreamType.SERIES,
            categoryMap = seriesCats,
            streamUrlBuilder = { id, ext -> "$cleanHost/series/$u/$p/$id.${ext.ifEmpty { "mp4" }}" },
            onProgress = onProgress,
            currentTotal = totalCount
        )
    }

    private fun fetchCategoryMap(url: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyMap()
                val body = response.body?.string() ?: return emptyMap()
                val arr = JSONArray(body)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val id = obj.optString("category_id", "")
                    val name = obj.optString("category_name", "General")
                    if (id.isNotEmpty()) map[id] = name
                }
            }
        } catch (_: Exception) {}
        return map
    }

    private suspend fun fetchXtreamSection(
        url: String,
        type: StreamType,
        categoryMap: Map<String, String>,
        streamUrlBuilder: (String, String) -> String,
        onProgress: (Int) -> Unit,
        currentTotal: Int
    ): Int {
        var added = 0
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return 0
                val body = response.body?.string() ?: return 0
                val jsonArray = JSONArray(body)
                val batch = ArrayList<ChannelEntity>(500)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val name = obj.optString("name", "Stream $i")
                    val streamId = obj.optString("stream_id", obj.optString("series_id", "$i"))
                    val logo = obj.optString("stream_icon", obj.optString("cover", ""))
                    val categoryId = obj.optString("category_id", "")
                    val groupName = categoryMap[categoryId] ?: "General"
                    val ext = obj.optString("container_extension", "")

                    batch.add(
                        ChannelEntity(
                            title = name,
                            streamUrl = streamUrlBuilder(streamId, ext),
                            logoUrl = logo,
                            groupName = groupName,
                            streamType = type
                        )
                    )

                    if (batch.size >= 500) {
                        dao.insertBatch(batch.toList())
                        added += batch.size
                        onProgress(currentTotal + added)
                        batch.clear()
                    }
                }
                if (batch.isNotEmpty()) {
                    dao.insertBatch(batch.toList())
                    added += batch.size
                    onProgress(currentTotal + added)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return added
    }

    suspend fun downloadAndParseM3U(m3uUrl: String, onProgress: (Int) -> Unit) = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(m3uUrl.trim()).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP Error: ${response.code}")
            val bodyStream = response.body?.byteStream() ?: throw Exception("Empty Stream")
            dao.clearAll()

            BufferedReader(InputStreamReader(bodyStream), 65536).use { reader ->
                val batch = ArrayList<ChannelEntity>(1000)
                var count = 0
                var currentTitle = ""
                var currentLogo = ""
                var currentGroup = "General"
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    val trimmed = line!!.trim()
                    if (trimmed.isEmpty()) continue

                    if (trimmed.startsWith("#EXTINF:")) {
                        currentLogo = extractAttribute(trimmed, "tvg-logo")
                        currentGroup = extractAttribute(trimmed, "group-title").ifEmpty { "General" }
                        val commaIndex = trimmed.lastIndexOf(',')
                        currentTitle = if (commaIndex != -1) trimmed.substring(commaIndex + 1).trim() else "Channel"
                    } else if (!trimmed.startsWith("#")) {
                        val streamType = classifyStream(trimmed, currentGroup)
                        batch.add(
                            ChannelEntity(
                                title = currentTitle.ifEmpty { "Stream" },
                                streamUrl = trimmed,
                                logoUrl = currentLogo,
                                groupName = currentGroup,
                                streamType = streamType
                            )
                        )
                        if (batch.size >= 1000) {
                            dao.insertBatch(batch.toList())
                            count += batch.size
                            onProgress(count)
                            batch.clear()
                        }
                        currentTitle = ""
                        currentLogo = ""
                    }
                }
                if (batch.isNotEmpty()) {
                    dao.insertBatch(batch.toList())
                    count += batch.size
                    onProgress(count)
                }
            }
        }
    }

    private fun extractAttribute(line: String, attr: String): String {
        val key = "$attr=\""
        val start = line.indexOf(key, ignoreCase = true)
        if (start == -1) return ""
        val valStart = start + key.length
        val end = line.indexOf('"', valStart)
        return if (end != -1) line.substring(valStart, end) else ""
    }

    private fun classifyStream(url: String, group: String): StreamType {
        val g = group.lowercase()
        val u = url.lowercase()
        return when {
            u.contains("/series/") || g.contains("series") || g.contains("مسلسلات") -> StreamType.SERIES
            u.contains("/movie/") || u.endsWith(".mp4") || u.endsWith(".mkv") || g.contains("movie") || g.contains("vod") || g.contains("أفلام") || g.contains("افلام") -> StreamType.MOVIE
            else -> StreamType.LIVE
        }
    }
}
