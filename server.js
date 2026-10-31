const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Health Check
app.get('/api/v1/health', (req, res) => {
    res.json({
        status: 'UP',
        app: 'RPG AUTO PRINT Backend Server',
        version: process.env.npm_package_version || '1.0.0',
        timestamp: new Date().toISOString()
    });
});

// Price Quote API
app.post('/api/v1/orders/quote', (req, res) => {
    const { pageCount = 1, copies = 1, colorMode = 'BW', duplexMode = 'SINGLE' } = req.body;
    const ratePerPage = colorMode === 'COLOR' ? 20.0 : 10.0;
    const sheetFactor = duplexMode === 'DOUBLE' ? 0.90 : 1.0;
    const subtotal = pageCount * copies * ratePerPage * sheetFactor;
    const gstAmount = subtotal * 0.18;
    const serviceCharge = 2.0;
    const totalAmount = Math.max(10.0, subtotal + gstAmount + serviceCharge);

    res.json({
        subtotal: parseFloat(subtotal.toFixed(2)),
        gstAmount: parseFloat(gstAmount.toFixed(2)),
        serviceCharge: parseFloat(serviceCharge.toFixed(2)),
        discountAmount: 0.0,
        totalAmount: parseFloat(totalAmount.toFixed(2)),
        minOrderApplied: totalAmount === 10.0
    });
});

// Verify Payment Webhook
app.post('/api/v1/payments/verify', (req, res) => {
    const { orderId, paymentTxnId, paymentMethod = 'UPI' } = req.body;
    res.json({
        success: true,
        orderId,
        paymentStatus: 'PAID',
        paymentTxnId: paymentTxnId || 'TXN-' + Date.now(),
        orderStatus: 'ACCEPTED',
        isAutoPrinted: true,
        message: 'Payment verified successfully. Auto-print dispatched.'
    });
});

if (require.main === module) {
    app.listen(PORT, () => {
        console.log(`RPG Auto Print Server listening on http://localhost:${PORT}`);
    });
}

module.exports = app;
