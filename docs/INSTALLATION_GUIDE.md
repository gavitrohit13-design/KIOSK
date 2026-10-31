# RPG AUTO PRINT KIOSK SYSTEM — Complete Installation & Setup Guide

**Tagline:** *Scan • Upload • Pay • Print*  
**Version:** `1.0.0`

---

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Component 1: Local Print Server & Customer Web Portal](#component-1-local-print-server--customer-web-portal)
4. [Component 2: Windows Desktop Owner PC Application](#component-2-windows-desktop-owner-pc-application)
5. [Component 3: Android Kiosk Terminal Application](#component-3-android-kiosk-terminal-application)
6. [Component 4: Hardware Printer Configuration](#component-4-hardware-printer-configuration)
7. [Component 5: UPI Payment Gateway & QR Standee Setup](#component-5-upi-payment-gateway--qr-standee-setup)
8. [Component 6: Production Deployment & Docker](#component-6-production-deployment--docker)
9. [Troubleshooting & Verification](#troubleshooting--verification)

---

## Architecture Overview

```
                               ┌──────────────────────────────────────────────┐
                               │            Shop QR Standee Code              │
                               │          (Scan with any smartphone)          │
                               └──────────────────────┬───────────────────────┘
                                                      │
                                                      ▼
                               ┌──────────────────────────────────────────────┐
                               │         Customer Web Portal & PWA            │
                               │     (Zero login, Previewer, UPI QR Pay)      │
                               └──────────────────────┬───────────────────────┘
                                                      │ HTTP / WebSocket / REST
                                                      ▼
┌──────────────────────────────┐        ┌──────────────────────────────┐        ┌──────────────────────────────┐
│   Android Kiosk Terminal     │        │    Local Print Server        │        │   Owner Desktop App (Win)    │
│  (Jetpack Compose + Cloud)   │◄──────►│       (Node.js/Express)      │◄──────►│   (Electron + Win32 Spool)   │
└──────────────┬───────────────┘        └──────────────┬───────────────┘        └──────────────┬───────────────┘
               │                                       │                                       │
               │ Google Auth                           │ Spool Job                             │ WinSpool / CUPS
               ▼                                       ▼                                       ▼
┌──────────────────────────────┐        ┌──────────────────────────────┐        ┌──────────────────────────────┐
│   Firebase Firestore Cloud   │        │     Static Storage / Spool   │        │  Hardware Printers (USB/LAN) │
│ (Strict ABAC Security Rules) │        │     uploads/ & print_jobs/   │        │   Laser, Inkjet, Thermal     │
└──────────────────────────────┘        └──────────────────────────────┘        └──────────────────────────────┘
```

---

## Prerequisites

| Requirement | Minimal Specification | Recommended Specification |
|---|---|---|
| **OS (Owner PC)** | Windows 10 (64-bit) / Ubuntu 22.04 LTS | Windows 11 Pro (64-bit) |
| **Node.js** | Node.js v18.x LTS | Node.js v20.x or v22.x LTS |
| **Java / Android** | JDK 17 (for Android build) | OpenJDK 17 + Android Studio Ladybug+ |
| **Hardware Printers** | 1x USB or WiFi/Ethernet Laser Printer | 1x B&W Duplex Laser + 1x Colour Inkjet |
| **Network** | Local Wi-Fi Router with mDNS/Local IP | Static Local IP for Owner PC (e.g., `192.168.1.100`) |

---

## Component 1: Local Print Server & Customer Web Portal

The local server runs both the REST API and serves the responsive Customer Web Portal (`public/index.html`).

### Step 1: Install Dependencies
```bash
git clone https://github.com/gavitrohit13-design/KIOSK.git
cd KIOSK
npm install
```

### Step 2: Configure Environment Variables
Create `.env` in the root folder:
```ini
PORT=3000
HOST=0.0.0.0
NODE_ENV=production

# Shop Configuration
SHOP_ID=rpg_kiosk_01
SHOP_NAME=RPG AUTO PRINT
OWNER_UPI_ID=rpgautoprint@upi
OWNER_PASSWORD=admin123

# Default Pricing (Owner editable in Dashboard)
PRICE_BW=10.00
PRICE_COLOR=20.00
GST_PERCENT=18.00

# Security
JWT_SECRET=super_secret_kiosk_jwt_token_2026
```

### Step 3: Start the Server
```bash
# Development mode with hot reload
npm run dev

# Or Production background service
npm start
```
The server will bind to `http://0.0.0.0:3000`:
- **Customer Web Portal**: `http://<YOUR-LOCAL-IP>:3000/`
- **Owner Dashboard / PC Manager**: `http://<YOUR-LOCAL-IP>:3000/owner`
- **Health Check**: `http://<YOUR-LOCAL-IP>:3000/api/health`

---

## Component 2: Windows Desktop Owner PC Application

The Electron desktop application integrates directly with Windows Print Spooler (`winspool.drv` via PowerShell) for zero-click automatic printing.

### Step 1: Run in Development Mode
```bash
npm run electron
```

### Step 2: Package Windows Installer (.exe) & Portable (.zip)
```bash
# Package for Windows
npx electron-builder --win nsis zip

# Output location:
# dist/RPG AUTO PRINT Setup 1.0.0.exe
# dist/RPG AUTO PRINT-1.0.0-win.zip
```

### Step 3: Windows Auto-Start Setup
1. Launch `RPG AUTO PRINT.exe`.
2. Enter the Owner Password (`admin123`).
3. Toggle **"Enable Auto-Print Engine"** to ON.
4. (Optional) Check "Start on Windows Boot" in Windows Task Manager > Startup.

---

## Component 3: Android Kiosk Terminal Application

For counter tablets or mobile shop kiosks, the Jetpack Compose app provides cloud synchronization, Google Sign-In, and native Android `PrintManager` integration.

### Step 1: Compile the APK
Using Gradle in terminal:
```bash
gradle :app:assembleRelease
# Output APK: app/build/outputs/apk/release/app-release.apk
```
Or for debug/testing:
```bash
gradle :app:assembleDebug
# Output APK: app/build/outputs/apk/debug/app-debug.apk
```

### Step 2: Install via ADB or Sideload
```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

### Step 3: First Run Initialization
1. Open **RPG AUTO PRINT** on the Android device.
2. Sign in with the Shop Owner's Google account via Credential Manager.
3. Access **Shop Settings** via the Top Bar gear icon to verify:
   - Base B&W Price (Default: ₹10)
   - Base Colour Price (Default: ₹20)
   - GST % (Default: 18%)
   - Owner UPI ID

---

## Component 4: Hardware Printer Configuration

The system automatically detects all connected local and networked printers.

### Windows Printer Mapping:
1. Open Windows **Settings > Bluetooth & devices > Printers & scanners**.
2. Connect your printers:
   - **B&W Laser Printer**: e.g., *HP LaserJet Pro M404dn*, *Brother HL-L2321D*
   - **Colour Inkjet/Laser**: e.g., *Epson EcoTank L3250*, *Canon PIXMA G3000*
3. Rename printers in Windows to clear names:
   - `SHOP_BW_PRINTER`
   - `SHOP_COLOR_PRINTER`
4. In the Owner Dashboard, assign `SHOP_BW_PRINTER` as the primary B&W queue and `SHOP_COLOR_PRINTER` as the colour queue.

### Linux / Raspberry Pi Kiosk:
Ensure CUPS is installed:
```bash
sudo apt-get install cups cups-client printer-driver-all
sudo usermod -a -G lpadmin $USER
lpstat -p -d
```

---

## Component 5: UPI Payment Gateway & QR Standee Setup

### 1. Permanent Shop QR Standee
- Print the permanent Shop QR Standee from the Owner Dashboard (`ShopQrCodeDialog` or Web `/owner`).
- Place the laminated QR Standee at the shop counter or storefront.
- When scanned, it opens the web app URL: `https://print.yourshop.com` or `http://192.168.1.100:3000`.

### 2. Dynamic Order UPI Payment
- For each order, the system computes the exact bill amount (including GST and page multipliers).
- Generates a dynamic UPI deep-link:
  `upi://pay?pa=rpgautoprint@upi&pn=RPG%20AUTO%20PRINT&am=35.40&tr=ORD-9842&tn=Order-ORD-9842&cu=INR`
- Customer scans the dynamic screen QR with Google Pay, PhonePe, Paytm, BHIM, or any UPI banking app.
- Upon payment confirmation (auto or 12-digit UTR verification), the print job is unlocked and dispatched.

---

## Component 6: Production Deployment & Docker

### Running via Docker Compose
Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  rpg-print-server:
    build: .
    container_name: rpg_auto_print
    restart: always
    ports:
      - "3000:3000"
    environment:
      - PORT=3000
      - NODE_ENV=production
      - SHOP_ID=rpg_kiosk_01
      - SHOP_NAME=RPG AUTO PRINT
      - OWNER_UPI_ID=rpgautoprint@upi
    volumes:
      - ./uploads:/app/uploads
      - ./print_jobs:/app/print_jobs
```

Start the container:
```bash
docker compose up -d
```

---

## Troubleshooting & Verification

| Issue | Cause | Solution |
|---|---|---|
| "Printer not detected" | Printer driver offline or USB disconnected | Check printer power cable and verify status in Windows `Printers & scanners`. Restart Windows Print Spooler service (`net stop spooler && net start spooler`). |
| "Order created but not printing" | Auto-Print engine paused or pending payment | Verify payment status in the Owner Dashboard. Ensure the **"Auto-Print Engine"** switch is enabled in the Owner Panel. |
| "Customer cannot open shop URL" | Mobile phone not connected to shop Wi-Fi or firewall blocking port 3000 | Open Port 3000 in Windows Defender Firewall: `netsh advfirewall firewall add rule name="RPG Print Server" dir=in action=allow protocol=TCP localport=3000`. |
| "Document preview blank" | File format corrupted or unsupported extension | Ensure files are `.pdf`, `.doc`, `.docx`, `.jpg`, or `.png`. Use the interactive zoom/rotate controls to refresh preview. |

---

*RPG AUTO PRINT KIOSK SYSTEM — Engineering Documentation*
