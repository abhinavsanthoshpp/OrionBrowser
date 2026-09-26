# 🌌 Orion Browser for Android

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.xml" width="120" height="120" alt="Orion Browser Logo" />
</p>

<p align="center">
  <b>A privacy-hardened, high-performance Android web browser blending the aesthetic of Tor, the ad-blocking power of Brave, and high-speed multi-threaded media downloading.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_8.0+-3DDC84?style=flat&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat" alt="License" />
</p>

---

## 🌟 Key Features

### 1. 🛡️ Tor-Inspired Privacy UI/UX
* **Obsidian & Violet Theme**: Deep dark interface (`#120E16` and `#7D4698`) inspired by the Tor Project.
* **Ergonomic One-Handed Navigation**: Bottom URL bar with quick tab counter, security badge, and instant search.
* **Tor Security Level Slider**:
  * **Standard**: Default ad/tracker blocking; all web features active.
  * **Safer**: Restricts insecure scripts and disables WebGL.
  * **Safest**: Disables JavaScript globally across all pages.
* **One-Tap "New Identity"**: Immediately incinerates all active tabs, cookies, cache, and session storage.

### 2. 🚫 Brave-Grade Native Adblocker
* **Pre-Network Interception**: Intercepts outgoing requests in milliseconds, dropping tracking scripts, DoubleClick, Google Analytics, and ad banners before network dispatch.
* **Cosmetic Filter Injection**: Collapses blank ad spaces so web pages render clean without empty gaps.
* **Live Shields Counter**: Real-time counter of trackers and ads blocked, bandwidth saved, and estimated browsing time saved.

### 3. ⚡ Firefox-Style Fast Search & Low-Network Booster
* **DNS-over-HTTPS (DoH)**: Integrates Cloudflare (`1.1.1.1`) and Quad9 DoH to bypass slow ISP DNS resolvers.
* **Data Saver & Image Suppression**: Automatically sends `Save-Data: on` headers and suppresses heavy images on metered connections.
* **Instant Suggestion Trie**: Offline prefix-search engine providing instant suggestions while typing.

### 4. 🚀 Parrot-Style Fast Video Downloader
* **Media Sniffer**: Automatically detects `.mp4`, `.webm`, and `.m3u8` (HLS) video streams from web pages and HTML5 video tags.
* **Multi-Threaded 4x Acceleration**: Splits single downloads into 4 concurrent byte-range slices for maximum download speed.
* **Audio Extraction**: One-tap option to extract pure audio (MP3) from video streams.
* **Google Play Compliance**: Built-in guard restricting YouTube downloads to comply with Google Play Developer Policies.

### 5. 💰 Non-Intrusive Orion Rewards
* **Policy Compliant**: Avoids invasive ad banners or incentivized traffic violations.
* **New Tab Page Sponsored Cards**: Clean, non-intrusive sponsor cards (Proton VPN, Bitwarden, DuckDuckGo).
* **Rewards Ledger**: Users accumulate internal Orion Points (+25 points daily) for active browsing.

### 6. 🔒 Biometric Secret Vault & Background Media
* **Biometric Authentication**: Fingerprint / PIN prompt required to unlock Secret Tabs.
* **Background & Screen-Off Playback**: Audio and video continue playing when switching apps or locking your phone screen.
* **OLED True-Black Dark Mode**: Universal shader forcing true `#000000` deep blacks for AMOLED power saving.

---

## 🏗️ Architecture & Project Structure

```
OrionBrowser/
├── app/
│   ├── src/main/
│   │   ├── java/org/orion/browser/
│   │   │   ├── adblock/           # AdBlock & Cosmetic Filter engine
│   │   │   ├── downloader/        # Media sniffer & 4-thread chunk downloader
│   │   │   ├── media/             # Background playback foreground service
│   │   │   ├── network/           # DNS-over-HTTPS & DataSaver manager
│   │   │   ├── rewards/           # Orion Points ledger & sponsored cards
│   │   │   ├── ui/
│   │   │   │   ├── components/    # BottomBar, ShieldSheet, NTP, Tabs, Dialogs
│   │   │   │   └── theme/         # Tor Obsidian & Purple Color scheme
│   │   │   ├── vault/             # Biometric authentication manager
│   │   │   ├── MainActivity.kt    # Main state controller
│   │   │   └── OrionApplication.kt
│   │   └── res/                   # Drawables, mipmaps, strings, colors
│   └── build.gradle.kts
├── docs/
│   ├── ARCHITECTURE.md            # Detailed engineering specification
│   └── BROWSER_ANALYSIS.md        # Comprehensive browser ecosystem comparison
└── build.gradle.kts
```

---

## 🛠️ Building & Running

### Prerequisites
* **Android SDK** (API 34)
* **JDK 17 or 21**
* **Gradle 8.7+**

### Build Debug APK
```bash
./gradlew assembleDebug
```

The compiled APK will be located at:
```
app/build/outputs/apk/debug/orion.apk
```

### Install to Connected Device
```bash
adb install app/build/outputs/apk/debug/orion.apk
```

---

## 📜 Documentation
* [Technical Architecture & Blueprint](docs/ARCHITECTURE.md)
* [Comprehensive Browser Ecosystem Analysis](docs/BROWSER_ANALYSIS.md)

---

## 📄 License

Licensed under the [Apache License, Version 2.0](LICENSE).
