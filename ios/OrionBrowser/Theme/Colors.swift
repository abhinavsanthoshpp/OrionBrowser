import SwiftUI

extension Color {
    // Tor-inspired Obsidian & Onion Purple Palette
    static let torObsidian = Color(hex: 0x120E16)
    static let torSlate = Color(hex: 0x1E1826)
    static let torSlateLight = Color(hex: 0x2C2337)
    static let torPurple = Color(hex: 0x7D4698)
    static let torPurpleBright = Color(hex: 0x9C5FB8)
    static let torPurpleDark = Color(hex: 0x4C2760)

    // Security Indicators
    static let shieldGreen = Color(hex: 0x27AE60)
    static let shieldAmber = Color(hex: 0xE67E22)
    static let shieldRed = Color(hex: 0xE74C3C)

    // Typography & Dividers
    static let textPrimary = Color(hex: 0xF5F3F7)
    static let textSecondary = Color(hex: 0x9E97A6)
    static let textMuted = Color(hex: 0x6B6574)
    static let dividerDark = Color(hex: 0x2A2234)

    init(hex: UInt, alpha: Double = 1.0) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255.0,
            green: Double((hex >> 08) & 0xff) / 255.0,
            blue: Double((hex >> 00) & 0xff) / 255.0,
            opacity: alpha
        )
    }
}
