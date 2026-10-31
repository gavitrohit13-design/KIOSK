package com.example.ui.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Shop

@Composable
fun ShopSettingsDialog(
    shop: Shop,
    onDismiss: () -> Unit,
    onSave: (Shop) -> Unit
) {
    var shopName by remember { mutableStateOf(shop.shopName) }
    var tagline by remember { mutableStateOf(shop.tagline) }
    var address by remember { mutableStateOf(shop.address) }
    var phone by remember { mutableStateOf(shop.phone) }
    var whatsapp by remember { mutableStateOf(shop.whatsapp) }
    var email by remember { mutableStateOf(shop.email) }
    var gstNumber by remember { mutableStateOf(shop.gstNumber) }
    var upiId by remember { mutableStateOf(shop.upiId) }
    var bankDetails by remember { mutableStateOf(shop.bankDetails) }
    var businessHours by remember { mutableStateOf(shop.businessHours) }

    var bwPrice by remember { mutableStateOf(shop.bwPricePerPage.toString()) }
    var colorPrice by remember { mutableStateOf(shop.colorPricePerPage.toString()) }
    var gstPercentage by remember { mutableStateOf(shop.gstPercentage.toString()) }
    var discountPercentage by remember { mutableStateOf(shop.discountPercentage.toString()) }
    var serviceCharge by remember { mutableStateOf(shop.serviceCharge.toString()) }
    var minOrderAmount by remember { mutableStateOf(shop.minOrderAmount.toString()) }
    var autoPrintEnabled by remember { mutableStateOf(shop.autoPrintEnabled) }

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
                    Text(
                        text = "OWNER PRICING & SETTINGS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E3A8A)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Configure pricing, taxes, UPI gateway, and shop credentials.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Auto Print Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto Print Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Instantly print when order payment is verified", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(checked = autoPrintEnabled, onCheckedChange = { autoPrintEnabled = it })
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Text("Printing Rates & Billing (₹)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bwPrice,
                        onValueChange = { bwPrice = it },
                        label = { Text("B&W Rate / pg", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = colorPrice,
                        onValueChange = { colorPrice = it },
                        label = { Text("Colour Rate / pg", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gstPercentage,
                        onValueChange = { gstPercentage = it },
                        label = { Text("GST (%)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = discountPercentage,
                        onValueChange = { discountPercentage = it },
                        label = { Text("Discount (%)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = serviceCharge,
                        onValueChange = { serviceCharge = it },
                        label = { Text("Service Fee (₹)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = minOrderAmount,
                        onValueChange = { minOrderAmount = it },
                        label = { Text("Min Order (₹)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Shop & Gateway Identity", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID (for payments)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = gstNumber,
                    onValueChange = { gstNumber = it },
                    label = { Text("GST Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Shop Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val updated = shop.copy(
                            shopName = shopName,
                            tagline = tagline,
                            address = address,
                            phone = phone,
                            whatsapp = whatsapp,
                            email = email,
                            gstNumber = gstNumber,
                            upiId = upiId,
                            bankDetails = bankDetails,
                            businessHours = businessHours,
                            bwPricePerPage = bwPrice.toDoubleOrNull() ?: shop.bwPricePerPage,
                            colorPricePerPage = colorPrice.toDoubleOrNull() ?: shop.colorPricePerPage,
                            gstPercentage = gstPercentage.toDoubleOrNull() ?: shop.gstPercentage,
                            discountPercentage = discountPercentage.toDoubleOrNull() ?: shop.discountPercentage,
                            serviceCharge = serviceCharge.toDoubleOrNull() ?: shop.serviceCharge,
                            minOrderAmount = minOrderAmount.toDoubleOrNull() ?: shop.minOrderAmount,
                            autoPrintEnabled = autoPrintEnabled
                        )
                        onSave(updated)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Save & Apply Pricing", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
