package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Printer
import com.example.data.model.Shop
import com.example.ui.locale.Strings
import java.util.Locale

@Composable
fun OwnerDashboardScreen(
    shop: Shop,
    orders: List<Order>,
    printers: List<Printer>,
    language: String,
    onUpdateOrderStatus: (String, String, String?) -> Unit,
    onToggleAutoPrint: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPrinters: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenShopQr: () -> Unit,
    onViewInvoice: (Order) -> Unit,
    onTriggerPrint: (Order) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var isQueuePaused by remember { mutableStateOf(false) }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "PENDING" -> orders.filter { it.orderStatus == "PENDING" || it.orderStatus == "ACCEPTED" }
            "PRINTING" -> orders.filter { it.orderStatus == "PRINTING" }
            "COMPLETED" -> orders.filter { it.orderStatus == "COMPLETED" }
            "CANCELLED" -> orders.filter { it.orderStatus == "CANCELLED" }
            else -> orders
        }
    }

    val totalRevenue = orders.filter { it.paymentStatus == "PAID" }.sumOf { it.totalAmount }
    val printingCount = orders.count { it.orderStatus == "PRINTING" }
    val pendingCount = orders.count { it.orderStatus == "PENDING" || it.orderStatus == "ACCEPTED" }
    val completedCount = orders.count { it.orderStatus == "COMPLETED" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(16.dp)
    ) {
        // Quick Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DashboardActionButton("Pricing", Icons.Default.Settings, onOpenSettings, Modifier.weight(1f))
            DashboardActionButton("Printers", Icons.Default.Print, onOpenPrinters, Modifier.weight(1f))
            DashboardActionButton("Reports", Icons.Default.Assessment, onOpenReports, Modifier.weight(1f))
            DashboardActionButton("Shop QR", Icons.Default.QrCode, onOpenShopQr, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Auto Print Master Engine Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (shop.autoPrintEnabled && !isQueuePaused) Color(0xFF0F172A) else Color(0xFF334155)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (shop.autoPrintEnabled && !isQueuePaused) Color(0xFF10B981) else Color(0xFF64748B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Auto Print",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Auto Print Engine",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (shop.autoPrintEnabled && !isQueuePaused) Color(0xFF059669) else Color(0xFF64748B))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (shop.autoPrintEnabled && !isQueuePaused) "ACTIVE" else "MANUAL",
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Text(
                            text = if (isQueuePaused) "Queue is PAUSED" else "Automatically sends paid jobs to printer",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isQueuePaused = !isQueuePaused },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = if (isQueuePaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause / Resume",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = shop.autoPrintEnabled,
                        onCheckedChange = onToggleAutoPrint,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mini KPI Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiCard("Today's Revenue", "₹${String.format(Locale.US, "%.0f", totalRevenue)}", Color(0xFF1E3A8A), Modifier.weight(1f))
            KpiCard("Printing Queue", "$printingCount", Color(0xFF2563EB), Modifier.weight(1f))
            KpiCard("Pending", "$pendingCount", Color(0xFFD97706), Modifier.weight(1f))
            KpiCard("Completed", "$completedCount", Color(0xFF059669), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL" to "All", "PENDING" to "Queue", "PRINTING" to "Printing", "COMPLETED" to "Done").forEach { (f, label) ->
                FilterTab(label, selected = selectedFilter == f, modifier = Modifier.weight(1f)) {
                    selectedFilter = f
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live Order Queue List
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Print, contentDescription = "Empty", tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No orders in this queue", color = Color(0xFF64748B), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredOrders) { order ->
                    OwnerOrderCard(
                        order = order,
                        onUpdateStatus = { status -> onUpdateOrderStatus(order.id, status, shop.selectedPrinterId) },
                        onViewInvoice = { onViewInvoice(order) },
                        onTriggerPrint = { onTriggerPrint(order) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OwnerOrderCard(
    order: Order,
    onUpdateStatus: (String) -> Unit,
    onViewInvoice: () -> Unit,
    onTriggerPrint: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.id,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF1E3A8A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${order.customerName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (order.orderStatus) {
                                "COMPLETED" -> Color(0xFFDCFCE7)
                                "PRINTING" -> Color(0xFFDBEAFE)
                                "CANCELLED" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFFEF3C7)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = order.orderStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.orderStatus) {
                            "COMPLETED" -> Color(0xFF166534)
                            "PRINTING" -> Color(0xFF1E40AF)
                            "CANCELLED" -> Color(0xFF991B1B)
                            else -> Color(0xFF92400E)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // File & Config Info
            Text(
                text = order.fileName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            Text(
                text = "${order.colorMode} • ${order.duplexMode} • ${order.paperSize} • ${order.pageCount} pgs × ${order.copies} copy",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pricing & Payment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ₹${String.format(Locale.US, "%.2f", order.totalAmount)} (${order.paymentStatus})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (order.paymentStatus == "PAID") Color(0xFF059669) else Color(0xFFD97706)
                )

                Text(
                    text = "Printer: ${order.printerAssigned}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Print / Spool Button
                Button(
                    onClick = {
                        onTriggerPrint()
                        onUpdateStatus("PRINTING")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Print", fontSize = 11.sp)
                }

                // Complete Button
                Button(
                    onClick = { onUpdateStatus("COMPLETED") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Done", fontSize = 11.sp)
                }

                // Invoice Button
                OutlinedButton(
                    onClick = onViewInvoice,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = "Invoice", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invoice", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun DashboardActionButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, tint = Color(0xFF1E3A8A), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
        }
    }
}

@Composable
private fun KpiCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 9.sp, color = Color(0xFF64748B), maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
private fun FilterTab(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color(0xFF1E3A8A) else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else Color(0xFF475569)
        )
    }
}
