# Changelog

All notable changes to the **RPG AUTO PRINT** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [v1.0.0] - 2026-10-31

### New Features
- **Customer Self-Service Kiosk & Web PWA**:
  - Instant document upload supporting PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, JPG, JPEG, and PNG.
  - Interactive page preview with Zoom in/out, 90° rotation, page navigation, and thumbnail strip.
  - Automatic page counter and file size detection.
  - Configuration options for B&W vs Full Colour, Single vs Double Side (Duplex), Paper Size (A4, A3, Legal), Orientation (Portrait/Landscape), and print quality (Normal, High, Draft).
  - Dynamic transparent price calculator enforcing shop owner rates, GST, discounts, and service fees.
  - Multi-channel payment gateway supporting Dynamic UPI QR, Google Pay, PhonePe, Paytm, Razorpay cards, and Counter Cash.
  - Real-time order progress stepper (*Payment Verified* ➔ *Order Accepted* ➔ *Printing in Progress* ➔ *Ready for Collection*).
  - Itemized GST Tax Invoice generator with instant print and share features.
- **Owner Terminal & Management Panel**:
  - Auto-Print Master Engine with instant dispatch to hardware spoolers upon payment verification.
  - Live Queue manager with filters (*All, Queue, Printing, Completed, Cancelled*).
  - Queue action controls: Manual Print Now, Pause Queue, Resume Queue, Cancel Job, and Reprint.
  - Hardware Printer Manager supporting USB, Network, WiFi, Laser, Inkjet, Thermal Receipt, and Plotters with live paper and toner/ink telemetry.
  - Owner dynamic pricing and shop profile settings editor (B&W rate, Colour rate, GST %, Discount %, Service Fee, Min Order, Shop Name, UPI ID).
  - Permanent Shop QR Code Standee generator with PNG, SVG, and text export.
  - Analytics & Reports suite showing daily/monthly revenue, sheets consumed by paper size, and service breakdown.
  - Additional Services Counter Billing for Photocopy / Xerox, Document Scanning, Passport Photos (8-photo set), Lamination, and Spiral Binding.
- **Tri-Lingual Localization**:
  - Complete native language support for English, Hindi (हिंदी), and Marathi (मराठी).
- **Multi-Platform Ecosystem**:
  - Android Mobile Terminal (Jetpack Compose + PrintManager).
  - Windows Desktop Owner Panel (Electron + WinSpool API).
  - Web Application & Progressive Web App (PWA).
  - Node.js + Express Backend with PostgreSQL & Firebase Firestore real-time sync.

### Improvements
- Zero-latency Firestore snapshot listeners for instant synchronization between Customer phone and Owner desktop.
- Hardened Attribute-Based Access Control (ABAC) Firestore security rules preventing unauthorized price tampering or order modification.
- Responsive container layouts conforming strictly to Material Design 3 spacing and accessibility standards.

### Bug Fixes
- Fixed property undefined error in Firestore rule evaluator by safely validating auth token claims before property extraction.
- Added missing `OutlinedButton` component import in hardware printer management dialog.
- Resolved database ID binding ensuring local emulators bind specifically to the provisioned database ID.

### Breaking Changes
- None (Initial Production Release).

### Known Issues
- Large plotters requiring custom paper rolls (> A3) require manual tray configuration on older USB drivers without bidirectional status.
