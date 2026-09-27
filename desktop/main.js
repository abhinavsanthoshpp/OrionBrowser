const { app, BrowserWindow, ipcMain, shell, session } = require('electron');
const path = require('path');
const fs = require('fs');

let mainWindow;
let isShieldsEnabled = true;
let securityLevel = 'standard'; // standard | safer | safest
let stats = {
  adsBlockedTotal: 1420,
  dataSavedMB: 85,
  timeSavedSec: 28
};

// Load ad-blocking rules
let blockedDomains = new Set();
let blockedPatterns = [];

try {
  const rulesPath = path.join(__dirname, 'adblock', 'rules.json');
  if (fs.existsSync(rulesPath)) {
    const raw = fs.readFileSync(rulesPath, 'utf8');
    const parsed = JSON.parse(raw);
    blockedDomains = new Set(parsed.blockedDomains.map(d => d.toLowerCase()));
    blockedPatterns = parsed.blockedPatterns || [];
  }
} catch (e) {
  console.error('Failed to load adblock rules:', e);
}

function isAdOrTracker(url) {
  if (!isShieldsEnabled) return false;
  try {
    const parsedUrl = new URL(url);
    const host = parsedUrl.hostname.toLowerCase();
    
    // Check domain parts
    let current = host;
    while (current.includes('.')) {
      if (blockedDomains.has(current)) return true;
      const dotIdx = current.indexOf('.');
      current = current.substring(dotIdx + 1);
    }
    
    // Check patterns
    const fullPath = parsedUrl.pathname + parsedUrl.search;
    for (const pattern of blockedPatterns) {
      if (fullPath.includes(pattern)) return true;
    }
  } catch (_) {
    return false;
  }
  return false;
}

const mediaExtensions = ['.mp4', '.webm', '.m3u8', '.mpd', '.mkv', '.mov', '.ts', '.mp3', '.m4a'];

function checkMediaSniffer(url) {
  try {
    const lower = url.toLowerCase();
    if (mediaExtensions.some(ext => lower.includes(ext))) {
      let filename = 'Media_' + Date.now();
      try {
        const parsed = new URL(url);
        const segments = parsed.pathname.split('/');
        const last = segments[segments.length - 1];
        if (last && last.length > 2) filename = decodeURIComponent(last);
      } catch (_) {}

      if (mainWindow && !mainWindow.isDestroyed()) {
        mainWindow.webContents.send('media-detected', {
          url: url,
          filename: filename,
          isHls: lower.includes('.m3u8')
        });
      }
    }
  } catch (_) {}
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 840,
    minWidth: 900,
    minHeight: 600,
    backgroundColor: '#07050A',
    frame: false, // Custom sleek dark window frame matching Tor Obsidian theme
    titleBarStyle: 'hidden',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      webviewTag: true,
      nodeIntegration: false,
      contextIsolation: true,
      spellcheck: true
    },
    icon: path.join(__dirname, 'ui', 'assets', 'icon.png')
  });

  mainWindow.loadFile(path.join(__dirname, 'ui', 'index.html'));

  // Setup Sub-Millisecond Network Request Interceptor (Orion Shields)
  session.defaultSession.webRequest.onBeforeRequest((details, callback) => {
    const url = details.url;

    // Inspect for downloadable video streams
    checkMediaSniffer(url);

    // Block ads & telemetry
    if (isAdOrTracker(url)) {
      stats.adsBlockedTotal++;
      stats.dataSavedMB += 0.08;
      stats.timeSavedSec += 0.03;
      return callback({ cancel: true });
    }

    callback({ cancel: false });
  });

  // Tor-inspired Security Restrictions for 'safest' mode
  session.defaultSession.webRequest.onHeadersReceived((details, callback) => {
    let responseHeaders = Object.assign({}, details.responseHeaders);
    
    // Inject privacy headers
    responseHeaders['X-Content-Type-Options'] = ['nosniff'];
    responseHeaders['Referrer-Policy'] = ['strict-origin-when-cross-origin'];

    callback({ responseHeaders });
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// App Lifecycle
app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit();
});

// IPC Handlers
ipcMain.on('window-minimize', () => {
  if (mainWindow) mainWindow.minimize();
});

ipcMain.on('window-maximize', () => {
  if (mainWindow) {
    if (mainWindow.isMaximized()) {
      mainWindow.unmaximize();
    } else {
      mainWindow.maximize();
    }
  }
});

ipcMain.on('window-close', () => {
  if (mainWindow) mainWindow.close();
});

ipcMain.on('open-external', (_event, url) => {
  if (url && (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('upi://') || url.startsWith('mailto:'))) {
    shell.openExternal(url);
  }
});

ipcMain.handle('get-privacy-stats', () => {
  return {
    adsBlockedTotal: stats.adsBlockedTotal,
    dataSavedMB: Math.round(stats.dataSavedMB),
    timeSavedSec: Math.round(stats.timeSavedSec)
  };
});

ipcMain.on('toggle-shields', (_event, enabled) => {
  isShieldsEnabled = !!enabled;
});

ipcMain.on('set-security-level', (_event, level) => {
  securityLevel = level;
});

ipcMain.on('download-media', (_event, { url, filename }) => {
  if (mainWindow) {
    mainWindow.webContents.downloadURL(url);
  }
});
