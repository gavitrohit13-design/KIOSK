package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Shop(
    @DocumentId
    val id: String = "main_shop",
    val ownerId: String = "",
    val shopName: String = "RPG AUTO PRINT",
    val tagline: String = "Scan • Upload • Pay • Print",
    val address: String = "Shop No. 4, College Road, Tech Hub",
    val phone: String = "+91 98765 43210",
    val whatsapp: String = "+91 98765 43210",
    val email: String = "gvtrohit5@gmail.com",
    val gstNumber: String = "27AAAAA0000A1Z5",
    val upiId: String = "rpgautoprint@upi",
    val bankDetails: String = "HDFC Bank | A/C: 50100234567890 | IFSC: HDFC0001234",
    val businessHours: String = "9:00 AM - 10:00 PM (Mon-Sun)",
    val bwPricePerPage: Double = 10.0,
    val colorPricePerPage: Double = 20.0,
    val gstPercentage: Double = 18.0,
    val discountPercentage: Double = 0.0,
    val serviceCharge: Double = 2.0,
    val minOrderAmount: Double = 10.0,
    val autoPrintEnabled: Boolean = true,
    val selectedPrinterId: String = "laser_bw_1",
    val language: String = "en",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
