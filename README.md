# RPG AUTO PRINT

> **Tagline:** Scan • Upload • Pay • Print

[![CI Workflow](https://github.com/owner/rpg-auto-print/actions/workflows/ci.yml/badge.svg)](https://github.com/owner/rpg-auto-print/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/owner/rpg-auto-print)](https://github.com/owner/rpg-auto-print/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An automated, cross-platform cloud print ecosystem designed for printing shops, cyber cafés, and universities. Customers scan a permanent shop QR code, upload documents, customize print parameters, and pay via UPI or cards. Once paid, orders automatically spool and print on the owner's hardware without manual intervention.

---

## 🌟 Key Features

- **Customer Self-Service Portal & Web PWA**:
  - No login required for customers. Instant unique Order ID generation (`ORD-XXXXXX`).
  - Document uploads: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, JPG, JPEG, and PNG.
  - Interactive file previewer with Zoom in/out, 90° rotation, page navigation, and thumbnail strip.
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

| Component | Technology | Platform |
| :--- | :--- | :--- |
| **Mobile Manager** | Kotlin, Jetpack Compose, PrintManager | Android (Phones & Tablets) |
| **Owner Desktop App** | Electron, Node.js, WinSpool API | Windows 10/11 & Linux |
| **Customer Web Kiosk** | React, TypeScript, Tailwind CSS, PWA | Any Web Browser / Mobile |
| **Backend & APIs** | Node.js, Express, WebSockets | Docker / Linux Server |
| **Database** | Firebase Firestore & PostgreSQL | Cloud / On-Premise |

---

## 🚀 Quick Start

### 1. Android Application
```bash
# Build Debug APK
./gradlew :app:assembleDebug

# Run Unit and Firestore Rules Tests
./gradlew :app:testDebugUnitTest --tests '*Rule*'
```

### 2. Docker Deployment (Backend + Database + Web Kiosk)
```bash
docker compose -f docker/docker-compose.yml up -d
```

### 3. Windows Desktop Owner Panel
```bash
npm install
npm run electron:start
# Package Windows Installer (.exe)
npm run electron:build
```

---

## 🔄 Automated CI/CD & GitHub Releases

This repository includes automated GitHub Actions workflows:

1. **Continuous Integration (`.github/workflows/ci.yml`)**:
   - Triggers on every push to `main`, pull request, and manual `workflow_dispatch`.
   - Runs linting, unit tests, security audits, and builds all project targets.
   - Uploads build artifacts on every successful run.
2. **Automated Release Packaging (`.github/workflows/release.yml`)**:
   - Triggers when a release tag (e.g. `v1.0.0`) is pushed or release created.
   - Builds and packages:
     - Windows Installer (`.exe`)
     - Windows Portable (`.zip`)
     - Linux AppImage (`.AppImage`) & Archive (`.tar.gz`)
     - Android APK (`.apk`)
     - Web Kiosk build (`.zip`)
     - PWA build (`.zip`)
     - Backend Server package (`.zip`)
   - Automatically generates release notes categorized by Features, Improvements, Fixes, and Known Issues.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
