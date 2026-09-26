package org.orion.browser.downloader

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

data class DetectedMedia(
    val url: String,
    val mimeType: String,
    val title: String,
    val isHls: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

object MediaSniffer {

    // When true, disables sniffing on YouTube to adhere to Google Play Store policy
    var enforceGooglePlayPolicy: Boolean = true

    private val detectedMediaMap = ConcurrentHashMap<String, MutableList<DetectedMedia>>()
    private val _currentMediaList = MutableStateFlow<List<DetectedMedia>>(emptyList())
    val currentMediaList: StateFlow<List<DetectedMedia>> = _currentMediaList.asStateFlow()

    private val videoExtensions = listOf(".mp4", ".webm", ".m3u8", ".mpd", ".mkv", ".mov", ".ts")

    fun inspectUrl(pageUrl: String?, requestedUrl: String): Boolean {
        if (pageUrl == null) return false

        // Check Play Store compliance
        if (enforceGooglePlayPolicy && isRestrictedDomain(pageUrl)) {
            return false
        }

        val lowerUrl = requestedUrl.lowercase()
        val isVideoExt = videoExtensions.any { lowerUrl.contains(it) }
        val isHls = lowerUrl.contains(".m3u8") || lowerUrl.contains("hls")

        if (isVideoExt) {
            val mime = when {
                lowerUrl.contains(".m3u8") -> "application/x-mpegURL"
                lowerUrl.contains(".webm") -> "video/webm"
                else -> "video/mp4"
            }

            val filename = try {
                Uri.parse(requestedUrl).lastPathSegment ?: "Media_${System.currentTimeMillis()}"
            } catch (_: Exception) {
                "Media_${System.currentTimeMillis()}"
            }

            val media = DetectedMedia(
                url = requestedUrl,
                mimeType = mime,
                title = filename,
                isHls = isHls
            )

            addMedia(pageUrl, media)
            return true
        }

        return false
    }

    private fun addMedia(pageUrl: String, media: DetectedMedia) {
        val list = detectedMediaMap.getOrPut(pageUrl) { mutableListOf() }
        synchronized(list) {
            if (list.none { it.url == media.url }) {
                list.add(media)
                _currentMediaList.value = list.toList()
            }
        }
    }

    fun onPageStarted(pageUrl: String) {
        val list = detectedMediaMap[pageUrl] ?: emptyList()
        _currentMediaList.value = list
    }

    private fun isRestrictedDomain(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains("youtube.com") || lower.contains("youtu.be")
    }

    fun getSnifferInjectionScript(): String {
        return """
            (function() {
                function sniffVideos() {
                    var videos = document.querySelectorAll('video, audio');
                    videos.forEach(function(v) {
                        var src = v.src || (v.querySelector('source') ? v.querySelector('source').src : '');
                        if (src && src.startsWith('http')) {
                            // Send URL to browser wrapper via console or custom scheme
                            console.log('[ORION_SNIFFER]:' + src);
                        }
                    });
                }
                setInterval(sniffVideos, 3000);
            })();
        """.trimIndent()
    }
}
