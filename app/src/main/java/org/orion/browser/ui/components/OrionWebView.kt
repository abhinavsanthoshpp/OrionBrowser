package org.orion.browser.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.orion.browser.adblock.AdBlockEngine
import org.orion.browser.adblock.CosmeticFilterManager
import org.orion.browser.downloader.MediaSniffer
import org.orion.browser.network.DataSaverManager
import org.orion.browser.ui.theme.TorObsidian
import java.io.ByteArrayInputStream

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OrionWebView(
    tab: BrowserTab,
    securityLevel: SecurityLevel,
    isAdBlockEnabled: Boolean,
    isOledBlackEnabled: Boolean,
    isDataSaverEnabled: Boolean,
    onUrlChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onCanGoBackChanged: (Boolean) -> Unit,
    onCanGoForwardChanged: (Boolean) -> Unit,
    onWebViewCreated: (WebView) -> Unit = {},
    onDownloadRequested: (url: String, filename: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                onWebViewCreated(this)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(android.graphics.Color.parseColor("#120E16"))

                settings.apply {
                    // Tor security levels control JavaScript
                    javaScriptEnabled = securityLevel != SecurityLevel.SAFEST
                    domStorageEnabled = securityLevel != SecurityLevel.SAFEST
                    databaseEnabled = !tab.isSecret

                    // Privacy & Security settings
                    allowFileAccess = false
                    allowContentAccess = false
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false

                    // Low-network optimization
                    cacheMode = if (isDataSaverEnabled) WebSettings.LOAD_DEFAULT else WebSettings.LOAD_CACHE_ELSE_NETWORK
                    mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                }

                setDownloadListener { downloadUrl, _, contentDisposition, mimetype, _ ->
                    val filename = try {
                        android.webkit.URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype)
                    } catch (_: Exception) {
                        "Download_${System.currentTimeMillis()}"
                    }
                    onDownloadRequested(downloadUrl, filename)
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val url = request?.url?.toString() ?: return null

                        // 1. Inspect for downloadable media streams
                        MediaSniffer.inspectUrl(view?.url, url)

                        // 2. Brave-grade Ad & Tracker Blocker
                        if (isAdBlockEnabled && AdBlockEngine.isAdOrTracker(url, view?.url)) {
                            // Drop request by returning an empty 204 No Content response
                            return WebResourceResponse(
                                "text/plain",
                                "UTF-8",
                                204,
                                "No Content",
                                emptyMap(),
                                ByteArrayInputStream(ByteArray(0))
                            )
                        }

                        // 3. Data-saver heavy image suppression on metered connections
                        if (DataSaverManager.shouldSuppressImage(context, url)) {
                            return WebResourceResponse(
                                "image/png",
                                "UTF-8",
                                204,
                                "No Content",
                                emptyMap(),
                                ByteArrayInputStream(ByteArray(0))
                            )
                        }

                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        url?.let {
                            onUrlChanged(it)
                            MediaSniffer.onPageStarted(it)
                        }
                        onCanGoBackChanged(view?.canGoBack() ?: false)
                        onCanGoForwardChanged(view?.canGoForward() ?: false)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        url?.let { onUrlChanged(it) }
                        onCanGoBackChanged(view?.canGoBack() ?: false)
                        onCanGoForwardChanged(view?.canGoForward() ?: false)

                        // Inject cosmetic filter CSS & OLED true black styling
                        view?.evaluateJavascript(
                            CosmeticFilterManager.getCosmeticInjectionJs(isOledBlackEnabled),
                            null
                        )

                        // Inject DOM media sniffer
                        view?.evaluateJavascript(
                            MediaSniffer.getSnifferInjectionScript(),
                            null
                        )
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        onProgressChanged(newProgress)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        title?.let { onTitleChanged(it) }
                    }
                }

                if (tab.url.isNotEmpty() && tab.url != "about:blank") {
                    loadUrl(tab.url, DataSaverManager.getExtraHeaders())
                }
            }
        },
        update = { webView ->
            webView.settings.javaScriptEnabled = securityLevel != SecurityLevel.SAFEST

            if (tab.url.isNotEmpty() && tab.url != "about:blank" && webView.url != tab.url) {
                webView.loadUrl(tab.url, DataSaverManager.getExtraHeaders())
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(TorObsidian)
    )
}
