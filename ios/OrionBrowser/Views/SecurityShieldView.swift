import SwiftUI

enum TorSecurityLevel: String, CaseIterable {
    case standard = "Standard"
    case safer = "Safer"
    case safest = "Safest"

    var color: Color {
        switch self {
        case .standard: return .shieldGreen
        case .safer: return .shieldAmber
        case .safest: return .shieldRed
        }
    }
}

struct SecurityShieldView: View {
    @Binding var currentLevel: TorSecurityLevel
    @Binding var isAdBlockEnabled: Bool
    @Binding var isOledBlackEnabled: Bool
    @Binding var isDataSaverEnabled: Bool

    let adsBlockedTotal: Int
    let onNukeIdentity: () -> Void
    let onDismiss: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 18) {
            // Header
            HStack {
                Circle()
                    .fill(currentLevel.color.opacity(0.2))
                    .frame(width: 38, height: 38)
                    .overlay(
                        Image(systemName: "shield.fill")
                            .foregroundColor(currentLevel.color)
                    )

                VStack(alignment: .leading, spacing: 2) {
                    Text("Orion Shields")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.textPrimary)

                    Text("\(adsBlockedTotal) Trackers & Ads Blocked")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.shieldGreen)
                }

                Spacer()

                Button(action: onDismiss) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.textMuted)
                        .font(.system(size: 22))
                }
            }

            // Tor Security Level Picker
            Text("TOR SECURITY LEVEL")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(.textSecondary)

            HStack(spacing: 8) {
                ForEach(TorSecurityLevel.allCases, id: \.self) { level in
                    Button(action: { currentLevel = level }) {
                        Text(level.rawValue)
                            .font(.system(size: 13, weight: currentLevel == level ? .bold : .medium))
                            .foregroundColor(currentLevel == level ? level.color : .textSecondary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(currentLevel == level ? level.color.opacity(0.2) : Color.torSlateLight)
                            .cornerRadius(8)
                    }
                }
            }

            // Feature Toggles
            VStack(spacing: 12) {
                Toggle(isOn: $isAdBlockEnabled) {
                    Label("Orion Shields Adblock", systemImage: "hand.raised.fill")
                        .foregroundColor(.textPrimary)
                }
                .tint(.torPurple)

                Divider().background(Color.dividerDark)

                Toggle(isOn: $isOledBlackEnabled) {
                    Label("OLED True Black Mode", systemImage: "moon.fill")
                        .foregroundColor(.textPrimary)
                }
                .tint(.torPurple)

                Divider().background(Color.dividerDark)

                Toggle(isOn: $isDataSaverEnabled) {
                    Label("Data Saver & Low Network", systemImage: "bolt.fill")
                        .foregroundColor(.textPrimary)
                }
                .tint(.torPurple)
            }
            .padding()
            .background(Color.torSlateLight)
            .cornerRadius(12)

            // Nuke Identity Button
            Button(action: {
                onNukeIdentity()
                onDismiss()
            }) {
                HStack {
                    Image(systemName: "trash.fill")
                    Text("New Identity (Wipe All Tabs & Data)")
                        .fontWeight(.bold)
                }
                .foregroundColor(.shieldRed)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .background(Color.shieldRed.opacity(0.15))
                .cornerRadius(10)
            }

            Spacer()
        }
        .padding(20)
        .background(Color.torSlate)
    }
}
