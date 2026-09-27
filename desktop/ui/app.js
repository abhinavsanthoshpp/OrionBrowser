// ── ORION DESKTOP BROWSER CONTROLLER ──
// Architect & Developer: Abhinav Santhosh
// Copyright © 2026 Abhinav Santhosh. All Rights Reserved.

let tabs = [];
let activeTabId = null;
let detectedMediaList = [];

// DOM References
const tabStrip = document.getElementById('tab-strip');
const webviewContainer = document.getElementById('webview-container');
const btnNewTab = document.getElementById('btn-new-tab');
const omniboxInput = document.getElementById('omnibox-input');
const btnBack = document.getElementById('btn-back');
const btnForward = document.getElementById('btn-forward');
const btnReload = document.getElementById('btn-reload');
const btnHome = document.getElementById('btn-home');
const progressBar = document.getElementById('progress-bar');
const shieldCount = document.getElementById('shield-count');
const btnShields = document.getElementById('btn-shields');
const btnMediaSniffer = document.getElementById('btn-media-sniffer');
const mediaCount = document.getElementById('media-count');
const btnSupportDev = document.getElementById('btn-support-dev');

// Modals
const modalSupport = document.getElementById('modal-support');
const modalShields = document.getElementById('modal-shields');
const modalMedia = document.getElementById('modal-media');

// Window Controls
document.getElementById('win-min').addEventListener('click', () => window.orionAPI?.minimizeWindow());
document.getElementById('win-max').addEventListener('click', () => window.orionAPI?.maximizeWindow());
document.getElementById('win-close').addEventListener('click', () => window.orionAPI?.closeWindow());

// ── TAB MANAGEMENT ──
function createTab(initialUrl = 'newtab.html') {
  const tabId = 'tab_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5);

  // Create Webview
  const webview = document.createElement('webview');
  webview.id = 'wv_' + tabId;
  webview.setAttribute('allowpopups', 'true');
  webview.src = initialUrl;
  webviewContainer.appendChild(webview);

  // Tab Object
  const tab = {
    id: tabId,
    title: 'New Tab',
    url: initialUrl,
    favicon: 'assets/icon.png',
    webview: webview,
    canGoBack: false,
    canGoForward: false
  };
  tabs.push(tab);

  // Tab DOM Element
  const tabEl = document.createElement('div');
  tabEl.className = 'browser-tab';
  tabEl.id = 'el_' + tabId;
  tabEl.innerHTML = `
    <img class="tab-favicon" src="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%239C5FB8'><circle cx='12' cy='12' r='10'/></svg>" />
    <span class="tab-title">New Tab</span>
    <span class="tab-close-btn">&times;</span>
  `;

  tabEl.addEventListener('click', (e) => {
    if (e.target.classList.contains('tab-close-btn')) {
      closeTab(tabId);
    } else {
      switchTab(tabId);
    }
  });

  tabStrip.appendChild(tabEl);

  // Attach Webview Listeners
  setupWebviewEvents(tab);

  switchTab(tabId);
  return tab;
}

function switchTab(tabId) {
  activeTabId = tabId;
  const currentTab = tabs.find(t => t.id === tabId);
  if (!currentTab) return;

  // Update Tab Elements styling
  document.querySelectorAll('.browser-tab').forEach(el => el.classList.remove('active'));
  const activeEl = document.getElementById('el_' + tabId);
  if (activeEl) activeEl.classList.add('active');

  // Update Webviews
  document.querySelectorAll('webview').forEach(wv => wv.classList.remove('active'));
  currentTab.webview.classList.add('active');

  // Update Omnibox
  updateOmnibox(currentTab.url);

  // Update Navigation buttons
  updateNavButtons(currentTab);
}

function closeTab(tabId) {
  if (tabs.length === 1) {
    // If last tab closed, open fresh new tab
    const tab = tabs[0];
    tab.webview.src = 'newtab.html';
    return;
  }

  const idx = tabs.findIndex(t => t.id === tabId);
  if (idx === -1) return;

  const tabToRemove = tabs[idx];
  tabToRemove.webview.remove();
  const el = document.getElementById('el_' + tabId);
  if (el) el.remove();

  tabs.splice(idx, 1);

  if (activeTabId === tabId) {
    const nextIdx = Math.max(0, idx - 1);
    switchTab(tabs[nextIdx].id);
  }
}

