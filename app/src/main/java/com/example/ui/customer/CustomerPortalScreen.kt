package com.example.ui.customer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Order
import com.example.data.model.Shop
import com.example.ui.components.QrCodeView
import com.example.ui.locale.Strings
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomerPortalScreen(
    shop: Shop,
    language: String,
    currentOrder: Order?,
    isSubmitting: Boolean,
    onPlaceAndPayOrder: (Order) -> Unit,
    onViewInvoice: () -> Unit,
    onNewOrder: () -> Unit
) {
    // If order is active and paid or in progress, show OrderStatusTracker
    if (currentOrder != null && (currentOrder.paymentStatus == "PAID" || currentOrder.orderStatus != "PENDING")) {
        OrderStatusTracker(
            order = currentOrder,
            shop = shop,
            language = language,
            onViewInvoice = onViewInvoice,
            onNewOrder = onNewOrder
        )
        return
    }

    // Customer form state
    var customerName by remember { mutableStateOf("Rohit (Walk-in)") }
    var customerPhone by remember { mutableStateOf("+91 98765 43210") }
    var fileName by remember { mutableStateOf("University_Thesis_Final.pdf") }
    var fileType by remember { mutableStateOf("PDF") }
    var fileSize by remember { mutableStateOf(2450000L) } // 2.45 MB
    var pageCount by remember { mutableIntStateOf(14) }
    var copies by remember { mutableIntStateOf(1) }
    var colorMode by remember { mutableStateOf("BW") } // "BW" or "COLOR"
    var duplexMode by remember { mutableStateOf("SINGLE") } // "SINGLE" or "DOUBLE"
    var paperSize by remember { mutableStateOf("A4") } // "A4", "A3", "LEGAL"
    var pageRange by remember { mutableStateOf("ALL") }
    var orientation by remember { mutableStateOf("PORTRAIT") } // "PORTRAIT" or "LANDSCAPE"
    var scaleMode by remember { mutableStateOf("FIT_TO_PAGE") }
    var printQuality by remember { mutableStateOf("NORMAL") }

    // Payment Modal State
    var showPaymentModal by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("UPI") }

    // Dynamic Price Calculation based on Shop Owner's Settings
    val ratePerPage = if (colorMode == "COLOR") shop.colorPricePerPage else shop.bwPricePerPage
    val sheetFactor = if (duplexMode == "DOUBLE") 0.90 else 1.0 // 10% duplex paper savings
    val subtotal = (pageCount * copies * ratePerPage * sheetFactor)
    val gstAmount = subtotal * (shop.gstPercentage / 100.0)
    val discountAmount = subtotal * (shop.discountPercentage / 100.0)
    val totalAmount = maxOf(shop.minOrderAmount, subtotal + gstAmount + shop.serviceCharge - discountAmount)

    // Document Picker
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = uri.path ?: "Uploaded_Document.pdf"
            fileName = path.substringAfterLast("/").substringAfterLast(":")
            if (!fileName.contains(".")) fileName += ".pdf"
            fileType = fileName.substringAfterLast(".").uppercase()
            pageCount = (1..25).random() // Realistic simulated page counter
            fileSize = 1024L * (150..4500).random()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = shop.shopName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = shop.tagline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF93C5FD)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2563EB))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "LIVE SHOP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PriceBadge("B&W Rate", "₹${shop.bwPricePerPage}/pg", Color(0xFF38BDF8))
                    PriceBadge("Colour Rate", "₹${shop.colorPricePerPage}/pg", Color(0xFFFBBF24))
                    PriceBadge("GST Rate", "${shop.gstPercentage}%", Color(0xFF4ADE80))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1: Document Upload
        SectionHeader("1. ${Strings.get("upload_doc", language)}", Icons.Default.FileUpload)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Upload trigger area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .clickable { docPickerLauncher.launch("*/*") }
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDBEAFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Upload", tint = Color(0xFF2563EB))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Strings.get("select_file", language),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A),
                            fontSize = 14.sp
                        )
                        Text(
                            text = Strings.get("upload_desc", language),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selected File Preview Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2563EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (fileType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                            contentDescription = fileType,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fileName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "$fileType • ${(fileSize / 1024)} KB • $pageCount pages detected",
                            fontSize = 11.sp,
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sample document quick chips
                Text(
                    text = Strings.get("sample_files", language),
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SampleChip("College Notes (12 pgs)") {
                        fileName = "College_Notes_Unit4.pdf"
                        fileType = "PDF"
                        pageCount = 12
                        fileSize = 1840000L
                    }
                    SampleChip("ID Card & Aadhaar (1 pg)") {
                        fileName = "Govt_ID_Card.jpg"
                        fileType = "JPG"
                        pageCount = 1
                        fileSize = 420000L
                    }
                    SampleChip("Resume (2 pgs)") {
                        fileName = "Software_Resume_2026.docx"
                        fileType = "DOCX"
                        pageCount = 2
                        fileSize = 310000L
                    }
                    SampleChip("Tax Invoice (4 pgs)") {
                        fileName = "Company_Invoice_Q3.xlsx"
                        fileType = "XLSX"
                        pageCount = 4
                        fileSize = 890000L
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Document Previewer
                com.example.ui.components.DocumentPreviewer(
                    fileName = fileName,
                    fileType = fileType,
                    pageCount = pageCount,
                    colorMode = colorMode,
                    duplexMode = duplexMode,
                    orientation = orientation
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 2: Print Configurations
        SectionHeader("2. ${Strings.get("print_options", language)}", Icons.Default.Print)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Color Mode: B/W vs Colour
                Text(Strings.get("color_mode", language), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OptionChip(
                        title = "${Strings.get("bw", language)} (₹${shop.bwPricePerPage})",
                        selected = colorMode == "BW",
                        modifier = Modifier.weight(1f)
                    ) { colorMode = "BW" }

                    OptionChip(
                        title = "${Strings.get("colour", language)} (₹${shop.colorPricePerPage})",
                        selected = colorMode == "COLOR",
                        modifier = Modifier.weight(1f)
                    ) { colorMode = "COLOR" }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sides: Single vs Double Side
                Text(Strings.get("sides", language), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OptionChip(
                        title = Strings.get("single_side", language),
                        selected = duplexMode == "SINGLE",
                        modifier = Modifier.weight(1f)
                    ) { duplexMode = "SINGLE" }

                    OptionChip(
                        title = Strings.get("double_side", language),
                        selected = duplexMode == "DOUBLE",
                        modifier = Modifier.weight(1f)
                    ) { duplexMode = "DOUBLE" }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Paper Size
                Text(Strings.get("paper_size", language), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("A4", "A3", "LEGAL").forEach { size ->
                        OptionChip(
                            title = size,
                            selected = paperSize == size,
                            modifier = Modifier.weight(1f)
                        ) { paperSize = size }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Copies & Orientation
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Copies Counter
                    Column(modifier = Modifier.weight(1f)) {
                        Text(Strings.get("copies", language), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (copies > 1) copies-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }

                            Text(
                                text = "$copies",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = { if (copies < 100) copies++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }

                    // Orientation
                    Column(modifier = Modifier.weight(1f)) {
                        Text(Strings.get("orientation", language), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OptionChip(
                                title = "Portrait",
                                selected = orientation == "PORTRAIT",
                                modifier = Modifier.weight(1f)
                            ) { orientation = "PORTRAIT" }

                            OptionChip(
                                title = "Landscape",
                                selected = orientation == "LANDSCAPE",
                                modifier = Modifier.weight(1f)
                            ) { orientation = "LANDSCAPE" }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Page Range & Quality
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = pageRange,
                        onValueChange = { pageRange = it },
                        label = { Text(Strings.get("page_range", language), fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Phone (for SMS/WhatsApp)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 3: Live Price Summary & Checkout
        SectionHeader("3. ${Strings.get("price_summary", language)}", Icons.Default.Payments)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                PriceRow(
                    label = "${pageCount} pages × ${copies} copy @ ₹${ratePerPage}",
                    value = "₹${String.format(Locale.US, "%.2f", subtotal)}"
                )
                PriceRow(
                    label = "GST (${shop.gstPercentage}%)",
                    value = "₹${String.format(Locale.US, "%.2f", gstAmount)}"
                )
                if (shop.serviceCharge > 0) {
                    PriceRow(
                        label = "Service / Cloud Fee",
                        value = "₹${String.format(Locale.US, "%.2f", shop.serviceCharge)}"
                    )
                }
                if (discountAmount > 0) {
                    PriceRow(
                        label = "Discount (${shop.discountPercentage}%)",
                        value = "-₹${String.format(Locale.US, "%.2f", discountAmount)}",
                        highlight = true
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.get("total_amount", language),
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Auto calculated • Protected",
                            fontSize = 10.sp,
                            color = Color(0xFF059669)
                        )
                    }
                    Text(
                        text = "₹${String.format(Locale.US, "%.2f", totalAmount)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E3A8A)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pay Now Button
                Button(
                    onClick = { showPaymentModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = "Pay")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${Strings.get("proceed_pay", language)} • ₹${String.format(Locale.US, "%.2f", totalAmount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Payment Gateway Dialog
    if (showPaymentModal) {
        PaymentGatewayModal(
            shop = shop,
            totalAmount = totalAmount,
            fileName = fileName,
            selectedMethod = selectedPaymentMethod,
            onMethodSelect = { selectedPaymentMethod = it },
            onDismiss = { showPaymentModal = false },
            onConfirmPayment = { txnId ->
                showPaymentModal = false
                val newOrder = Order(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    fileName = fileName,
                    fileType = fileType,
                    fileSize = fileSize,
                    pageCount = pageCount,
                    copies = copies,
                    colorMode = colorMode,
                    duplexMode = duplexMode,
                    paperSize = paperSize,
                    pageRange = pageRange,
                    orientation = orientation,
                    scaleMode = scaleMode,
                    printQuality = printQuality,
                    subtotal = subtotal,
                    gstAmount = gstAmount,
                    serviceCharge = shop.serviceCharge,
                    discountAmount = discountAmount,
                    totalAmount = totalAmount,
                    paymentMethod = selectedPaymentMethod,
                    paymentStatus = "PAID", // Confirmed via secure gateway
                    paymentTxnId = txnId,
                    orderStatus = if (shop.autoPrintEnabled) "PRINTING" else "ACCEPTED",
                    printerAssigned = shop.selectedPrinterId,
                    isAutoPrinted = shop.autoPrintEnabled
                )
                onPlaceAndPayOrder(newOrder)
            }
        )
    }
}

@Composable
private fun PaymentGatewayModal(
    shop: Shop,
    totalAmount: Double,
    fileName: String,
    selectedMethod: String,
    onMethodSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirmPayment: (String) -> Unit
) {
    val upiString = "upi://pay?pa=${shop.upiId}&pn=${Uri.encode(shop.shopName)}&am=${String.format(Locale.US, "%.2f", totalAmount)}&cu=INR&tn=${Uri.encode("Print $fileName")}"
    var isVerifying by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RPG SECURE PAYMENT GATEWAY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E3A8A),
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Amount Payable: ₹${String.format(Locale.US, "%.2f", totalAmount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Methods Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PaymentTab("UPI / QR", selectedMethod == "UPI") { onMethodSelect("UPI") }
                    PaymentTab("GPay", selectedMethod == "GPAY") { onMethodSelect("GPAY") }
                    PaymentTab("PhonePe", selectedMethod == "PHONEPE") { onMethodSelect("PHONEPE") }
                    PaymentTab("Cash", selectedMethod == "CASH") { onMethodSelect("CASH") }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedMethod in listOf("UPI", "GPAY", "PHONEPE", "PAYTM")) {
                    Text(
                        text = "Scan UPI QR Code to Pay",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Live UPI QR Code
                    QrCodeView(
                        data = upiString,
                        size = 180.dp,
                        centerBadgeText = "UPI"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "UPI ID: ${shop.upiId}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF2563EB)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Please hand over ₹${String.format(Locale.US, "%.2f", totalAmount)} in cash to the shop operator at the counter. The operator will verify and approve the print.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm / Verify Button
                Button(
                    onClick = {
                        isVerifying = true
                        val mockTxnId = "TXN" + System.currentTimeMillis().toString().takeLast(8)
                        onConfirmPayment(mockTxnId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Confirm", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedMethod == "CASH") "Confirm Cash Order" else "I Have Paid • Verify & Print",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun PaymentTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color(0xFF1E3A8A) else Color(0xFFF1F5F9))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else Color(0xFF475569)
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = title, tint = Color(0xFF1E3A8A), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
}

@Composable
private fun PriceBadge(label: String, value: String, accent: Color) {
    Column {
        Text(label, fontSize = 10.sp, color = Color(0xFFCBD5E1))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accent)
    }
}

@Composable
private fun OptionChip(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (selected) Color(0xFFEFF6FF) else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color(0xFF1E3A8A) else Color(0xFF334155),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SampleChip(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE2E8F0))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = title, fontSize = 10.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PriceRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) Color(0xFF059669) else Color(0xFF1E293B)
        )
    }
}
