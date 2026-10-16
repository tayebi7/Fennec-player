package com.fennecplayer.app.parser

import com.fennecplayer.app.data.ContentType
import com.fennecplayer.app.data.PlaylistItem
import java.util.regex.Pattern

object MassivePlaylistEngine {
    fun parseM3U(content: String): List<PlaylistItem> {
        val items = mutableListOf<PlaylistItem>()
        val lines = content.split("\n")
        var currentTitle = ""
        var currentLogo = ""
        var currentGroup = "Uncategorized"
        var currentType = ContentType.LIVE

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                currentTitle = extractAttr(trimmed, "tvg-name") ?: trimmed.substringAfterLast(",").trim()
                currentLogo = extractAttr(trimmed, "tvg-logo") ?: ""
                currentGroup = extractAttr(trimmed, "group-title") ?: "General"
                
                currentType = when {
                    currentGroup.contains("movie", true) || currentGroup.contains("أفلام", true) -> ContentType.MOVIE
                    currentGroup.contains("series", true) || currentGroup.contains("مسلسلات", true) -> ContentType.SERIES
                    else -> ContentType.LIVE
                }
            } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                items.add(
                    PlaylistItem(
                        id = java.util.UUID.randomUUID().toString(),
                        name = currentTitle,
                        url = trimmed,
                        logo = currentLogo,
                        category = currentGroup,
                        type = currentType
                    )
                )
            }
        }
        return items
    }

    private fun extractAttr(row: String, attr: String): String? {
        val p = Pattern.compile("$attr=\"([^\"]*)\"")
        val m = p.matcher(row)
        return if (m.find()) m.group(1) else null
    }
}