function updateOmnibox(url) {
  if (url === 'newtab.html' || url.endsWith('newtab.html') || url === 'about:blank') {
    omniboxInput.value = '';
    omniboxInput.placeholder = 'Search with DuckDuckGo or enter web address…';
  } else {
    omniboxInput.value = url;
  }
}

function updateNavButtons(tab) {
  try {
    btnBack.disabled = !tab.webview.canGoBack();
    btnForward.disabled = !tab.webview.canGoForward();
  } catch (_) {
    btnBack.disabled = true;
    btnForward.disabled = true;
  }
}

function setupWebviewEvents(tab) {
  const wv = tab.webview;

  wv.addEventListener('did-start-loading', () => {
    if (activeTabId === tab.id) {
      progressBar.style.opacity = '1';
      progressBar.style.width = '35%';
    }
  });

  wv.addEventListener('did-stop-loading', () => {
    if (activeTabId === tab.id) {
      progressBar.style.width = '100%';
      setTimeout(() => {
        progressBar.style.opacity = '0';
        progressBar.style.width = '0%';
      }, 250);
      updateNavButtons(tab);
    }
  });

  wv.addEventListener('did-navigate', (e) => {
    tab.url = e.url;
    if (activeTabId === tab.id) {
      updateOmnibox(e.url);
      updateNavButtons(tab);
    }
  });

  wv.addEventListener('did-navigate-in-page', (e) => {
    tab.url = e.url;
    if (activeTabId === tab.id) {
      updateOmnibox(e.url);
      updateNavButtons(tab);
    }
  });

  wv.addEventListener('page-title-updated', (e) => {
    tab.title = e.title;
    const tabEl = document.getElementById('el_' + tab.id);
    if (tabEl) {
      const titleSpan = tabEl.querySelector('.tab-title');
      if (titleSpan) titleSpan.innerText = e.title || 'Untitled';
    }
  });

  wv.addEventListener('page-favicon-updated', (e) => {
    if (e.favicons && e.favicons.length > 0) {
      tab.favicon = e.favicons[0];
      const tabEl = document.getElementById('el_' + tab.id);
      if (tabEl) {
        const iconImg = tabEl.querySelector('.tab-favicon');
        if (iconImg) iconImg.src = e.favicons[0];
      }
    }
  });
}

// ── NAVIGATION CONTROLS ──
btnBack.addEventListener('click', () => {
  const currentTab = tabs.find(t => t.id === activeTabId);
  if (currentTab && currentTab.webview.canGoBack()) currentTab.webview.goBack();
});

btnForward.addEventListener('click', () => {
  const currentTab = tabs.find(t => t.id === activeTabId);
  if (currentTab && currentTab.webview.canGoForward()) currentTab.webview.goForward();
});

btnReload.addEventListener('click', () => {
  const currentTab = tabs.find(t => t.id === activeTabId);
  if (currentTab) currentTab.webview.reload();
});

btnHome.addEventListener('click', () => {
  const currentTab = tabs.find(t => t.id === activeTabId);
  if (currentTab) currentTab.webview.src = 'newtab.html';
});

btnNewTab.addEventListener('click', () => {
  createTab('newtab.html');
});

// Omnibox Navigation Handler
omniboxInput.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') {
    const input = omniboxInput.value.trim();
    if (!input) return;

    let targetUrl = input;
    if (input.includes('.') && !input.includes(' ')) {
      if (!input.startsWith('http://') && !input.startsWith('https://')) {
        targetUrl = 'https://' + input;
      }
    } else {
      targetUrl = 'https://duckduckgo.com/?q=' + encodeURIComponent(input);
    }

    const currentTab = tabs.find(t => t.id === activeTabId);
    if (currentTab) {
      currentTab.webview.src = targetUrl;
    }
  }
});

// Select omnibox on focus
omniboxInput.addEventListener('focus', () => {
  omniboxInput.select();
});

