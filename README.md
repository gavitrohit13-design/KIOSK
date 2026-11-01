# RPG AUTO PRINT

> **Tagline:** Scan • Upload • Pay • Print

[![CI Workflow](https://github.com/gavitrohit13-design/KIOSK/actions/workflows/ci.yml/badge.svg)](https://github.com/gavitrohit13-design/KIOSK/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/gavitrohit13-design/KIOSK)](https://github.com/gavitrohit13-design/KIOSK/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Node.js Version](https://img.shields.io/badge/Node.js-22.x%20LTS-brightgreen.svg)](https://nodejs.org/)

An automated, cross-platform cloud print ecosystem designed for printing shops, cyber cafés, and universities. Customers scan a permanent shop QR code, upload documents, customize print parameters, and pay via UPI or cards. Once paid, orders automatically spool and print on the owner's hardware without manual intervention.

---

## 📋 Table of Contents
1. [Key Features](#-key-features)
2. [Multi-Tier Architecture](#-multi-tier-architecture)
3. [Installation](#-installation)
4. [Development](#-development)
5. [Build](#-build)
6. [Release & Packaging](#-release--packaging)
7. [GitHub Actions CI/CD](#-github-actions-cicd)
8. [Versioning Strategy](#-versioning-strategy)
9. [Deployment](#-deployment)
10. [Troubleshooting](#-troubleshooting)
11. [License](#-license)

---

## 🌟 Key Features

- **Customer Self-Service Portal & Web PWA**:
  - Zero login required. Instant unique Order ID generation (`ORD-XXXXXX`).
  - Document uploads: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, JPG, JPEG, and PNG.
  - Interactive file previewer with 70%–140% Zoom, 90° canvas rotation, page navigation, and thumbnail strip.
  - Dynamic pricing calculator enforcing owner rates, GST, discounts, and minimum order rules.
  - Multi-channel payments: Dynamic UPI QR code, Google Pay, PhonePe, Paytm, Razorpay cards, and Cash.
  - Real-time progress timeline & itemized GST Tax Invoice generator.
- **Owner Terminal & Management Panel**:
  - Auto-Print Master Engine: Automatically dispatches verified paid jobs to hardware printers.
  - Full queue controls: Pause, Resume, Cancel, and Reprint.
  - Multi-printer support: USB, Network, WiFi, Thermal Receipt, Laser, Inkjet, and Plotters.
  - Dynamic pricing editor: Edit B&W price, Colour price, GST %, Discount %, Service fee, and Shop details.
  - Additional services billing: Photocopy/Xerox, Scanning, Passport Photos, Lamination, and Spiral Binding.
  - Business analytics & paper consumption reports (A4, A3, Legal).
  - Permanent shop QR standee generator.
- **Trilingual Localization**:
  - Full support for English, Hindi (हिंदी), and Marathi (मराठी).

---

## 🏗️ Multi-Tier Architecture

```
[Customer Kiosk / Phone Web PWA]
        │ (1. Scan Shop QR & Upload File)
        ▼
[Cloud Database & Storage (Firebase Firestore / PostgreSQL)]
        │ (2. Real-time Order Snapshot & Payment Verification)
        ▼
[Owner Windows Desktop Terminal / Android Mobile Manager]
        │ (3. Auto-Print Engine Dispatches Spool)
        ▼
[Hardware Printers (Laser / Inkjet / Thermal / Plotter)]
```

| Component | Technology | Target Platform |
| :--- | :--- | :--- |
| **Mobile Manager** | Kotlin, Jetpack Compose, PrintManager | Android (Phones & Tablets) |
| **Owner Desktop App** | Electron, Node.js, WinSpool / PowerShell API | Windows 10/11 & Linux |
| **Customer Web Kiosk** | HTML5, CSS3, JavaScript, PWA Manifest | Any Modern Web Browser / Mobile |
| **Backend & APIs** | Node.js (22.x LTS), Express, REST | Docker / Linux Server / Windows |
| **Database** | Firebase Firestore & PostgreSQL | Cloud / Local On-Premise |

---

## 📦 Installation

### Prerequisites
- Node.js `22.x LTS` (or newer)
- npm `10.x+` (or yarn / pnpm)
- Java JDK 17 (for Android build)
- Windows 10/11 or Ubuntu 22.04 LTS

### Step-by-Step Setup
```bash
# 1. Clone repository
git clone https://github.com/gavitrohit13-design/KIOSK.git
cd KIOSK

# 2. Install dependencies (auto-detects package-lock.json)
npm install

# 3. Create environment configuration
cp .env.example .env
```

---

## 💻 Development

### Running Local Print Server & Web Kiosk
```bash
# Starts Node.js backend on http://localhost:3000
npm start
```
- Customer Web Portal: `http://localhost:3000/`
- Owner Dashboard: `http://localhost:3000/owner`
- API Health Check: `http://localhost:3000/api/health`

### Running Windows Desktop App (Electron)
```bash
npm run electron:start
```

### Running Test Suite
```bash
# Run unit & API test suite
npm test --if-present

# Run linter
npm run lint --if-present
```

---

## 🔨 Build

The project features unified build scripts supporting multi-platform outputs:

```bash
# Build Web Kiosk & Backend Bundles
npm run build --if-present

# Build Web Kiosk & PWA exclusively
npm run build:web --if-present

# Build Backend Server package exclusively
npm run build:backend --if-present

# Build Electron Desktop (Windows & Linux)
npm run electron:build --if-present

# Build Android Kiosk Terminal APK
gradle assembleDebug
```

---

## 🚀 Release & Packaging

### Generating Release Assets
When a tag (e.g. `v1.0.0`) is pushed to GitHub, the automated release workflow builds and packages:
1. **Windows Installer**: `rpg-autoprint-setup-v1.0.0.exe`
2. **Windows Portable**: `rpg-autoprint-windows-portable-v1.0.0.zip`
3. **Linux AppImage**: `rpg-autoprint-v1.0.0.AppImage`
4. **Linux Tarball**: `rpg-autoprint-linux-v1.0.0.tar.gz`
5. **Customer Web Kiosk**: `rpg-autoprint-web-v1.0.0.zip`
6. **Progressive Web App (PWA)**: `rpg-autoprint-pwa-v1.0.0.zip`
7. **Backend Server Package**: `rpg-autoprint-backend-server-v1.0.0.zip`
8. **Android Kiosk APK**: `rpg-autoprint-android-v1.0.0.apk`
9. **Complete Source Code**: `rpg-autoprint-source-v1.0.0.zip`
10. **Version Metadata**: `version.json`
11. **Release Notes**: `RELEASE_NOTES.md`

---

## 🤖 GitHub Actions CI/CD

Our workflows adhere to strict DevOps standards:
- **Zero-Failure Package Installation**: Dynamically detects whether `package-lock.json`, `yarn.lock`, or `pnpm-lock.yaml` is present. Uses `npm ci` when lockfiles exist and `npm install` otherwise.
- **Node.js 22 LTS**: Uses `actions/setup-node@v4` with active LTS runtime.
- **Fail-Safe Script Invocations**: Uses `--if-present` on all npm script invocations (`npm run lint --if-present`, `npm test --if-present`, `npm run build --if-present`) preventing failures if optional scripts are omitted.
- **Automated Framework Detection**: Automatically distinguishes Next.js, Vite, Electron, Express, and standard Node.js projects.
- **Persistent Artifact Upload**: Every successful build produces downloadable artifacts preserved for 14 days.

### Workflows Included:
1. `.github/workflows/ci.yml`: Continuous Integration, testing, multi-target build, and artifact preservation.
2. `.github/workflows/release.yml`: Triggered on tag `v*` or manual dispatch; compiles all release bundles, publishes a GitHub Release, and uploads all binaries.
3. `.github/workflows/security.yml`: Scheduled weekly vulnerability audit and secrets detection.

---

## 🏷️ Versioning Strategy

This project strictly follows [Semantic Versioning 2.0.0](https://semver.org/):
`v<MAJOR>.<MINOR>.<PATCH>` (e.g., `v1.0.0`)
- **MAJOR**: Incompatible API or breaking database schema changes.
- **MINOR**: New customer or owner kiosk capabilities in a backward-compatible manner.
- **PATCH**: Backward-compatible bug fixes and security patches.

To create a new release:
```bash
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1
```

---

## 🌐 Deployment

### 1. Docker Deployment (Recommended)
```bash
docker compose -f docker/docker-compose.yml up -d
```

### 2. Systemd Service (Linux Server)
```ini
[Unit]
Description=RPG Auto Print Server
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/var/www/rpg-auto-print
ExecStart=/usr/bin/node server.js
Restart=always
Environment=NODE_ENV=production PORT=3000

[Install]
WantedBy=multi-user.target
```

### 3. Windows Kiosk Mode
Place `rpg-autoprint-setup.exe` in the Windows Startup folder or configure Windows Assigned Access for kiosk locking.

---

## 🔧 Troubleshooting

| Symptom | Root Cause | Solution |
| :--- | :--- | :--- |
| `npm ci fails: package-lock.json not found` | Missing lockfile in container | The updated workflow uses dynamic package detection; run `npm install` locally to recreate lockfile. |
| `Printer not spooling on Windows` | Print Spooler service stopped | Run in Admin PowerShell: `Restart-Service Spooler`. |
| `Port 3000 already in use` | Existing server process running | Kill port: `npx kill-port 3000` or change `PORT=3001` in `.env`. |
| `Firestore permission denied` | Security rule mismatch | Check ABAC rules in `firestore.rules` and verify auth token claims. |

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
