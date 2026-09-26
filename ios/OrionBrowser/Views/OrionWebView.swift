import SwiftUI
import WebKit

struct OrionWebView: UIViewRepresentable {
    let urlString: String
    @Binding var canGoBack: Bool
    @Binding var canGoForward: Bool
    @Binding var currentUrl: String
    @Binding var estimatedProgress: Double

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIView(context: Context) -> WKWebView {
        let configuration = WKWebViewConfiguration()

        // Apply native WebKit content blocker rules (AdBlock)
        AdBlockEngine.shared.applyContentRules(to: configuration)

        // Inject Media Sniffer script and message handler
        configuration.userContentController.addUserScript(MediaSniffer.shared.injectionScript)
        configuration.userContentController.add(MediaSniffer.shared, name: "orionMediaSniffer")

        // Enable inline media playback & background audio
        configuration.allowsInlineMediaPlayback = true
        configuration.mediaTypesRequiringUserActionForPlayback = []

        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.navigationDelegate = context.coordinator
        webView.backgroundColor = UIColor(Color.torObsidian)
        webView.isOpaque = false

        context.coordinator.setupProgressObserver(for: webView)

        if let url = URL(string: urlString), urlString != "about:blank" {
            webView.load(URLRequest(url: url))
        }

        return webView
    }

    func updateUIView(_ webView: WKWebView, context: Context) {
        if let targetUrl = URL(string: urlString),
           urlString != "about:blank",
           webView.url?.absoluteString != urlString {
            webView.load(URLRequest(url: targetUrl))
        }
    }

    class Coordinator: NSObject, WKNavigationDelegate {
        var parent: OrionWebView
        private var observation: NSKeyValueObservation?

        init(_ parent: OrionWebView) {
            self.parent = parent
        }

        func setupProgressObserver(for webView: WKWebView) {
            observation = webView.observe(\.estimatedProgress, options: [.new]) { [weak self] webView, _ in
                DispatchQueue.main.async {
                    self?.parent.estimatedProgress = webView.estimatedProgress
                }
            }
        }

        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            DispatchQueue.main.async {
                self.parent.canGoBack = webView.canGoBack
                self.parent.canGoForward = webView.canGoForward
                if let url = webView.url?.absoluteString {
                    self.parent.currentUrl = url
                }
            }
        }
    }
}
