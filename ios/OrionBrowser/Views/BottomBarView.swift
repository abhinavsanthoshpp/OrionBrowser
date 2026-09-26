import SwiftUI

struct BottomBarView: View {
    let currentUrl: String
    let canGoBack: Bool
    let canGoForward: Bool
    let tabCount: Int
    let detectedMediaCount: Int
    let adsBlockedCount: Int

    let onBack: () -> Void
    let onForward: () -> Void
    let onSearchClick: () -> Void
    let onShieldClick: () -> Void
    let onTabsClick: () -> Void
    let onMediaClick: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            Divider()
                .background(Color.dividerDark)

            HStack(spacing: 12) {
                // Back Button
                Button(action: onBack) {
                    Image(systemName: "chevron.backward")
                        .foregroundColor(canGoBack ? .textPrimary : .textMuted)
                        .font(.system(size: 16, weight: .bold))
                }
                .disabled(!canGoBack)
                .frame(width: 32, height: 32)

                // Forward Button
                Button(action: onForward) {
                    Image(systemName: "chevron.forward")
                        .foregroundColor(canGoForward ? .textPrimary : .textMuted)
                        .font(.system(size: 16, weight: .bold))
                }
                .disabled(!canGoForward)
                .frame(width: 32, height: 32)

                // Center URL / Search Pill
                HStack {
                    Button(action: onShieldClick) {
                        HStack(spacing: 4) {
                            Image(systemName: "shield.fill")
                                .foregroundColor(.shieldGreen)
                                .font(.system(size: 14))

                            if adsBlockedCount > 0 {
                                Text("\(adsBlockedCount)")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 4)
                                    .padding(.vertical, 1)
                                    .background(Color.shieldGreen)
                                    .clipShape(Capsule())
                            }
                        }
                    }

                    Text(currentUrl.isEmpty || currentUrl == "about:blank" ? "Search or enter address" : currentUrl)
                        .font(.system(size: 13))
                        .foregroundColor(currentUrl.isEmpty || currentUrl == "about:blank" ? .textMuted : .textPrimary)
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .contentShape(Rectangle())
                        .onTapGesture {
                            onSearchClick()
                        }

                    if detectedMediaCount > 0 {
                        Button(action: onMediaClick) {
                            Image(systemName: "arrow.down.circle.fill")
                                .foregroundColor(.shieldGreen)
                                .font(.system(size: 18))
                        }
                    }
                }
                .padding(.horizontal, 10)
                .frame(height: 38)
                .background(Color.torSlate)
                .cornerRadius(19)

                // Tab Switcher Button
                Button(action: onTabsClick) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 6)
                            .stroke(Color.textPrimary, lineWidth: 1.5)
                            .frame(width: 24, height: 24)

                        Text("\(tabCount)")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.textPrimary)
                    }
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
            .background(Color.torObsidian)
        }
    }
}
