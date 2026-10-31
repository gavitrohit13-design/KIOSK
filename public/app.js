// RPG AUTO PRINT — Client Web Portal & Kiosk Controller

const state = {
  fileName: "University_Project_Report.pdf",
  fileType: "PDF",
  fileSizeKb: 2450,
  pageCount: 14,
  currentPage: 1,
  zoomLevel: 1.0,
  rotation: 0,
  colorMode: "BW",
  duplexMode: "SINGLE",
  paperSize: "A4",
  copies: 1,
  pageRange: "ALL",
  bwRate: 10.0,
  colorRate: 20.0,
  gstPercentage: 18.0,
  serviceCharge: 2.0,
  subtotal: 140.0,
  gstAmount: 25.20,
  totalAmount: 167.20,
  autoPrintEnabled: true,
  currentOrderId: "ORD-" + Math.floor(100000 + Math.random() * 900000),
  lang: "en"
};

const i18n = {
  en: {
    title: "RPG AUTO PRINT",
    tagline: "Scan • Upload • Pay • Print",
    upload: "Upload Document",
    config: "Print Configurations",
    price: "Live Cost Breakdown",
    bw: "Black & White",
    colour: "Full Colour",
    proceed: "Proceed to Pay"
  },
  hi: {
    title: "आरपीजी ऑटो प्रिंट",
    tagline: "स्कैन • अपलोड • पे • प्रिंट",
    upload: "दस्तावेज़ अपलोड करें",
    config: "प्रिंट विकल्प",
    price: "कीमत का विवरण",
    bw: "ब्लैक एंड व्हाइट",
    colour: "रंगीन (कलर)",
    proceed: "भुगतान के लिए आगे बढ़ें"
  },
  mr: {
    title: "आरपीजी ऑटो प्रिंट",
    tagline: "स्कॅन • अपलोड • पे • प्रिंट",
    upload: "दस्तऐवज अपलोड करा",
    config: "प्रिंट पर्याय",
    price: "दरांचे विवरण",
    bw: "ब्लॅक अँड व्हाईट",
    colour: "रंगीत (कलर)",
    proceed: "पेमेंटसाठी पुढे जा"
  }
};

function changeLanguage(lang) {
  state.lang = lang;
  const dict = i18n[lang] || i18n.en;
  document.getElementById("txtAppTitle").textContent = dict.title;
  document.getElementById("txtAppTagline").textContent = dict.tagline;
  document.getElementById("txtStepUpload").textContent = dict.upload;
  document.getElementById("txtStepConfig").textContent = dict.config;
  document.getElementById("txtStepPrice").textContent = dict.price;
  calculatePrice();
}

// File Selection Handler
function handleFileSelected(input) {
  if (input.files && input.files[0]) {
    const file = input.files[0];
    state.fileName = file.name;
    state.fileType = file.name.split('.').pop().toUpperCase();
    state.fileSizeKb = Math.round(file.size / 1024);
    state.pageCount = Math.floor(Math.random() * 18) + 1; // Simulated page detector
    state.currentPage = 1;

    document.getElementById("displayFileName").textContent = state.fileName;
    document.getElementById("displayFileMeta").textContent = `${state.fileType} • ${state.pageCount} Pages detected • ${state.fileSizeKb} KB`;
    document.getElementById("previewPageIndicator").textContent = `Page 1 of ${state.pageCount}`;
    calculatePrice();
  }
}

// Print Configuration Setters
function setColorMode(mode) {
  state.colorMode = mode;
  document.getElementById("btnBw").classList.toggle("active", mode === "BW");
  document.getElementById("btnColor").classList.toggle("active", mode === "COLOR");
  document.getElementById("previewSheet").classList.toggle("color-mode", mode === "COLOR");
  calculatePrice();
}

function setDuplex(mode) {
  state.duplexMode = mode;
  document.getElementById("btnSingleSide").classList.toggle("active", mode === "SINGLE");
  document.getElementById("btnDoubleSide").classList.toggle("active", mode === "DOUBLE");
  calculatePrice();
}

function setPaperSize(size) {
  state.paperSize = size;
  document.getElementById("btnA4").classList.toggle("active", size === "A4");
  document.getElementById("btnA3").classList.toggle("active", size === "A3");
  document.getElementById("btnLegal").classList.toggle("active", size === "LEGAL");
  calculatePrice();
}

function adjustCopies(delta) {
  state.copies = Math.max(1, state.copies + delta);
  document.getElementById("copiesDisplay").textContent = state.copies;
  calculatePrice();
}

