# RPG AUTO PRINT — REST API & Event Contract

Complete API Specification for the RPG Auto Print System (Node.js + Express Backend).

---

## 1. Customer Endpoints

### 1.1 Calculate Real-time Price Quote
- **Endpoint:** `POST /api/v1/orders/quote`
- **Auth:** None (Public Kiosk)
- **Request Body:**
```json
{
  "pageCount": 14,
  "copies": 2,
  "colorMode": "COLOR",    // "BW" | "COLOR"
  "duplexMode": "DOUBLE",  // "SINGLE" | "DOUBLE"
  "paperSize": "A4"        // "A4" | "A3" | "LEGAL"
}
```
- **Response:**
```json
{
  "subtotal": 504.00,
  "gstAmount": 90.72,
  "serviceCharge": 2.00,
  "discountAmount": 0.00,
  "totalAmount": 596.72,
  "minOrderApplied": false
}
```

---

### 1.2 Create Print Order & Upload Document
- **Endpoint:** `POST /api/v1/orders/upload`
- **Content-Type:** `multipart/form-data`
- **Fields:**
  - `file`: Binary file (PDF, DOCX, PPTX, XLSX, JPG, PNG)
  - `customerName`: String
  - `customerPhone`: String
  - `colorMode`: "BW" | "COLOR"
  - `duplexMode`: "SINGLE" | "DOUBLE"
  - `paperSize`: "A4" | "A3" | "LEGAL"
  - `copies`: Number
  - `pageRange`: String (e.g. "ALL" or "1-5")
  - `orientation`: "PORTRAIT" | "LANDSCAPE"
- **Response:**
```json
{
  "orderId": "ORD-849201",
  "status": "PENDING",
  "totalAmount": 596.72,
  "upiString": "upi://pay?pa=rpgautoprint@upi&pn=RPG%20Auto%20Print&am=596.72&cu=INR&tn=Print%20ORD-849201",
  "qrPayload": "https://rpg-autoprint.com/pay/ORD-849201"
}
```

---

### 1.3 Verify Payment & Trigger Auto Print
- **Endpoint:** `POST /api/v1/payments/verify`
- **Auth:** HMAC Signature / Razorpay Webhook or UPI Txn reference
- **Request Body:**
```json
{
  "orderId": "ORD-849201",
  "paymentTxnId": "UPI9283749102",
  "paymentMethod": "UPI",
  "signature": "a1b2c3d4..."
}
```
- **Response:**
```json
{
  "success": true,
  "orderStatus": "ACCEPTED",
  "isAutoPrinted": true,
  "invoiceId": "INV-2026-849201",
  "message": "Payment verified. Job auto-dispatched to HP LaserJet M404dn."
}
```

---

## 2. Owner & Desktop Panel Endpoints

### 2.1 Get Live Print Queue
- **Endpoint:** `GET /api/v1/owner/queue`
- **Auth:** Bearer JWT (Role: `ADMIN` or `STAFF`)
- **Query Params:** `status=PENDING,PRINTING`
- **Response:**
```json
[
  {
    "id": "ORD-849201",
    "customerName": "Rohit",
    "fileName": "University_Thesis.pdf",
    "pageCount": 14,
    "copies": 2,
    "colorMode": "COLOR",
    "totalAmount": 596.72,
    "paymentStatus": "PAID",
    "orderStatus": "PRINTING",
    "assignedPrinter": "Epson EcoTank L3250"
  }
]
```

---

### 2.2 Update Order Status / Reprint / Cancel
- **Endpoint:** `PATCH /api/v1/owner/orders/:orderId/status`
- **Auth:** Bearer JWT
- **Request Body:**
```json
{
  "status": "COMPLETED",   // "ACCEPTED" | "PRINTING" | "COMPLETED" | "CANCELLED"
  "printerId": "laser_bw_1",
  "reprint": false
}
```

---

### 2.3 Manage Hardware Printers & Status
- **Endpoint:** `GET /api/v1/owner/printers`
- **Response:**
```json
[
  {
    "id": "laser_bw_1",
    "name": "HP LaserJet Pro M404dn",
    "type": "LASER",
    "connectionType": "NETWORK",
    "ipAddress": "192.168.1.101",
    "status": "ONLINE",
    "paperLevelPercent": 92,
    "inkLevelPercent": 84,
    "queueCount": 1
  }
]
```

---

## 3. Real-Time WebSocket / Event Stream
- **URL:** `wss://rpg-autoprint.com/events`
- **Events:**
  - `ORDER_CREATED`: New customer order arrived.
  - `PAYMENT_RECEIVED`: Payment confirmed; trigger auto-print job.
  - `JOB_PRINTING`: Printer started spooling.
  - `JOB_COMPLETED`: Output tray ready for pickup.
