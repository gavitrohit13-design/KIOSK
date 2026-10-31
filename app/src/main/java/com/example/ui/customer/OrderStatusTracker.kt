package com.example.ui.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Shop
import com.example.ui.locale.Strings

@Composable
fun OrderStatusTracker(
    order: Order,
    shop: Shop,
    language: String,
    onViewInvoice: () -> Unit,
    onNewOrder: () -> Unit
) {
    val step = when (order.orderStatus) {
        "PENDING" -> if (order.paymentStatus == "PAID") 2 else 1
        "ACCEPTED" -> 2
        "PRINTING" -> 3
        "COMPLETED" -> 4
        "CANCELLED" -> 0
        else -> 1
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Strings.get("order_tracking", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "ID: ${order.id}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF2563EB)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (order.orderStatus) {
                                "COMPLETED" -> Color(0xFFDCFCE7)
                                "PRINTING" -> Color(0xFFDBEAFE)
                                "CANCELLED" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFFEF3C7)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (order.orderStatus) {
                            "COMPLETED" -> Strings.get("status_completed", language)
                            "PRINTING" -> Strings.get("status_printing", language)
                            "CANCELLED" -> Strings.get("status_cancelled", language)
                            "ACCEPTED" -> Strings.get("status_accepted", language)
                            else -> Strings.get("status_pending", language)
                        },
                        fontSize = 11.sp,
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

            Spacer(modifier = Modifier.height(20.dp))

            // Stepper timeline
            TimelineStep(
                title = Strings.get("payment_success", language),
                subtitle = "Txn ID: ${order.paymentTxnId.ifBlank { "Verified" }} (${order.paymentMethod})",
                icon = Icons.Default.Payments,
                isCompleted = order.paymentStatus == "PAID",
                isActive = step == 1,
                isLast = false
            )

            TimelineStep(
                title = Strings.get("status_accepted", language),
                subtitle = "Assigned to: ${order.printerAssigned}",
                icon = Icons.Default.CheckCircle,
                isCompleted = step >= 2,
                isActive = step == 2,
                isLast = false
            )

            TimelineStep(
                title = Strings.get("status_printing", language),
                subtitle = "${order.pageCount} pages • ${order.copies} copy • ${order.colorMode}",
                icon = Icons.Default.Print,
                isCompleted = step >= 4,
                isActive = step == 3,
                showPulse = step == 3,
                isLast = false
            )

            TimelineStep(
                title = Strings.get("status_completed", language),
                subtitle = "Collect from shop counter at ${shop.shopName}",
                icon = Icons.Default.Storefront,
                isCompleted = step >= 4,
                isActive = step == 4,
                isLast = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (step == 3) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF2563EB),
                    trackColor = Color(0xFFDBEAFE)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Printing in progress... Please wait 10-30 seconds",
                    fontSize = 12.sp,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewInvoice,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = "Invoice", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.get("view_invoice", language), fontSize = 12.sp)
                }

                Button(
                    onClick = onNewOrder,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Text("+ Print Another", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun TimelineStep(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isCompleted: Boolean,
    isActive: Boolean,
    showPulse: Boolean = false,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Color(0xFF10B981)
                            isActive -> Color(0xFF2563EB)
                            else -> Color(0xFFE2E8F0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (showPulse) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.Check else icon,
                        contentDescription = title,
                        tint = if (isCompleted || isActive) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(if (isCompleted) Color(0xFF10B981) else Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive || isCompleted) Color(0xFF0F172A) else Color(0xFF64748B)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
