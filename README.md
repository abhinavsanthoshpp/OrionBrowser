# 🌌 Orion Browser (Android & iOS)

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.xml" width="120" height="120" alt="Orion Browser Logo" />
</p>

<p align="center">
  <b>A privacy-hardened, high-performance cross-platform web browser (Android & iOS) with sub-millisecond native ad-blocking, multi-stage Tor security controls, and high-speed media downloading.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_8.0+_%7C_iOS_16.0+-3DDC84?style=flat&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin_%7C_Swift-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Languages" />
  <img src="https://img.shields.io/badge/UI-Compose_%7C_SwiftUI-4285F4?style=flat&logo=swift&logoColor=white" alt="UI" />
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

### 2. 🚫 Orion Shields: Sub-Millisecond Native Adblocker
* **Pre-Network Interception**: Intercepts outgoing requests in milliseconds, dropping tracking scripts, DoubleClick, Google Analytics, and ad banners before network dispatch.
* **Cosmetic Filter Injection**: Collapses blank ad spaces so web pages render clean without empty gaps.
* **Live Shields Counter**: Real-time counter of trackers and ads blocked, bandwidth saved, and estimated browsing time saved.

### 3. ⚡ Turbo DNS & Low-Network Data Saver
* **DNS-over-HTTPS (DoH)**: Integrates Cloudflare (`1.1.1.1`) and Quad9 DoH to bypass slow ISP DNS resolvers.
* **Data Saver & Image Suppression**: Automatically sends `Save-Data: on` headers and suppresses heavy images on metered connections.
* **Instant Suggestion Trie**: Offline prefix-search engine providing instant suggestions while typing.

### 4. 🚀 Turbo Multi-Threaded Fast Video Downloader
* **Media Sniffer**: Automatically detects `.mp4`, `.webm`, and `.m3u8` (HLS) video streams from web pages and HTML5 video tags.
* **Multi-Threaded 4x Acceleration**: Splits single downloads into 4 concurrent byte-range slices for maximum download speed.
* **Audio Extraction**: One-tap option to extract pure audio (MP3) from video streams.
* **Google Play Compliance**: Built-in guard restricting YouTube downloads to comply with Google Play Developer Policies.

### 5. 🔒 Biometric Secret Vault & Background Media
* **Biometric Authentication**: Fingerprint / PIN prompt required to unlock Secret Tabs.
* **Background & Screen-Off Playback**: Audio and video continue playing when switching apps or locking your phone screen.
* **OLED True-Black Dark Mode**: Universal shader forcing true `#000000` deep blacks for AMOLED power saving.

---

## 🌐 Official Download Website

The responsive landing page is located at [`index.html`](index.html) (and in `docs/` for GitHub Pages).
Users can download `orion.apk` directly with one click.

---

## 🏗️ Architecture & Project Structure

```
OrionBrowser/
├── app/                           # Android Application (Kotlin + Jetpack Compose)
│   ├── src/main/java/org/orion/browser/
│   │   ├── adblock/               # AdBlock & Cosmetic Filter engine
│   │   ├── downloader/            # Media sniffer & 4-thread chunk downloader
│   │   ├── media/                 # Background playback foreground service
│   │   ├── network/               # DNS-over-HTTPS & DataSaver manager
│   │   ├── ui/                    # Jetpack Compose UI (Tor theme)
│   │   └── vault/                 # Biometric authentication manager
│   └── build.gradle.kts
├── ios/                           # iOS Application (Swift + SwiftUI + WebKit)
│   └── OrionBrowser/
│       ├── Core/                  # WebKit ContentBlocker & MediaSniffer
│       ├── Theme/                 # SwiftUI Tor Obsidian & Violet theme
│       ├── Views/                 # OrionWebView, BottomBarView, SecurityShieldView
│       └── Resources/             # content-blocker-rules.json, Info.plist
├── docs/                          # Architecture blueprints & GitHub Pages site
├── index.html                     # Official download website
└── build.gradle.kts
```

---

## 🛠️ Building & Running

### Android Build
* **Prerequisites**: Android SDK (API 34), JDK 17 or 21, Gradle 8.7+
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/orion.apk`

### iOS Build
* **Prerequisites**: macOS with Xcode 15+, iOS 16.0+ SDK
* Open `ios/OrionBrowser` in Xcode and select target simulator or connected iPhone.

### Install Android APK to Connected Device
```bash
adb install app/build/outputs/apk/debug/orion.apk
```

---

## 📜 Documentation
* [Technical Architecture & Blueprint](docs/ARCHITECTURE.md)

---

## 📄 License

Licensed under the [Apache License, Version 2.0](LICENSE).
