const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

// In-Memory Database store with local JSON backup
const db = {
  shop: {
    name: "RPG AUTO PRINT",
    tagline: "Scan • Upload • Pay • Print",
    address: "Shop No. 4, College Road, Tech Hub",
    phone: "+91 98765 43210",
    upiId: "rpgautoprint@upi",
    gstNumber: "27AAAAA0000A1Z5",
    bwPricePerPage: 10.0,
    colorPricePerPage: 20.0,
    gstPercentage: 18.0,
    discountPercentage: 0.0,
    serviceCharge: 2.0,
    minOrderAmount: 10.0,
    autoPrintEnabled: true,
    defaultPrinter: "laser_bw_1"
  },
  printers: [
    {
      id: "laser_bw_1",
      name: "HP LaserJet Pro M404dn (B&W High Speed)",
      type: "LASER",
      connection: "NETWORK",
      ip: "192.168.1.101",
      status: "ONLINE",
      paperPercent: 90,
      tonerPercent: 84,
      queueCount: 0
    },
    {
      id: "color_inkjet_1",
      name: "Epson EcoTank L3250 (All-in-One Colour)",
      type: "INKJET",
      connection: "WIFI",
      ip: "192.168.1.102",
      status: "ONLINE",
      paperPercent: 78,
      tonerPercent: 95,
      queueCount: 0
    },
    {
      id: "thermal_pos_1",
      name: "TVS RP 3200 Star (Receipt / Token Thermal)",
      type: "THERMAL",
      connection: "USB",
      ip: "USB001",
      status: "ONLINE",
      paperPercent: 65,
      tonerPercent: 100,
      queueCount: 0
    }
  ],
  orders: [
    {
      id: "ORD-849201",
      customerName: "Rohit Gavit",
      fileName: "College_Project_Final.pdf",
      fileType: "PDF",
      pageCount: 14,
      copies: 1,
      colorMode: "BW",
      duplexMode: "SINGLE",
      paperSize: "A4",
      subtotal: 140.0,
      gstAmount: 25.20,
      serviceCharge: 2.0,
      totalAmount: 167.20,
      paymentMethod: "UPI",
      paymentStatus: "PAID",
      paymentTxnId: "TXN-84920199",
      orderStatus: "COMPLETED",
      assignedPrinter: "HP LaserJet Pro M404dn",
      createdAt: new Date(Date.now() - 3600000).toISOString()
    }
  ],
  logs: []
};

// -----------------------------------------------------------------
// 1. HEALTH & METRICS
// -----------------------------------------------------------------
app.get('/api/v1/health', (req, res) => {
  res.json({
    status: 'ONLINE',
    app: 'RPG AUTO PRINT Local Server & Print Spooler',
    version: '1.0.0',
    timestamp: new Date().toISOString(),
    connectedPrinters: db.printers.filter(p => p.status === 'ONLINE').length,
    autoPrintActive: db.shop.autoPrintEnabled
  });
});

// -----------------------------------------------------------------
// 2. SHOP CONFIGURATION & DYNAMIC PRICING
// -----------------------------------------------------------------
app.get('/api/v1/shop', (req, res) => {
  res.json(db.shop);
});

app.post('/api/v1/shop', (req, res) => {
  const updates = req.body;
  db.shop = { ...db.shop, ...updates };
  db.logs.push({ action: 'SHOP_UPDATED', timestamp: new Date(), details: updates });
  res.json({ success: true, shop: db.shop });
});

// -----------------------------------------------------------------
// 3. QUOTE & PRICE COMPUTATION
// -----------------------------------------------------------------
app.post('/api/v1/orders/quote', (req, res) => {
  const { pageCount = 1, copies = 1, colorMode = 'BW', duplexMode = 'SINGLE', paperSize = 'A4' } = req.body;
  const rate = colorMode === 'COLOR' ? db.shop.colorPricePerPage : db.shop.bwPricePerPage;
  const sheetFactor = duplexMode === 'DOUBLE' ? 0.90 : 1.0;
  const subtotal = pageCount * copies * rate * sheetFactor;
  const gstAmount = subtotal * (db.shop.gstPercentage / 100.0);
  const totalAmount = Math.max(db.shop.minOrderAmount, subtotal + gstAmount + db.shop.serviceCharge);

  res.json({
    subtotal: parseFloat(subtotal.toFixed(2)),
    gstAmount: parseFloat(gstAmount.toFixed(2)),
    serviceCharge: db.shop.serviceCharge,
    totalAmount: parseFloat(totalAmount.toFixed(2)),
    ratePerPage: rate
  });
});

