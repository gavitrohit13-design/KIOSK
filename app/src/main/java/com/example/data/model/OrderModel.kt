package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Order(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val customerName: String = "Walk-in Customer",
    val customerPhone: String = "",
    val fileName: String = "Document.pdf",
    val fileType: String = "PDF",
    val fileSize: Long = 0L,
    val pageCount: Int = 1,
    val copies: Int = 1,
    val colorMode: String = "BW", // "BW" or "COLOR"
    val duplexMode: String = "SINGLE", // "SINGLE" or "DOUBLE"
    val paperSize: String = "A4", // "A4", "A3", "LEGAL"
    val pageRange: String = "ALL",
    val orientation: String = "PORTRAIT", // "PORTRAIT" or "LANDSCAPE"
    val scaleMode: String = "FIT_TO_PAGE", // "FIT_TO_PAGE" or "ACTUAL_SIZE"
    val printQuality: String = "NORMAL", // "NORMAL", "HIGH", "DRAFT"
    val subtotal: Double = 10.0,
    val gstAmount: Double = 1.8,
    val serviceCharge: Double = 2.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 13.8,
    val paymentMethod: String = "UPI", // "UPI", "QR", "GPAY", "PHONEPE", "PAYTM", "RAZORPAY", "CASH"
    val paymentStatus: String = "PENDING", // "PENDING", "PAID", "FAILED"
    val paymentTxnId: String = "",
    val orderStatus: String = "PENDING", // "PENDING", "ACCEPTED", "PRINTING", "COMPLETED", "CANCELLED"
    val printerAssigned: String = "HP LaserJet M404dn",
    val isAutoPrinted: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
