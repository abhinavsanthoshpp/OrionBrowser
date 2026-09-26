import Foundation
import WebKit

struct DetectedMediaItem: Identifiable, Equatable {
    let id = UUID()
    let url: String
    let mimeType: String
    let title: String
    let isHls: Bool
}

class MediaSniffer: NSObject, ObservableObject, WKScriptMessageHandler {
    static let shared = MediaSniffer()

    @Published var detectedMedia: [DetectedMediaItem] = []

    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        guard message.name == "orionMediaSniffer",
              let mediaUrl = message.body as? String else {
            return
        }

        inspectMediaUrl(mediaUrl)
    }

    func inspectMediaUrl(_ urlString: String) {
        let lower = urlString.lowercased()
        if lower.contains(".mp4") || lower.contains(".webm") || lower.contains(".m3u8") {
            let isHls = lower.contains(".m3u8")
            let filename = URL(string: urlString)?.lastPathComponent ?? "Media_\(Date().timeIntervalSince1970)"

            let item = DetectedMediaItem(
                url: urlString,
                mimeType: isHls ? "application/x-mpegURL" : "video/mp4",
                title: filename,
                isHls: isHls
            )

            DispatchQueue.main.async {
                if !self.detectedMedia.contains(where: { $0.url == item.url }) {
                    self.detectedMedia.append(item)
                }
            }
        }
    }

    func clearMedia() {
        DispatchQueue.main.async {
            self.detectedMedia.removeAll()
        }
    }

    var injectionScript: WKUserScript {
        let source = """
        (function() {
            function sniff() {
                var media = document.querySelectorAll('video, audio');
                media.forEach(function(m) {
                    var src = m.src || (m.querySelector('source') ? m.querySelector('source').src : '');
                    if (src && src.startsWith('http')) {
                        window.webkit.messageHandlers.orionMediaSniffer.postMessage(src);
                    }
                });
            }
            setInterval(sniff, 3000);
        })();
        """
        return WKUserScript(source: source, injectionTime: .atDocumentEnd, forMainFrameOnly: false)
    }
}
