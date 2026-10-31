package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Printer(
    @DocumentId
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val type: String = "LASER", // "LASER", "INKJET", "THERMAL", "PLOTTER"
    val connectionType: String = "NETWORK", // "USB", "NETWORK", "WIFI"
    val ipAddress: String = "192.168.1.101",
    val colorSupported: Boolean = false,
    val duplexSupported: Boolean = true,
    val status: String = "ONLINE", // "ONLINE", "OFFLINE", "PRINTING", "OUT_OF_PAPER", "PAUSED"
    val paperLevelPercent: Int = 85,
    val inkLevelPercent: Int = 90,
    val queueCount: Int = 0,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
