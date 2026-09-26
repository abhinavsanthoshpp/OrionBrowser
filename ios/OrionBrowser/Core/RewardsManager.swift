import Foundation

struct SponsoredCardItem: Identifiable {
    let id = UUID()
    let title: String
    let description: String
    let sponsorName: String
    let targetUrl: String
    let rewardPoints: Int
}

class RewardsManager: ObservableObject {
    static let shared = RewardsManager()

    @Published var userPoints: Int = 150
    @Published var sponsoredCards: [SponsoredCardItem] = [
        SponsoredCardItem(
            title: "Proton VPN - Encrypt Your Internet",
            description: "High-speed Swiss VPN with zero logs and strict privacy laws.",
            sponsorName: "Proton AG",
            targetUrl: "https://protonvpn.com",
            rewardPoints: 25
        ),
        SponsoredCardItem(
            title: "DuckDuckGo - Privacy, Simplified",
            description: "Search without being tracked by advertisers.",
            sponsorName: "DuckDuckGo",
            targetUrl: "https://duckduckgo.com",
            rewardPoints: 20
        ),
        SponsoredCardItem(
            title: "Bitwarden - Open Source Password Manager",
            description: "Securely generate and store all your passwords across all devices.",
            sponsorName: "Bitwarden Inc.",
            targetUrl: "https://bitwarden.com",
            rewardPoints: 30
        )
    ]

    var estimatedUsdValue: String {
        let usd = Double(userPoints) / 1000.0
        return String(format: "$%.2f", usd)
    }

    func addPoints(_ points: Int) {
        userPoints += points
    }
}