// Dynamic Price Calculation
function calculatePrice() {
  const rate = state.colorMode === "COLOR" ? state.colorRate : state.bwRate;
  const sheetFactor = state.duplexMode === "DOUBLE" ? 0.90 : 1.0;
  
  state.subtotal = state.pageCount * state.copies * rate * sheetFactor;
  state.gstAmount = state.subtotal * (state.gstPercentage / 100.0);
  state.totalAmount = Math.max(10.0, state.subtotal + state.gstAmount + state.serviceCharge);

  document.getElementById("txtCalcPages").textContent = `${state.pageCount} pages × ${state.copies} copy @ ₹${rate}`;
  document.getElementById("txtSubtotal").textContent = `₹${state.subtotal.toFixed(2)}`;
  document.getElementById("txtGst").textContent = `₹${state.gstAmount.toFixed(2)}`;
  document.getElementById("txtGrandTotal").textContent = `₹${state.totalAmount.toFixed(2)}`;
  document.getElementById("btnPayText").textContent = `Proceed to Pay • ₹${state.totalAmount.toFixed(2)}`;
}

// Interactive Document Preview Controls
function adjustZoom(delta) {
  state.zoomLevel = Math.min(1.4, Math.max(0.7, state.zoomLevel + delta));
  document.getElementById("zoomDisplay").textContent = `${Math.round(state.zoomLevel * 100)}%`;
  updatePreviewTransform();
}

function rotatePreview() {
  state.rotation = (state.rotation + 90) % 360;
  updatePreviewTransform();
}

function updatePreviewTransform() {
  const sheet = document.getElementById("previewSheet");
  sheet.style.transform = `scale(${state.zoomLevel}) rotate(${state.rotation}deg)`;
}

function stepPreviewPage(delta) {
  state.currentPage = Math.max(1, Math.min(state.pageCount, state.currentPage + delta));
  document.getElementById("previewPageIndicator").textContent = `Page ${state.currentPage} of ${state.pageCount}`;
  document.getElementById("sheetPageStamp").textContent = state.currentPage;
}

// Modal Handlers
function openPaymentModal() {
  const upiString = `upi://pay?pa=rpgautoprint@upi&pn=RPG%20Auto%20Print&am=${state.totalAmount.toFixed(2)}&cu=INR&tn=Print%20${state.currentOrderId}`;
  document.getElementById("qrUpiStringDisplay").textContent = `UPI: rpgautoprint@upi (${state.currentOrderId})`;
  document.getElementById("qrPayAmount").textContent = `Pay: ₹${state.totalAmount.toFixed(2)}`;
  drawQrCode(upiString);
  document.getElementById("paymentModal").classList.add("active");
}

function closeModal(id) {
  document.getElementById(id).classList.remove("active");
}

// Canvas QR Code Generator
function drawQrCode(text) {
  const canvas = document.getElementById("qrCanvas");
  if (!canvas) return;
  const ctx = canvas.getContext("2d");
  const size = canvas.width;
  const n = 25;
  const cellSize = size / n;

  ctx.fillStyle = "#FFFFFF";
  ctx.fillRect(0, 0, size, size);

  function drawFinder(startR, startC) {
    ctx.fillStyle = "#0F172A";
    for (let r = 0; r < 7; r++) {
      for (let c = 0; c < 7; c++) {
        if (r === 0 || r === 6 || c === 0 || c === 6 || (r >= 2 && r <= 4 && c >= 2 && c <= 4)) {
          ctx.fillRect((startC + c) * cellSize, (startR + r) * cellSize, cellSize, cellSize);
        }
      }
    }
  }

  drawFinder(0, 0);
  drawFinder(0, n - 7);
  drawFinder(n - 7, 0);

  // Timing patterns
  for (let i = 8; i < n - 8; i++) {
    if (i % 2 === 0) {
      ctx.fillRect(i * cellSize, 6 * cellSize, cellSize, cellSize);
      ctx.fillRect(6 * cellSize, i * cellSize, cellSize, cellSize);
    }
  }

  // Data modules simulation based on text hash
  let hash = 0;
  for (let i = 0; i < text.length; i++) hash = (hash << 5) - hash + text.charCodeAt(i);

  ctx.fillStyle = "#1E3A8A";
  for (let r = 0; r < n; r++) {
    for (let c = 0; c < n; c++) {
      const inFinder = (r < 8 && c < 8) || (r < 8 && c >= n - 8) || (r >= n - 8 && c < 8);
      const inTiming = r === 6 || c === 6;
      if (!inFinder && !inTiming) {
        if ((r * c + hash) % 3 === 0) {
          ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
        }
      }
    }
  }

  // Central RPG Badge
  ctx.fillStyle = "#2563EB";
  ctx.fillRect(10 * cellSize, 10 * cellSize, 5 * cellSize, 5 * cellSize);
  ctx.fillStyle = "#FFFFFF";
  ctx.font = "bold 10px sans-serif";
  ctx.textAlign = "center";
  ctx.textBaseline = "middle";
  ctx.fillText("RPG", 12.5 * cellSize, 12.5 * cellSize);
}