// Keyboard Shortcuts (Ctrl+T, Ctrl+W, Ctrl+R, Alt+Left, Alt+Right)
window.addEventListener('keydown', (e) => {
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 't') {
    e.preventDefault();
    createTab('newtab.html');
  } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'w') {
    e.preventDefault();
    if (activeTabId) closeTab(activeTabId);
  } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'r') {
    e.preventDefault();
    const currentTab = tabs.find(t => t.id === activeTabId);
    if (currentTab) currentTab.webview.reload();
  } else if (e.altKey && e.key === 'ArrowLeft') {
    const currentTab = tabs.find(t => t.id === activeTabId);
    if (currentTab && currentTab.webview.canGoBack()) currentTab.webview.goBack();
  } else if (e.altKey && e.key === 'ArrowRight') {
    const currentTab = tabs.find(t => t.id === activeTabId);
    if (currentTab && currentTab.webview.canGoForward()) currentTab.webview.goForward();
  }
});

// PostMessage listener from NewTab page
window.addEventListener('message', (e) => {
  if (e.data && e.data.type === 'navigate') {
    const currentTab = tabs.find(t => t.id === activeTabId);
    if (currentTab) currentTab.webview.src = e.data.url;
  } else if (e.data && e.data.type === 'open-support-modal') {
    modalSupport.classList.add('open');
  }
});

// ── MEDIA SNIFFER LISTENER ──
if (window.orionAPI?.onMediaDetected) {
  window.orionAPI.onMediaDetected((media) => {
    if (!detectedMediaList.some(m => m.url === media.url)) {
      detectedMediaList.unshift(media);
      btnMediaSniffer.style.display = 'flex';
      mediaCount.innerText = detectedMediaList.length;

      // Populate Modal list
      const mediaListContainer = document.getElementById('media-list');
      const item = document.createElement('div');
      item.className = 'support-link-card';
      item.innerHTML = `
        <div class="support-link-info">
          <h4>📹 ${media.filename}</h4>
          <p>${media.isHls ? 'HLS Video Stream (.m3u8)' : 'Direct Video Stream'}</p>
        </div>
        <button class="copy-btn">Download</button>
      `;
      item.querySelector('button').addEventListener('click', () => {
        window.orionAPI.downloadMedia(media.url, media.filename);
      });
      mediaListContainer.appendChild(item);
    }
  });
}

btnMediaSniffer.addEventListener('click', () => {
  modalMedia.classList.add('open');
});
document.getElementById('close-modal-media').addEventListener('click', () => {
  modalMedia.classList.remove('open');
});

// ── SHIELDS MODAL ──
btnShields.addEventListener('click', () => {
  modalShields.classList.add('open');
});
document.getElementById('close-modal-shields').addEventListener('click', () => {
  modalShields.classList.remove('open');
});
document.getElementById('toggle-adblock').addEventListener('change', (e) => {
  window.orionAPI?.toggleShields(e.target.checked);
});

// ── DEVELOPER SUPPORT & DONATIONS MODAL ──
btnSupportDev.addEventListener('click', () => {
  modalSupport.classList.add('open');
});
document.getElementById('close-modal-support').addEventListener('click', () => {
  modalSupport.classList.remove('open');
});

// Copy UPI Button
document.getElementById('btn-copy-upi').addEventListener('click', () => {
  const upi = document.getElementById('desktop-upi-id').innerText;
  navigator.clipboard.writeText(upi).then(() => {
    const btn = document.getElementById('btn-copy-upi');
    btn.innerText = 'Copied! ✓';
    btn.style.background = '#10B981';
    btn.style.color = '#000';
    setTimeout(() => {
      btn.innerText = 'Copy ID';
      btn.style.background = '';
      btn.style.color = '';
    }, 2000);
  });
});

// External Links
document.getElementById('link-bmac').addEventListener('click', () => {
  window.orionAPI?.openExternal('https://buymeacoffee.com/abhinavsanthoshpp');
});
document.getElementById('link-monetag').addEventListener('click', () => {
  window.orionAPI?.openExternal('http://hai8g.com/4/11759358');
});
document.getElementById('link-github').addEventListener('click', () => {
  window.orionAPI?.openExternal('https://github.com/abhinavsanthoshpp/OrionBrowser');
});

// Close modals on clicking overlay backdrop
[modalSupport, modalShields, modalMedia].forEach(modal => {
  modal.addEventListener('click', (e) => {
    if (e.target === modal) modal.classList.remove('open');
  });
});

// Initialize with First Tab
createTab('newtab.html');
