# RPG AUTO PRINT — Windows Desktop Application (Owner Panel)

Architecture and implementation guide for the Windows Desktop Owner Panel built with **Electron + Node.js + Windows Print Spooler API**.

---

## 1. Electron Architecture Overview

```
[Customer Kiosk / Phone]
        │ (1. Scan QR & Upload & Pay)
        ▼
[Firebase Firestore / Node Backend]
        │ (2. Real-time Snapshot / WebSocket Event)
        ▼
[Electron Windows Desktop App (Owner PC)]
        │
   ┌────┴───────────────────────────┐
   │ Auto Print Engine Enabled?      │
   ├───────────────┬────────────────┤
   │ YES           │ NO             │
   ▼               ▼                │
[Direct Spool]   [Live Queue UI]    │
   │             (Owner clicks      │
   │              "Print Now")      │
   └───────────────┬────────────────┘
                   ▼
       [Windows Print Spooler]
  (win32-print / pdf-to-printer / PowerShell)
                   │
    ┌──────────────┼──────────────┐
    ▼              ▼              ▼
[HP LaserJet] [Canon Colour] [Thermal POS]
```

---

## 2. Core Electron Spooler Implementation (`electron/main.js`)

```javascript
const { app, BrowserWindow, ipcMain, Notification } = require('electron');
const path = require('path');
const ptp = require('pdf-to-printer');
const { exec } = require('child_process');
const fs = require('fs');

let mainWindow;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    title: "RPG AUTO PRINT - Owner Terminal",
    icon: path.join(__dirname, 'assets', 'icon.ico'),
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      nodeIntegration: false,
      contextIsolation: true
    }
  });

  mainWindow.loadURL('http://localhost:3000/owner');
}

// 1. Enumerate Windows Printers via Win32 / PowerShell
ipcMain.handle('get-windows-printers', async () => {
  return new Promise((resolve, reject) => {
    exec('powershell -Command "Get-CimInstance Win32_Printer | Select-Object Name, PortName, PrinterStatus, Default"', (err, stdout) => {
      if (err) return reject(err);
      resolve(stdout);
    });
  });
});

// 2. Dispatch Direct Print to Hardware
ipcMain.handle('print-document', async (event, { filePath, printerName, copies, paperSize, colorMode, duplex }) => {
  const options = {
    printer: printerName,
    copies: copies || 1,
    paperSize: paperSize || 'A4',
    side: duplex === 'DOUBLE' ? 'duplex' : 'simplex'
  };

  try {
    await ptp.print(filePath, options);
    new Notification({
      title: 'Auto Print Success',
      body: `Printed to ${printerName}`
    }).show();
    return { success: true };
  } catch (error) {
    console.error('Spooler Error:', error);
    return { success: false, error: error.message };
  }
});

app.whenReady().then(createWindow);
```

---

## 3. Windows Installer Packaging (`electron-builder.json`)

To build the Windows `.exe` / `.msi` standalone installer:

```json
{
  "appId": "com.rpg.autoprint",
  "productName": "RPG AUTO PRINT",
  "directories": {
    "output": "dist-electron"
  },
  "win": {
    "target": ["nsis", "portable"],
    "icon": "assets/icon.ico"
  },
  "nsis": {
    "oneClick": false,
    "allowToChangeInstallationDirectory": true,
    "createDesktopShortcut": true,
    "createStartMenuShortcut": true,
    "shortcutName": "RPG Auto Print"
  }
}
```

Run build:
```bash
npm run electron:build
```
Produces: `dist-electron/RPG-AUTO-PRINT-Setup-1.0.0.exe`.
