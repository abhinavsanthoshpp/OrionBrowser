const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('orionAPI', {
  // Window management
  minimizeWindow: () => ipcRenderer.send('window-minimize'),
  maximizeWindow: () => ipcRenderer.send('window-maximize'),
  closeWindow: () => ipcRenderer.send('window-close'),

  // External Links
  openExternal: (url) => ipcRenderer.send('open-external', url),

  // AdBlock & Privacy Stats
  getPrivacyStats: () => ipcRenderer.invoke('get-privacy-stats'),
  toggleShields: (enabled) => ipcRenderer.send('toggle-shields', enabled),
  setSecurityLevel: (level) => ipcRenderer.send('set-security-level', level),

  // Media Sniffer
  onMediaDetected: (callback) => {
    ipcRenderer.on('media-detected', (_event, data) => callback(data));
  },
  downloadMedia: (url, filename) => ipcRenderer.send('download-media', { url, filename }),

  // Settings & Storage
  getSettings: () => ipcRenderer.invoke('get-settings'),
  saveSettings: (settings) => ipcRenderer.send('save-settings', settings)
});
