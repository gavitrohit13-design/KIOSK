package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Order
import com.example.data.model.Shop
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceDialog(
    order: Order,
    shop: Shop,
    onDismiss: () -> Unit,
    onTriggerPrint: (Order) -> Unit
) {
    val context = LocalContext.current
    val dateStr = rememberFormattedDate(order.createdAt?.seconds)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TAX INVOICE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = shop.shopName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "${shop.address} • Phone: ${shop.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "GSTIN: ${shop.gstNumber} | UPI: ${shop.upiId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                // Invoice Meta Info
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Invoice No:", fontSize = 11.sp, color = Color.Gray)
                        Text(order.id, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Date & Time:", fontSize = 11.sp, color = Color.Gray)
                        Text(dateStr, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Customer:", fontSize = 11.sp, color = Color.Gray)
                        Text(order.customerName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Payment:", fontSize = 11.sp, color = Color.Gray)
                        Text("${order.paymentStatus} (${order.paymentMethod})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (order.paymentStatus == "PAID") Color(0xFF059669) else Color(0xFFDC2626))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                // Itemized table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Item / Job Description", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("Qty", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                    Text("Amount (₹)", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Item Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(2f)) {
                        Text(order.fileName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${order.colorMode} • ${order.duplexMode} • ${order.paperSize} (${order.pageCount} pgs)",
                            fontSize = 10.sp,
                            color = Color.DarkGray
                        )
                    }
                    Text("${order.copies} copy", fontSize = 11.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                    Text("₹${String.format(Locale.US, "%.2f", order.subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                // Financial Breakdown
                InvoiceRow("Subtotal", "₹${String.format(Locale.US, "%.2f", order.subtotal)}")
                InvoiceRow("GST (${shop.gstPercentage}%)", "₹${String.format(Locale.US, "%.2f", order.gstAmount)}")
                if (order.serviceCharge > 0) {
                    InvoiceRow("Convenience / Service Fee", "₹${String.format(Locale.US, "%.2f", order.serviceCharge)}")
                }
                if (order.discountAmount > 0) {
                    InvoiceRow("Promotional Discount", "-₹${String.format(Locale.US, "%.2f", order.discountAmount)}", Color(0xFF059669))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF94A3B8))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("GRAND TOTAL", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                    Text("₹${String.format(Locale.US, "%.2f", order.totalAmount)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E3A8A))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons (Print / Share)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            shareInvoiceText(context, order, shop)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onTriggerPrint(order) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(label: String, value: String, valueColor: Color = Color(0xFF334155)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

private fun rememberFormattedDate(seconds: Long?): String {
    if (seconds == null || seconds <= 0) return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
    return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(seconds * 1000))
}

private fun shareInvoiceText(context: Context, order: Order, shop: Shop) {
    val text = """
        🧾 INVOICE - ${shop.shopName}
        Order ID: ${order.id}
        Date: ${SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US).format(Date())}
        Customer: ${order.customerName}
        ------------------------------
        File: ${order.fileName}
        Config: ${order.colorMode} | ${order.duplexMode} | ${order.paperSize}
        Pages: ${order.pageCount} | Copies: ${order.copies}
        ------------------------------
        Subtotal: ₹${order.subtotal}
        GST: ₹${order.gstAmount}
        Grand Total: ₹${order.totalAmount}
        Payment: ${order.paymentStatus} (${order.paymentMethod})
        Txn ID: ${order.paymentTxnId.ifBlank { "N/A" }}
        Status: ${order.orderStatus}
        ------------------------------
        Thank you for printing with ${shop.shopName}!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Print Invoice - ${order.id}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Invoice via"))
}
