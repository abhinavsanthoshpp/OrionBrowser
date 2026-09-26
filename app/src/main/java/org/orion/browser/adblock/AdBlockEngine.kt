package org.orion.browser.adblock

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object AdBlockEngine {

    private val blockedHostSet = ConcurrentHashMap.newKeySet<String>()
    private val blockedKeywordList = listOf(
        "/ads/", "/adserver/", "/doubleclick/", "/pagead/",
        "/advert/", "/telemetry/", "/analytics.js", "/gtag/js",
        "/pixel.gif", "/beacon.js", "googlesyndication.com",
        "google-analytics.com", "adservice.google", "adnxs.com",
        "criteo.com", "taboola.com", "outbrain.com", "scorecardresearch.com"
    )

    private val _adsBlockedTotal = MutableStateFlow(0)
    val adsBlockedTotal: StateFlow<Int> = _adsBlockedTotal.asStateFlow()

    private val _trackersBlockedTotal = MutableStateFlow(0)
    val trackersBlockedTotal: StateFlow<Int> = _trackersBlockedTotal.asStateFlow()

    private val currentSessionBlocked = AtomicInteger(0)

    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            loadDefaultFilterLists(context)
        }
    }

    private fun loadDefaultFilterLists(context: Context) {
        val defaultAdDomains = setOf(
            "doubleclick.net", "googleadservices.com", "googlesyndication.com",
            "adservice.google.com", "pagead2.googlesyndication.com", "adroll.com",
            "adnxs.com", "criteo.com", "criteo.net", "taboola.com", "outbrain.com",
            "scorecardresearch.com", "advertising.com", "rubiconproject.com",
            "pubmatic.com", "openx.net", "casalemedia.com", "moatads.com",
            "quantserve.com", "revcontent.com", "adcolony.com", "unityads.unity3d.com",
            "applovin.com", "chartboost.com", "vungle.com", "ironsrc.com",
            "facebook.com/tr", "analytics.twitter.com", "ads-twitter.com",
            "analytics.tiktok.com", "bat.bing.com", "hotjar.com", "clarity.ms"
        )
        blockedHostSet.addAll(defaultAdDomains)
    }

    fun isAdOrTracker(url: String, pageHost: String? = null): Boolean {
        try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase() ?: return false

            // Check direct host match and domain parts (e.g. ad.example.com -> example.com)
            var currentHost: String? = host
            while (currentHost != null && currentHost.contains(".")) {
                if (blockedHostSet.contains(currentHost)) {
                    incrementBlockedStats(isTracker = currentHost.contains("analytics") || currentHost.contains("track"))
                    return true
                }
                val dotIndex = currentHost.indexOf('.')
                currentHost = if (dotIndex >= 0 && dotIndex < currentHost.length - 1) {
                    currentHost.substring(dotIndex + 1)
                } else null
            }

            // Check URL path against common ad patterns
            val path = uri.path?.lowercase() ?: ""
            for (keyword in blockedKeywordList) {
                if (path.contains(keyword) || url.contains(keyword)) {
                    incrementBlockedStats(isTracker = keyword.contains("analytics") || keyword.contains("telemetry"))
                    return true
                }
            }
        } catch (_: Exception) {
            // Ignore malformed URLs
        }
        return false
    }

    private fun incrementBlockedStats(isTracker: Boolean) {
        currentSessionBlocked.incrementAndGet()
        if (isTracker) {
            _trackersBlockedTotal.value += 1
        } else {
            _adsBlockedTotal.value += 1
        }
    }

    fun getSessionBlockedCount(): Int = currentSessionBlocked.get()

    fun resetSessionBlockedCount() {
        currentSessionBlocked.set(0)
    }
}
