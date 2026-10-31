package com.example.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Order
import java.util.Locale

@Composable
fun ReportsDialog(
    orders: List<Order>,
    onDismiss: () -> Unit
) {
    val totalRevenue = orders.filter { it.paymentStatus == "PAID" }.sumOf { it.totalAmount }
    val totalPages = orders.sumOf { it.pageCount * it.copies }
    val completedOrders = orders.count { it.orderStatus == "COMPLETED" }
    val bwOrders = orders.count { it.colorMode == "BW" }
    val colorOrders = orders.count { it.colorMode == "COLOR" }

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
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRINT SHOP ANALYTICS & REPORTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "Daily, Monthly, Paper & Device Telemetry",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Stats Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBox(
                        title = "Total Revenue",
                        value = "₹${String.format(Locale.US, "%.0f", totalRevenue)}",
                        subtitle = "From ${orders.size} orders",
                        color = Color(0xFF1E3A8A),
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Pages Consumed",
                        value = "$totalPages sheets",
                        subtitle = "Paper inventory",
                        color = Color(0xFF059669),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Service Breakdown", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                Spacer(modifier = Modifier.height(6.dp))

                BreakdownRow("Black & White Jobs", bwOrders, orders.size)
                BreakdownRow("Colour Print Jobs", colorOrders, orders.size)
                BreakdownRow("Completed Orders", completedOrders, orders.size)

                Spacer(modifier = Modifier.height(14.dp))

                Text("Paper Size Consumption", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                Spacer(modifier = Modifier.height(6.dp))

                val a4Count = orders.filter { it.paperSize == "A4" }.sumOf { it.pageCount * it.copies }
                val a3Count = orders.filter { it.paperSize == "A3" }.sumOf { it.pageCount * it.copies }
                val legalCount = orders.filter { it.paperSize == "LEGAL" }.sumOf { it.pageCount * it.copies }

                Text("• A4 Standard: $a4Count pages", fontSize = 12.sp, color = Color(0xFF334155))
                Text("• A3 Large: $a3Count pages", fontSize = 12.sp, color = Color(0xFF334155))
                Text("• Legal Stamp: $legalCount pages", fontSize = 12.sp, color = Color(0xFF334155))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun MetricBox(title: String, value: String, subtitle: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .padding(12.dp)
    ) {
        Column {
            Text(title, fontSize = 11.sp, color = Color(0xFF64748B))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
private fun BreakdownRow(label: String, count: Int, total: Int) {
    val pct = if (total > 0) count.toFloat() / total else 0f
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = Color(0xFF475569))
            Text("$count (${(pct * 100).toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF2563EB),
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
