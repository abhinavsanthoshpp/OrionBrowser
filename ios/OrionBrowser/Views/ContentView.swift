import SwiftUI

struct BrowserTabItem: Identifiable, Equatable {
    let id = UUID()
    var title: String
    var url: String
    var isSecret: Bool = false
}

struct ContentView: View {
    @StateObject private var adBlockEngine = AdBlockEngine.shared
    @StateObject private var mediaSniffer = MediaSniffer.shared

    @State private var tabs: [BrowserTabItem] = [
        BrowserTabItem(title: "New Tab", url: "about:blank")
    ]
    @State private var activeTabIndex: Int = 0

    @State private var canGoBack: Bool = false
    @State private var canGoForward: Bool = false
    @State private var estimatedProgress: Double = 0.0

    @State private var securityLevel: TorSecurityLevel = .standard
    @State private var isAdBlockEnabled: Bool = true
    @State private var isOledBlackEnabled: Bool = false
    @State private var isDataSaverEnabled: Bool = false

    @State private var isShieldSheetOpen: Bool = false
    @State private var isSearchOverlayOpen: Bool = false
    @State private var searchInput: String = ""

    var currentTab: BrowserTabItem {
        guard activeTabIndex < tabs.count else { return tabs[0] }
        return tabs[activeTabIndex]
    }

    var body: some View {
        ZStack {
            Color.torObsidian.ignoresSafeArea()

            VStack(spacing: 0) {
                // Progress Bar
                if estimatedProgress > 0 && estimatedProgress < 1.0 {
                    ProgressView(value: estimatedProgress)
                        .tint(Color.torPurple)
                        .frame(height: 2)
                }

                // Main Content View
                if currentTab.url == "about:blank" || currentTab.url.isEmpty {
                    NewTabPageView(
                        adsBlockedTotal: adBlockEngine.adsBlockedTotal,
                        onSearchClick: { isSearchOverlayOpen = true },
                        onBookmarkClick: { url in
                            tabs[activeTabIndex].url = url
                        }
                    )
                } else {
                    OrionWebView(
                        urlString: currentTab.url,
                        canGoBack: $canGoBack,
                        canGoForward: $canGoForward,
                        currentUrl: Binding(
                            get: { self.currentTab.url },
                            set: { self.tabs[activeTabIndex].url = $0 }
                        ),
                        estimatedProgress: $estimatedProgress
                    )
                }

                // Bottom Navigation Bar
                BottomBarView(
                    currentUrl: currentTab.url,
                    canGoBack: canGoBack,
                    canGoForward: canGoForward,
                    tabCount: tabs.count,
                    detectedMediaCount: mediaSniffer.detectedMedia.count,
                    adsBlockedCount: adBlockEngine.adsBlockedTotal,
                    onBack: {},
                    onForward: {},
                    onSearchClick: { isSearchOverlayOpen = true },
                    onShieldClick: { isShieldSheetOpen = true },
                    onTabsClick: {},
                    onMediaClick: {}
                )
            }

            // Search Overlay
            if isSearchOverlayOpen {
                ZStack {
                    Color.torObsidian.ignoresSafeArea()
                    VStack {
                        HStack {
                            TextField("Search or enter web address…", text: $searchInput)
                                .padding(12)
                                .background(Color.torSlate)
                                .cornerRadius(12)
                                .foregroundColor(.textPrimary)
                                .onSubmit {
                                    navigateToUrl(searchInput)
                                    isSearchOverlayOpen = false
                                }

                            Button("Cancel") {
                                isSearchOverlayOpen = false
                            }
                            .foregroundColor(.torPurpleBright)
                        }
                        .padding()

                        Spacer()
                    }
                }
            }
        }
        .sheet(isPresented: $isShieldSheetOpen) {
            SecurityShieldView(
                currentLevel: $securityLevel,
                isAdBlockEnabled: $isAdBlockEnabled,
                isOledBlackEnabled: $isOledBlackEnabled,
                isDataSaverEnabled: $isDataSaverEnabled,
                adsBlockedTotal: adBlockEngine.adsBlockedTotal,
                onNukeIdentity: {
                    tabs = [BrowserTabItem(title: "New Tab", url: "about:blank")]
                    activeTabIndex = 0
                },
                onDismiss: { isShieldSheetOpen = false }
            )
        }
    }

    private func navigateToUrl(_ input: String) {
        let trimmed = input.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.starts(with: "http://") || trimmed.starts(with: "https://") {
            tabs[activeTabIndex].url = trimmed
        } else if trimmed.contains(".") && !trimmed.contains(" ") {
            tabs[activeTabIndex].url = "https://\(trimmed)"
        } else {
            let encoded = trimmed.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? trimmed
            tabs[activeTabIndex].url = "https://duckduckgo.com/?q=\(encoded)"
        }
    }
}
