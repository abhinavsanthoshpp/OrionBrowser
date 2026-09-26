import Foundation
import WebKit

class AdBlockEngine: ObservableObject {
    static let shared = AdBlockEngine()

    @Published var adsBlockedTotal: Int = 0
    @Published var trackersBlockedTotal: Int = 0
    @Published var isAdBlockEnabled: Boolean = true

    private var compiledRuleList: WKContentRuleList?

    init() {
        compileDefaultRules()
    }

    func compileDefaultRules() {
        guard let rulesURL = Bundle.main.url(forResource: "content-blocker-rules", withExtension: "json"),
              let rulesString = try? String(contentsOf: rulesURL) else {
            return
        }

        WKContentRuleListStore.default().compileContentRuleList(
            forIdentifier: "OrionBlockerRules",
            encodedContentRuleList: rulesString
        ) { [weak self] ruleList, error in
            if let ruleList = ruleList {
                DispatchQueue.main.async {
                    self?.compiledRuleList = ruleList
                }
            }
        }
    }

    func applyContentRules(to configuration: WKWebViewConfiguration) {
        if let ruleList = compiledRuleList, isAdBlockEnabled {
            configuration.userContentController.add(ruleList)
        }
    }

    func incrementBlockedCount() {
        DispatchQueue.main.async {
            self.adsBlockedTotal += 1
        }
    }
}
