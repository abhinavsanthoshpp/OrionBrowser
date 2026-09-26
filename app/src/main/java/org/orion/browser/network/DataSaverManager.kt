package org.orion.browser.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DataSaverManager {

    private val _isDataSaverEnabled = MutableStateFlow(false)
    val isDataSaverEnabled: StateFlow<Boolean> = _isDataSaverEnabled.asStateFlow()

    private val _blockImagesOnMetered = MutableStateFlow(false)
    val blockImagesOnMetered: StateFlow<Boolean> = _blockImagesOnMetered.asStateFlow()

    fun toggleDataSaver(enabled: Boolean) {
        _isDataSaverEnabled.value = enabled
    }

    fun toggleBlockImages(enabled: Boolean) {
        _blockImagesOnMetered.value = enabled
    }

    fun shouldSuppressImage(context: Context, resourceUrl: String): Boolean {
        if (!_blockImagesOnMetered.value) return false
        val isMetered = isMeteredNetwork(context)
        if (!isMetered) return false

        val lower = resourceUrl.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".webp") ||
                lower.endsWith(".gif") || lower.endsWith(".avif")
    }

    fun isMeteredNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    }

    fun getExtraHeaders(): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        if (_isDataSaverEnabled.value) {
            headers["Save-Data"] = "on"
            headers["Accept-Encoding"] = "gzip, deflate, br"
        }
        return headers
    }
}