// Payment Confirmation & Order Flow
function confirmPaymentAndPrint() {
  closeModal("paymentModal");
  document.getElementById("uploadCard").style.display = "none";
  document.getElementById("configCard").style.display = "none";
  document.getElementById("priceCard").style.display = "none";
  document.getElementById("orderTrackerSection").style.display = "block";
  document.getElementById("trackerOrderId").textContent = state.currentOrderId;

  // Add to owner live queue
  addOrderToOwnerQueue({
    id: state.currentOrderId,
    file: state.fileName,
    pages: state.pageCount,
    copies: state.copies,
    amount: state.totalAmount.toFixed(2),
    status: "PRINTING"
  });

  // Auto-Print Stepper Simulation
  setTimeout(() => {
    document.getElementById("step2").classList.add("done");
    document.getElementById("step3").classList.add("active");
  }, 1500);

  setTimeout(() => {
    document.getElementById("step3").classList.remove("active");
    document.getElementById("step3").classList.add("done");
    document.getElementById("step4").classList.add("done");
    document.getElementById("step4").classList.add("active");
  }, 4000);
}

function resetForNewOrder() {
  state.currentOrderId = "ORD-" + Math.floor(100000 + Math.random() * 900000);
  document.getElementById("uploadCard").style.display = "block";
  document.getElementById("configCard").style.display = "block";
  document.getElementById("priceCard").style.display = "block";
  document.getElementById("orderTrackerSection").style.display = "none";
  document.getElementById("step2").className = "step-item active";
  document.getElementById("step3").className = "step-item";
  document.getElementById("step4").className = "step-item";
}

function openInvoiceModal() {
  document.getElementById("invOrderId").textContent = state.currentOrderId;
  document.getElementById("invNumber").textContent = "INV-2026-" + state.currentOrderId.split('-')[1];
  document.getElementById("invItemDesc").textContent = `${state.fileName} (${state.pageCount} pgs ${state.colorMode} ${state.duplexMode})`;
  document.getElementById("invQty").textContent = `${state.copies} copy`;
  document.getElementById("invSubtotal").textContent = `₹${state.subtotal.toFixed(2)}`;
  document.getElementById("invGst").textContent = `₹${state.gstAmount.toFixed(2)}`;
  document.getElementById("invGrandTotal").textContent = `₹${state.totalAmount.toFixed(2)}`;
  document.getElementById("invoiceModal").classList.add("active");
}

// Owner PC Terminal Functions
function openOwnerPanel() {
  document.getElementById("ownerModal").classList.add("active");
}

function toggleAutoPrintMaster(enabled) {
  state.autoPrintEnabled = enabled;
  console.log("Auto-Print Master Engine is now:", enabled ? "ENABLED" : "DISABLED");
}

function addOrderToOwnerQueue(job) {
  const queueEl = document.getElementById("ownerQueueList");
  if (!queueEl) return;
  const item = document.createElement("div");
  item.style.background = "#F1F5F9";
  item.style.padding = "10px 14px";
  item.style.borderRadius = "8px";
  item.style.display = "flex";
  item.style.justifyContent = "space-between";
  item.style.alignItems = "center";
  item.innerHTML = `
    <div>
      <strong style="font-size: 13px; color: var(--primary);">${job.id} • ${job.file}</strong>
      <p style="font-size: 11px; color: var(--text-muted);">${job.pages} pgs • ${job.copies} copy • ₹${job.amount} (PAID)</p>
    </div>
    <div style="display: flex; gap: 6px;">
      <button style="background: #1E3A8A; color: white; border: none; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; cursor: pointer;" onclick="alert('Spooling ${job.id} to HP LaserJet M404dn...')">Print</button>
      <button style="background: #059669; color: white; border: none; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; cursor: pointer;" onclick="this.parentElement.parentElement.remove()">Done</button>
    </div>
  `;
  queueEl.prepend(item);
}

// Initialize on page load
window.addEventListener("DOMContentLoaded", () => {
  calculatePrice();
  // Seed sample job into owner queue
  addOrderToOwnerQueue({
    id: "ORD-918234",
    file: "College_Notes_Final.pdf",
    pages: 8,
    copies: 1,
    amount: "96.40",
    status: "QUEUED"
  });
});
