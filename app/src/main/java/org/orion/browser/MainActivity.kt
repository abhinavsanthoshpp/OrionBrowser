package org.orion.browser

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import org.orion.browser.adblock.AdBlockEngine
import org.orion.browser.downloader.MediaSniffer
import org.orion.browser.downloader.SegmentDownloader
import org.orion.browser.network.DataSaverManager
import org.orion.browser.ui.components.BottomNavigationBar
import org.orion.browser.ui.components.BrowserTab
import org.orion.browser.ui.components.MediaDownloadDialog
import org.orion.browser.ui.components.NewTabPageView
import org.orion.browser.ui.components.OrionWebView
import org.orion.browser.ui.components.SearchDialog
import org.orion.browser.ui.components.SecurityLevel
import org.orion.browser.ui.components.SecurityShieldSheet
import org.orion.browser.ui.components.TabSwitcherView
import org.orion.browser.ui.theme.OrionTheme
import org.orion.browser.ui.theme.TorObsidian
import org.orion.browser.ui.theme.TorPurple
import org.orion.browser.vault.BiometricVaultManager

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OrionTheme {
                BrowserApp(activity = this)
            }
        }
    }
}

@Composable
fun BrowserApp(activity: FragmentActivity) {
    val coroutineScope = rememberCoroutineScope()

    // Tabs state
    val tabs = remember {
        mutableStateListOf(
            BrowserTab(
                id = "tab_initial",
                title = "New Tab",
                url = "about:blank",
                isSecret = false
            )
        )
    }
    var activeTabIndex by remember { mutableIntStateOf(0) }
    var isSecretModeActive by remember { mutableStateOf(false) }

    // Security & Feature states
    var securityLevel by remember { mutableStateOf(SecurityLevel.STANDARD) }
    var isAdBlockEnabled by remember { mutableStateOf(true) }
    var isOledBlackEnabled by remember { mutableStateOf(false) }
    var isDataSaverEnabled by remember { mutableStateOf(false) }

    // Overlays & Sheets
    var isShieldSheetOpen by remember { mutableStateOf(false) }
    var isSearchDialogOpen by remember { mutableStateOf(false) }
    var isTabSwitcherOpen by remember { mutableStateOf(false) }
    var isMediaDownloadDialogOpen by remember { mutableStateOf(false) }

    // Web navigation states
    var pageProgress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var activeWebView by remember { mutableStateOf<android.webkit.WebView?>(null) }

    // Stats flows
    val adsBlockedTotal by AdBlockEngine.adsBlockedTotal.collectAsState()
    val trackersBlockedTotal by AdBlockEngine.trackersBlockedTotal.collectAsState()
    val detectedMediaList by MediaSniffer.currentMediaList.collectAsState()

    val currentTab = tabs.getOrNull(activeTabIndex) ?: tabs.first()

    // Handle Android system back gesture
    BackHandler(enabled = isSearchDialogOpen || isTabSwitcherOpen || isShieldSheetOpen || canGoBack || (currentTab.url.isNotEmpty() && currentTab.url != "about:blank")) {
        when {
            isSearchDialogOpen -> isSearchDialogOpen = false
            isTabSwitcherOpen -> isTabSwitcherOpen = false
            isShieldSheetOpen -> isShieldSheetOpen = false
            canGoBack && activeWebView != null -> activeWebView?.goBack()
            currentTab.url.isNotEmpty() && currentTab.url != "about:blank" -> {
                tabs[activeTabIndex] = currentTab.copy(url = "about:blank", title = "New Tab")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TorObsidian)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Page Loading Progress Bar
            AnimatedVisibility(visible = pageProgress in 1..99) {
                LinearProgressIndicator(
                    progress = { pageProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = TorPurple,
                    trackColor = TorObsidian
                )
            }

            // Main Content Area: New Tab Page or Active WebView
            Box(modifier = Modifier.weight(1f)) {
                if (currentTab.url.isEmpty() || currentTab.url == "about:blank") {
                    NewTabPageView(
                        adsBlockedTotal = adsBlockedTotal,
                        trackersBlockedTotal = trackersBlockedTotal,
                        onSearchClick = { isSearchDialogOpen = true },
                        onBookmarkClick = { targetUrl ->
                            tabs[activeTabIndex] = currentTab.copy(url = targetUrl)
                        }
                    )
                } else {
                    OrionWebView(
                        tab = currentTab,
                        securityLevel = securityLevel,
                        isAdBlockEnabled = isAdBlockEnabled,
                        isOledBlackEnabled = isOledBlackEnabled,
                        isDataSaverEnabled = isDataSaverEnabled,
                        onUrlChanged = { newUrl ->
                            if (newUrl != currentTab.url) {
                                tabs[activeTabIndex] = currentTab.copy(url = newUrl)
                            }
                        },
                        onTitleChanged = { newTitle ->
                            tabs[activeTabIndex] = currentTab.copy(title = newTitle)
                        },
                        onProgressChanged = { progress -> pageProgress = progress },
                        onCanGoBackChanged = { canGoBack = it },
                        onCanGoForwardChanged = { canGoForward = it },
                        onWebViewCreated = { webView -> activeWebView = webView },
                        onDownloadRequested = { url, filename ->
                            coroutineScope.launch {
                                Toast.makeText(activity, "Downloading $filename…", Toast.LENGTH_SHORT).show()
                                val result = SegmentDownloader.downloadMedia(activity, url, filename)
                                if (result.isSuccess) {
                                    Toast.makeText(activity, "Saved to Downloads: $filename", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(activity, "Download failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }

            // Bottom Navigation Bar
            BottomNavigationBar(
                currentUrl = if (currentTab.url == "about:blank") null else currentTab.url,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                tabCount = tabs.size,
                adsBlockedCount = AdBlockEngine.getSessionBlockedCount(),
                detectedMediaCount = detectedMediaList.size,
                onBack = {
                    if (canGoBack && activeWebView != null) {
                        activeWebView?.goBack()
                    } else if (currentTab.url.isNotEmpty() && currentTab.url != "about:blank") {
                        tabs[activeTabIndex] = currentTab.copy(url = "about:blank", title = "New Tab")
                    }
                },
                onForward = {
                    if (canGoForward && activeWebView != null) {
                        activeWebView?.goForward()
                    }
                },
                onUrlClick = { isSearchDialogOpen = true },
                onShieldClick = { isShieldSheetOpen = true },
                onTabsClick = { isTabSwitcherOpen = true },
                onMediaDownloadClick = { isMediaDownloadDialogOpen = true },
                onMenuClick = { isShieldSheetOpen = true }
            )
        }

        // Tab Switcher Overlay
        AnimatedVisibility(
            visible = isTabSwitcherOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TabSwitcherView(
                tabs = tabs,
                activeTabIndex = activeTabIndex,
                isSecretModeActive = isSecretModeActive,
                onSelectTab = { index ->
                    activeTabIndex = index
                    isTabSwitcherOpen = false
                },
                onCloseTab = { index ->
                    if (tabs.size > 1) {
                        tabs.removeAt(index)
                        if (activeTabIndex >= tabs.size) {
                            activeTabIndex = tabs.size - 1
                        }
                    } else {
                        tabs[0] = BrowserTab("tab_${System.currentTimeMillis()}", "New Tab", "about:blank")
                    }
                },
                onNewTab = { isSecret ->
                    val newTab = BrowserTab(
                        id = "tab_${System.currentTimeMillis()}",
                        title = "New Tab",
                        url = "about:blank",
                        isSecret = isSecret
                    )
                    tabs.add(newTab)
                    activeTabIndex = tabs.size - 1
                    isTabSwitcherOpen = false
                },
                onToggleSecretMode = {
                    if (!isSecretModeActive) {
                        // Prompt Biometrics to unlock Secret Vault
                        BiometricVaultManager.authenticate(
                            activity = activity,
                            onSuccess = { isSecretModeActive = true },
                            onError = { _ ->
                                Toast.makeText(activity, "Biometric required for Secret Mode", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        isSecretModeActive = false
                    }
                },
                onNukeIdentity = {
                    tabs.clear()
                    tabs.add(BrowserTab("tab_${System.currentTimeMillis()}", "New Tab", "about:blank"))
                    activeTabIndex = 0
                    AdBlockEngine.resetSessionBlockedCount()
                    isTabSwitcherOpen = false
                    Toast.makeText(activity, "New Identity Applied. All session data wiped.", Toast.LENGTH_SHORT).show()
                },
                onCloseSwitcher = { isTabSwitcherOpen = false }
            )
        }

        // Search / URL Overlay
        SearchDialog(
            isOpen = isSearchDialogOpen,
            initialQuery = if (currentTab.url == "about:blank") "" else currentTab.url,
            onSearch = { target ->
                val formattedUrl = if (target.startsWith("http://") || target.startsWith("https://")) {
                    target
                } else if (target.contains(".") && !target.contains(" ")) {
                    "https://$target"
                } else {
                    "https://duckduckgo.com/?q=${target.replace(" ", "+")}"
                }
                tabs[activeTabIndex] = currentTab.copy(url = formattedUrl, title = target)
                isSearchDialogOpen = false
            },
            onDismiss = { isSearchDialogOpen = false }
        )

        // Security Shield Bottom Sheet
        SecurityShieldSheet(
            isOpen = isShieldSheetOpen,
            currentLevel = securityLevel,
            adsBlockedCount = AdBlockEngine.getSessionBlockedCount(),
            isAdBlockEnabled = isAdBlockEnabled,
            isOledBlackEnabled = isOledBlackEnabled,
            isDataSaverEnabled = isDataSaverEnabled,
            onSecurityLevelChange = { newLevel -> securityLevel = newLevel },
            onToggleAdBlock = { isAdBlockEnabled = it },
            onToggleOledBlack = { isOledBlackEnabled = it },
            onToggleDataSaver = {
                isDataSaverEnabled = it
                DataSaverManager.toggleDataSaver(it)
                DataSaverManager.toggleBlockImages(it)
            },
            onNukeIdentity = {
                tabs.clear()
                tabs.add(BrowserTab("tab_${System.currentTimeMillis()}", "New Tab", "about:blank"))
                activeTabIndex = 0
                AdBlockEngine.resetSessionBlockedCount()
                Toast.makeText(activity, "New Identity Applied. All tabs wiped.", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { isShieldSheetOpen = false }
        )

        // Media Download Dialog (Parrot-style Fast Downloader)
        if (isMediaDownloadDialogOpen) {
            MediaDownloadDialog(
                mediaList = detectedMediaList,
                onDownloadVideo = { media ->
                    coroutineScope.launch {
                        Toast.makeText(activity, "Starting 4-Thread Fast Download: ${media.title}", Toast.LENGTH_SHORT).show()
                        val result = SegmentDownloader.downloadMedia(activity, media.url, media.title)
                        if (result.isSuccess) {
                            Toast.makeText(activity, "Download Complete: ${result.getOrNull()?.name}", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Download error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    isMediaDownloadDialogOpen = false
                },
                onDownloadAudio = { media ->
                    coroutineScope.launch {
                        Toast.makeText(activity, "Extracting audio track…", Toast.LENGTH_SHORT).show()
                        val result = SegmentDownloader.downloadMedia(activity, media.url, "${media.title}.mp3")
                        if (result.isSuccess) {
                            Toast.makeText(activity, "Audio saved to Downloads: ${result.getOrNull()?.name}", Toast.LENGTH_LONG).show()
                        }
                    }
                    isMediaDownloadDialogOpen = false
                },
                onDismiss = { isMediaDownloadDialogOpen = false }
            )
        }
    }
}
