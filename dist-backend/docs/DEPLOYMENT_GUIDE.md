# RPG AUTO PRINT — Complete System Deployment Guide

System-wide deployment instructions for Web, PWA, Windows Desktop, Backend, and Android Mobile Terminals.

---

## 1. System Architecture

| Component | Technology | Purpose |
| :--- | :--- | :--- |
| **Android Terminal** | Kotlin + Jetpack Compose + PrintManager | Mobile counter terminal, order placement & live queue |
| **Web Customer Portal** | React + TypeScript + Tailwind CSS | Instant QR upload and payment kiosk on customer phone |
| **Progressive Web App (PWA)** | Service Worker + CacheStorage | Offline-ready mobile portal with install prompt |
| **Owner Desktop App** | Electron + WinSpool API | Windows hardware print spooler & live shop dashboard |
| **Backend & APIs** | Node.js + Express + WebSocket | Real-time queue sync, payment webhooks, invoice generation |
| **Database** | PostgreSQL + Firebase Firestore | Cloud order persistence, ABAC security rules, real-time listeners |

---

## 2. Progressive Web App (PWA) Configuration

### `manifest.json`
```json
{
  "short_name": "RPG Print",
  "name": "RPG AUTO PRINT - Scan • Upload • Pay • Print",
  "icons": [
    {
      "src": "/icons/icon-192.png",
      "type": "image/png",
      "sizes": "192x192"
    },
    {
      "src": "/icons/icon-512.png",
      "type": "image/png",
      "sizes": "512x512"
    }
  ],
  "start_url": "/",
  "background_color": "#0F172A",
  "theme_color": "#1E3A8A",
  "display": "standalone",
  "orientation": "portrait"
}
```

---

## 3. Docker Production Deployment

1. Clone or copy project files into server:
```bash
git clone <repository_url> rpg-autoprint
cd rpg-autoprint
```

2. Configure environment variables in `.env`:
```ini
NODE_ENV=production
PORT=3000
DATABASE_URL=postgres://rpg_admin:secure_rpg_password_2026@postgres:5432/rpg_autoprint
JWT_SECRET=super_secret_jwt_key_2026
SHOP_NAME=RPG AUTO PRINT
UPI_ID=rpgautoprint@upi
AUTO_PRINT_ENABLED=true
DOMAIN=print.yourshop.com
```

3. Launch system via Docker Compose:
```bash
docker compose -f docker/docker-compose.yml up -d
```

4. Verify health:
```bash
docker ps
curl -i http://localhost:3000/api/v1/health
```

---

## 4. End-to-End Verification Checklist

- [x] **Shop Permanent QR Code**: Customer scans QR code; web portal loads automatically.
- [x] **Document Upload**: Supports PDF, DOCX, XLSX, PPTX, JPG, PNG with page detection.
- [x] **Interactive Preview**: Zoom in/out, 90° rotation, thumbnail slider, page navigation.
- [x] **Live Pricing Math**: Page count × Copies × Rate + GST + Service Fee - Discount.
- [x] **Payment Security**: UPI QR & gateway confirmation; printing locked until `paymentStatus == 'PAID'`.
- [x] **Auto Print Spooling**: Status switches from `PENDING` ➔ `ACCEPTED` ➔ `PRINTING` ➔ `COMPLETED`.
- [x] **Tax Invoice**: Itemized breakdown with download, share, and print features.
- [x] **Owner Queue Controls**: Pause, Resume, Cancel, Reprint, and hardware printer switching.
- [x] **Multi-language**: Seamless instant toggle between English, Hindi, and Marathi.
