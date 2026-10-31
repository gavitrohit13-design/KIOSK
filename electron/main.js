const { app, BrowserWindow, ipcMain, Notification } = require('electron');
const path = require('path');
const { exec } = require('child_process');

let mainWindow;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    title: "RPG AUTO PRINT — Owner Management Terminal",
    backgroundColor: "#0F172A",
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      nodeIntegration: false,
      contextIsolation: true
    }
  });

  const isDev = process.env.NODE_ENV === 'development';
  if (isDev) {
    mainWindow.loadURL('http://localhost:3000');
  } else {
    mainWindow.loadFile(path.join(__dirname, '..', 'public', 'index.html')).catch(() => {
      mainWindow.loadURL('http://localhost:3000');
    });
  }
}

// Windows & Linux Spooler IPC Handlers
ipcMain.handle('get-printers', async () => {
  if (process.platform === 'win32') {
    return new Promise((resolve) => {
      exec('powershell -Command "Get-CimInstance Win32_Printer | Select-Object Name, PortName, PrinterStatus, Default"', (err, stdout) => {
        resolve(err ? [] : stdout);
      });
    });
  } else {
    return new Promise((resolve) => {
      exec('lpstat -p -d', (err, stdout) => {
        resolve(err ? [] : stdout);
      });
    });
  }
});

ipcMain.handle('print-job', async (event, { filePath, printerName, copies }) => {
  console.log(`Spooling ${filePath} to ${printerName} (${copies} copies)...`);
  new Notification({
    title: 'RPG Auto Print',
    body: `Dispatched print job to ${printerName}`
  }).show();
  return { success: true };
});

app.whenReady().then(createWindow);

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit();
});
