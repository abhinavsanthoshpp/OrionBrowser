# 🌌 Orion Browser for Desktop (Linux & Windows)

**Architected & Developed by [Abhinav Santhosh](https://github.com/abhinavsanthoshpp)**  
**Copyright &copy; 2026 Abhinav Santhosh. All Rights Reserved.**

A high-performance, Tor-inspired desktop web browser engineered for **Linux** and **Windows**, featuring sub-millisecond native ad/tracker blocking, multi-tab workflow, media stream sniffer, and encrypted DoH DNS acceleration.

---

## 🌟 Desktop Features

* **🛡️ Tor-Inspired Obsidian Chrome**: Custom frameless dark interface (`#07050A`, `#0F0A18`) with glowing neon violet accents.
* **⚡ Orion Shields Network Interceptor**: Drops telemetry, trackers, and intrusive ad scripts before network socket allocation.
* **🗂️ Desktop Multi-Tab Engine**: Dynamic tab strip with audio indicators, favicon recognition, and keyboard shortcuts (`Ctrl+T`, `Ctrl+W`, `Ctrl+R`).
* **🔎 Desktop Omnibox**: Centered URL and search omnibox with DuckDuckGo fallback and SSL certificate validation.
* **📥 Real-Time Media Sniffer**: Automatically detects video and audio streams (.mp4, .webm, .m3u8 HLS) and provides 1-click download options.
* **💛 Creator Patronage**: Direct UPI transfers (India: `abhinava6525@naviaxis`), Buy Me A Coffee (Global), and 1-Click free sponsor link.

---

## 🚀 Quick Launch Instructions

### On Linux
```bash
cd desktop
./orion-linux.sh
```
Or directly using npm:
```bash
cd desktop
npm start
```

### On Windows
Double-click `orion-windows.bat` or run in Command Prompt / PowerShell:
```cmd
cd desktop
orion-windows.bat
```
Or via npm:
```cmd
cd desktop
npm start
```

---

## 📦 Packaging Standalone Binaries

To generate standalone distributable binaries for Linux and Windows:

```bash
# Install electron-packager
npm install -g electron-packager

# Build for Linux (x64)
npm run pack:linux

# Build for Windows (x64)
npm run pack:win
```

---

## 📄 Copyright & Proprietary Rights

**Copyright &copy; 2026 Abhinav Santhosh. All Rights Reserved.**  
This software, source code, UI designs, and assets are proprietary. Unauthorized copying, modification, decompilation, or distribution is strictly prohibited.
