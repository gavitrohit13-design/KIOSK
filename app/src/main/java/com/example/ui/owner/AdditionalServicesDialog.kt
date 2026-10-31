package com.example.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Order
import java.util.Locale

@Composable
fun AdditionalServicesDialog(
    onDismiss: () -> Unit,
    onCreateServiceOrder: (Order) -> Unit
) {
    var photocopyCount by remember { mutableIntStateOf(0) }
    var scanCount by remember { mutableIntStateOf(0) }
    var passportPhotoSets by remember { mutableIntStateOf(0) }
    var laminationCount by remember { mutableIntStateOf(0) }
    var bindingCount by remember { mutableIntStateOf(0) }

    val photocopyRate = 2.0
    val scanRate = 10.0
    val passportPhotoRate = 50.0
    val laminationRate = 20.0
    val bindingRate = 40.0

    val totalAmount = (photocopyCount * photocopyRate) +
            (scanCount * scanRate) +
            (passportPhotoSets * passportPhotoRate) +
            (laminationCount * laminationRate) +
            (bindingCount * bindingRate)

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
                            text = "ADDITIONAL SERVICES BILLING",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "Photocopy, Scanning, Passport Photos, Lamination & Binding",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                ServiceItemCounter(
                    icon = Icons.Default.ContentCopy,
                    title = "Photocopy / Xerox",
                    rate = "₹$photocopyRate / page",
                    count = photocopyCount,
                    onIncrease = { photocopyCount++ },
                    onDecrease = { if (photocopyCount > 0) photocopyCount-- }
                )

                ServiceItemCounter(
                    icon = Icons.Default.DocumentScanner,
                    title = "Document Scanning (PDF/Email)",
                    rate = "₹$scanRate / page",
                    count = scanCount,
                    onIncrease = { scanCount++ },
                    onDecrease = { if (scanCount > 0) scanCount-- }
                )

                ServiceItemCounter(
                    icon = Icons.Default.CameraAlt,
                    title = "Passport Photos (8 photos set)",
                    rate = "₹$passportPhotoRate / set",
                    count = passportPhotoSets,
                    onIncrease = { passportPhotoSets++ },
                    onDecrease = { if (passportPhotoSets > 0) passportPhotoSets-- }
                )

                ServiceItemCounter(
                    icon = Icons.Default.Layers,
                    title = "Lamination (A4 Pouches)",
                    rate = "₹$laminationRate / sheet",
                    count = laminationCount,
                    onIncrease = { laminationCount++ },
                    onDecrease = { if (laminationCount > 0) laminationCount-- }
                )

                ServiceItemCounter(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Spiral / Book Binding",
                    rate = "₹$bindingRate / book",
                    count = bindingCount,
                    onIncrease = { bindingCount++ },
                    onDecrease = { if (bindingCount > 0) bindingCount-- }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Services Bill", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("₹${String.format(Locale.US, "%.2f", totalAmount)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF1E3A8A))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (totalAmount > 0) {
                            val descList = mutableListOf<String>()
                            if (photocopyCount > 0) descList.add("$photocopyCount Xerox")
                            if (scanCount > 0) descList.add("$scanCount Scans")
                            if (passportPhotoSets > 0) descList.add("$passportPhotoSets Passport Photo Sets")
                            if (laminationCount > 0) descList.add("$laminationCount Lamination")
                            if (bindingCount > 0) descList.add("$bindingCount Binding")

                            val order = Order(
                                customerName = "Walk-in Counter Client",
                                fileName = descList.joinToString(", "),
                                fileType = "SERVICE",
                                pageCount = maxOf(1, photocopyCount + scanCount),
                                copies = 1,
                                colorMode = "BW",
                                duplexMode = "SINGLE",
                                paperSize = "A4",
                                subtotal = totalAmount,
                                gstAmount = 0.0,
                                totalAmount = totalAmount,
                                paymentMethod = "CASH",
                                paymentStatus = "PAID",
                                orderStatus = "COMPLETED"
                            )
                            onCreateServiceOrder(order)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = totalAmount > 0,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = "Bill")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Receipt & Record Sale (₹${String.format(Locale.US, "%.0f", totalAmount)})")
                }
            }
        }
    }
}

@Composable
private fun ServiceItemCounter(
    icon: ImageVector,
    title: String,
    rate: String,
    count: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(rate, fontSize = 10.sp, color = Color(0xFF64748B))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(14.dp))
            }
            Text("$count", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp))
            IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(14.dp))
            }
        }
    }
}
