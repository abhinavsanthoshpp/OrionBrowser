import SwiftUI

struct NewTabPageView: View {
    let adsBlockedTotal: Int

    let onSearchClick: () -> Void
    let onBookmarkClick: (String) -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                Spacer().frame(height: 20)

                // Logo & Title
                HStack(spacing: 10) {
                    Circle()
                        .fill(Color.torPurple)
                        .frame(width: 44, height: 44)
                        .overlay(
                            Image(systemName: "network")
                                .foregroundColor(.white)
                                .font(.system(size: 20))
                        )

                    Text("ORION")
                        .font(.system(size: 24, weight: .black))
                        .tracking(3)
                        .foregroundColor(.textPrimary)
                }

                // Privacy Stats Odometer
                HStack(spacing: 16) {
                    StatBox(value: "\(adsBlockedTotal)", label: "Trackers Blocked", icon: "shield.fill", color: .shieldGreen)
                    Divider().frame(height: 36).background(Color.dividerDark)
                    StatBox(value: "\(Int(Double(adsBlockedTotal) * 0.15)) MB", label: "Data Saved", icon: "bolt.fill", color: .torPurpleBright)
                    Divider().frame(height: 36).background(Color.dividerDark)
                    StatBox(value: "\(Int(Double(adsBlockedTotal) * 0.05))s", label: "Time Saved", icon: "clock.fill", color: Color(hex: 0x3498DB))
                }
                .padding(.vertical, 14)
                .frame(maxWidth: .infinity)
                .background(Color.torSlate)
                .cornerRadius(14)
                .padding(.horizontal)

                // Search Bar Trigger
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.torPurpleBright)

                    Text("Search or type URL…")
                        .foregroundColor(.textMuted)
                        .font(.system(size: 15))

                    Spacer()
                }
                .padding(.horizontal, 16)
                .frame(height: 50)
                .background(Color.torSlate)
                .cornerRadius(25)
                .overlay(RoundedRectangle(cornerRadius: 25).stroke(Color.dividerDark, lineWidth: 1))
                .padding(.horizontal)
                .contentShape(Rectangle())
                .onTapGesture {
                    onSearchClick()
                }

                // Quick Bookmarks
                HStack(spacing: 20) {
                    BookmarkIcon(title: "DuckDuckGo", icon: "magnifyingglass", url: "https://duckduckgo.com", onClick: onBookmarkClick)
                    BookmarkIcon(title: "Tor Project", icon: "shield.lefthalf.filled", url: "https://torproject.org", onClick: onBookmarkClick)
                    BookmarkIcon(title: "Wikipedia", icon: "book.fill", url: "https://wikipedia.org", onClick: onBookmarkClick)
                    BookmarkIcon(title: "GitHub", icon: "curlybraces", url: "https://github.com", onClick: onBookmarkClick)
                }
                .padding(.horizontal)

                Spacer().frame(height: 40)
            }
        }
        .background(Color.torObsidian.ignoresSafeArea())
    }
}

private struct StatBox: View {
    let value: String
    let label: String
    let icon: String
    let color: Color

    var body: some View {
        VStack(spacing: 4) {
            Image(systemName: icon)
                .foregroundColor(color)
                .font(.system(size: 14))

            Text(value)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(.textPrimary)

            Text(label)
                .font(.system(size: 10))
                .foregroundColor(.textMuted)
        }
    }
}

private struct BookmarkIcon: View {
    let title: String
    let icon: String
    let url: String
    let onClick: (String) -> Void

    var body: some View {
        VStack(spacing: 6) {
            Circle()
                .fill(Color.torSlate)
                .frame(width: 48, height: 48)
                .overlay(
                    Image(systemName: icon)
                        .foregroundColor(.textPrimary)
                )

            Text(title)
                .font(.system(size: 11))
                .foregroundColor(.textSecondary)
        }
        .onTapGesture {
            onClick(url)
        }
    }
}