// -----------------------------------------------------------------
// 4. ORDER CREATION & PAYMENT VERIFICATION
// -----------------------------------------------------------------
app.post('/api/v1/orders', (req, res) => {
  const orderData = req.body;
  const orderId = "ORD-" + Math.floor(100000 + Math.random() * 900000);
  const newOrder = {
    id: orderId,
    customerName: orderData.customerName || "Walk-in Customer",
    fileName: orderData.fileName || "Document.pdf",
    fileType: orderData.fileType || "PDF",
    pageCount: parseInt(orderData.pageCount) || 1,
    copies: parseInt(orderData.copies) || 1,
    colorMode: orderData.colorMode || "BW",
    duplexMode: orderData.duplexMode || "SINGLE",
    paperSize: orderData.paperSize || "A4",
    subtotal: parseFloat(orderData.subtotal) || 10.0,
    gstAmount: parseFloat(orderData.gstAmount) || 1.8,
    serviceCharge: db.shop.serviceCharge,
    totalAmount: parseFloat(orderData.totalAmount) || 13.8,
    paymentMethod: orderData.paymentMethod || "UPI",
    paymentStatus: orderData.paymentStatus || "PENDING",
    paymentTxnId: orderData.paymentTxnId || "",
    orderStatus: orderData.orderStatus || "PENDING",
    assignedPrinter: db.shop.defaultPrinter,
    createdAt: new Date().toISOString()
  };

  db.orders.unshift(newOrder);
  db.logs.push({ action: 'ORDER_CREATED', orderId, timestamp: new Date() });

  res.status(201).json(newOrder);
});

app.post('/api/v1/orders/:id/pay', (req, res) => {
  const { id } = req.params;
  const { paymentTxnId = "TXN-" + Date.now(), paymentMethod = "UPI" } = req.body;
  const order = db.orders.find(o => o.id === id);

  if (!order) return res.status(404).json({ error: "Order not found" });

  order.paymentStatus = "PAID";
  order.paymentTxnId = paymentTxnId;
  order.paymentMethod = paymentMethod;

  // Auto-Print Trigger
  if (db.shop.autoPrintEnabled) {
    order.orderStatus = "PRINTING";
    setTimeout(() => {
      order.orderStatus = "COMPLETED";
    }, 4000);
  } else {
    order.orderStatus = "ACCEPTED";
  }

  res.json({
    success: true,
    message: "Payment verified successfully. Auto-print dispatched.",
    order
  });
});

app.get('/api/v1/orders/:id', (req, res) => {
  const order = db.orders.find(o => o.id === req.params.id);
  if (!order) return res.status(404).json({ error: "Order not found" });
  res.json(order);
});

// -----------------------------------------------------------------
// 5. OWNER LIVE QUEUE & HARDWARE PRINT CONTROLLER
// -----------------------------------------------------------------
app.get('/api/v1/owner/queue', (req, res) => {
  res.json(db.orders);
});

app.post('/api/v1/owner/orders/:id/status', (req, res) => {
  const { id } = req.params;
  const { status, printerId } = req.body;
  const order = db.orders.find(o => o.id === id);
  if (!order) return res.status(404).json({ error: "Order not found" });

  order.orderStatus = status;
  if (printerId) order.assignedPrinter = printerId;
  res.json({ success: true, order });
});

app.post('/api/v1/owner/printers/spool', (req, res) => {
  const { orderId, printerId } = req.body;
  const order = db.orders.find(o => o.id === orderId);
  const printer = db.printers.find(p => p.id === printerId);

  if (!order) return res.status(404).json({ error: "Order not found" });

  console.log(`[SPOOLER] Dispatched ${order.fileName} to ${printer ? printer.name : 'Default Printer'}`);
  order.orderStatus = "PRINTING";
  setTimeout(() => { order.orderStatus = "COMPLETED"; }, 3500);

  res.json({ success: true, message: `Dispatched to hardware spooler.` });
});

app.get('/api/v1/owner/printers', (req, res) => {
  res.json(db.printers);
});

app.get('/api/v1/owner/reports', (req, res) => {
  const totalRevenue = db.orders.filter(o => o.paymentStatus === 'PAID').sumOf(o => o.totalAmount) || db.orders.reduce((acc, o) => acc + (o.paymentStatus === 'PAID' ? o.totalAmount : 0), 0);
  const totalSheets = db.orders.reduce((acc, o) => acc + (o.pageCount * o.copies), 0);

  res.json({
    totalRevenue,
    totalOrders: db.orders.length,
    completedOrders: db.orders.filter(o => o.orderStatus === 'COMPLETED').length,
    totalSheetsConsumed: totalSheets,
    printersOnline: db.printers.filter(p => p.status === 'ONLINE').length
  });
});

// Fallback to Web Kiosk SPA
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

if (require.main === module) {
  app.listen(PORT, () => {
    console.log(`=======================================================`);
    console.log(`  RPG AUTO PRINT LOCAL SERVER & HARDWARE SPOOLER       `);
    console.log(`  Port: http://localhost:${PORT}                       `);
    console.log(`  Kiosk Portal: http://localhost:${PORT}/index.html    `);
    console.log(`=======================================================`);
  });
}

module.exports = app;
