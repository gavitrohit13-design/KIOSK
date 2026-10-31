package com.example.data.repository

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import com.example.R
import com.example.data.model.Order
import com.example.data.model.Printer
import com.example.data.model.Shop
import com.example.data.util.FirestoreErrorHandler
import com.example.data.util.OperationType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PrintRepository(val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeShop(): Flow<Shop> = flow {
        val path = "shops/main_shop"
        emitAll(
            db.collection("shops").document("main_shop")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObject(Shop::class.java) ?: Shop()
                }
                .catch { error ->
                    FirestoreErrorHandler.handleFirestoreError(error, OperationType.READ, path)
                    throw error
                }
        )
    }

    suspend fun createShopIfAbsent(defaultShop: Shop) {
        val docRef = db.collection("shops").document("main_shop")
        try {
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val uid = auth.currentUser?.uid ?: defaultShop.ownerId
                val payload = hashMapOf<String, Any>(
                    "ownerId" to uid,
                    "shopName" to defaultShop.shopName,
                    "tagline" to defaultShop.tagline,
                    "address" to defaultShop.address,
                    "phone" to defaultShop.phone,
                    "whatsapp" to defaultShop.whatsapp,
                    "email" to defaultShop.email,
                    "gstNumber" to defaultShop.gstNumber,
                    "upiId" to defaultShop.upiId,
                    "bankDetails" to defaultShop.bankDetails,
                    "businessHours" to defaultShop.businessHours,
                    "bwPricePerPage" to defaultShop.bwPricePerPage,
                    "colorPricePerPage" to defaultShop.colorPricePerPage,
                    "gstPercentage" to defaultShop.gstPercentage,
                    "discountPercentage" to defaultShop.discountPercentage,
                    "serviceCharge" to defaultShop.serviceCharge,
                    "minOrderAmount" to defaultShop.minOrderAmount,
                    "autoPrintEnabled" to defaultShop.autoPrintEnabled,
                    "selectedPrinterId" to defaultShop.selectedPrinterId,
                    "language" to defaultShop.language,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }

    suspend fun updateShop(shop: Shop) {
        val docRef = db.collection("shops").document("main_shop")
        try {
            val updates = hashMapOf<String, Any>(
                "shopName" to shop.shopName,
                "tagline" to shop.tagline,
                "address" to shop.address,
                "phone" to shop.phone,
                "whatsapp" to shop.whatsapp,
                "email" to shop.email,
                "gstNumber" to shop.gstNumber,
                "upiId" to shop.upiId,
                "bankDetails" to shop.bankDetails,
                "businessHours" to shop.businessHours,
                "bwPricePerPage" to shop.bwPricePerPage,
                "colorPricePerPage" to shop.colorPricePerPage,
                "gstPercentage" to shop.gstPercentage,
                "discountPercentage" to shop.discountPercentage,
                "serviceCharge" to shop.serviceCharge,
                "minOrderAmount" to shop.minOrderAmount,
                "autoPrintEnabled" to shop.autoPrintEnabled,
                "selectedPrinterId" to shop.selectedPrinterId,
                "language" to shop.language,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    fun observeOrders(currentUserId: String?, isOwner: Boolean): Flow<List<Order>> = flow {
        val query = if (isOwner || currentUserId == null) {
            db.collection("orders")
        } else {
            db.collection("orders").whereEqualTo("userId", currentUserId)
        }
        val path = "orders"
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Order::class.java).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    FirestoreErrorHandler.handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createOrder(order: Order): String {
        val uid = requireUserId()
        val orderId = "ORD-" + System.currentTimeMillis().toString().takeLast(6)
        val docRef = db.collection("orders").document(orderId)

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "customerName" to order.customerName,
            "customerPhone" to order.customerPhone,
            "fileName" to order.fileName,
            "fileType" to order.fileType,
            "fileSize" to order.fileSize,
            "pageCount" to order.pageCount,
            "copies" to order.copies,
            "colorMode" to order.colorMode,
            "duplexMode" to order.duplexMode,
            "paperSize" to order.paperSize,
            "pageRange" to order.pageRange,
            "orientation" to order.orientation,
            "scaleMode" to order.scaleMode,
            "printQuality" to order.printQuality,
            "subtotal" to order.subtotal,
            "gstAmount" to order.gstAmount,
            "serviceCharge" to order.serviceCharge,
            "discountAmount" to order.discountAmount,
            "totalAmount" to order.totalAmount,
            "paymentMethod" to order.paymentMethod,
            "paymentStatus" to order.paymentStatus,
            "paymentTxnId" to order.paymentTxnId,
            "orderStatus" to order.orderStatus,
            "printerAssigned" to order.printerAssigned,
            "isAutoPrinted" to order.isAutoPrinted,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
            return orderId
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateOrderStatus(
        orderId: String,
        status: String,
        printerAssigned: String? = null,
        isAutoPrinted: Boolean? = null
    ) {
        val docRef = db.collection("orders").document(orderId)
        val updates = mutableMapOf<String, Any>(
            "orderStatus" to status,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (printerAssigned != null) {
            updates["printerAssigned"] = printerAssigned
        }
        if (isAutoPrinted != null) {
            updates["isAutoPrinted"] = isAutoPrinted
        }
        try {
            docRef.update(updates).await()
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updatePayment(
        orderId: String,
        paymentStatus: String,
        paymentTxnId: String,
        paymentMethod: String
    ) {
        val docRef = db.collection("orders").document(orderId)
        val updates = hashMapOf<String, Any>(
            "paymentStatus" to paymentStatus,
            "paymentTxnId" to paymentTxnId,
            "paymentMethod" to paymentMethod,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.update(updates).await()
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    fun observePrinters(): Flow<List<Printer>> = flow {
        val path = "printers"
        emitAll(
            db.collection("printers")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Printer::class.java)
                }
                .catch { error ->
                    FirestoreErrorHandler.handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun savePrinter(printer: Printer) {
        val uid = requireUserId()
        val printerId = if (printer.id.isNotBlank()) printer.id else "PRT-" + System.currentTimeMillis().toString().takeLast(4)
        val docRef = db.collection("printers").document(printerId)

        val payload = hashMapOf<String, Any>(
            "ownerId" to uid,
            "name" to printer.name,
            "type" to printer.type,
            "connectionType" to printer.connectionType,
            "ipAddress" to printer.ipAddress,
            "colorSupported" to printer.colorSupported,
            "duplexSupported" to printer.duplexSupported,
            "status" to printer.status,
            "paperLevelPercent" to printer.paperLevelPercent,
            "inkLevelPercent" to printer.inkLevelPercent,
            "queueCount" to printer.queueCount,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            val exists = docRef.get().await().exists()
            if (!exists) {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            } else {
                docRef.update(payload).await()
            }
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deletePrinter(printerId: String) {
        val docRef = db.collection("printers").document(printerId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun seedDefaultPrintersIfEmpty(ownerId: String) {
        val colRef = db.collection("printers")
        try {
            val snapshot = colRef.get().await()
            if (snapshot.isEmpty) {
                val defaults = listOf(
                    Printer(
                        id = "laser_bw_1",
                        ownerId = ownerId,
                        name = "HP LaserJet Pro M404dn (B&W High Speed)",
                        type = "LASER",
                        connectionType = "NETWORK",
                        ipAddress = "192.168.1.101",
                        colorSupported = false,
                        duplexSupported = true,
                        status = "ONLINE",
                        paperLevelPercent = 90,
                        inkLevelPercent = 82,
                        queueCount = 0
                    ),
                    Printer(
                        id = "color_inkjet_1",
                        ownerId = ownerId,
                        name = "Epson EcoTank L3250 (All-in-One Colour)",
                        type = "INKJET",
                        connectionType = "WIFI",
                        ipAddress = "192.168.1.102",
                        colorSupported = true,
                        duplexSupported = false,
                        status = "ONLINE",
                        paperLevelPercent = 75,
                        inkLevelPercent = 95,
                        queueCount = 0
                    ),
                    Printer(
                        id = "thermal_pos_1",
                        ownerId = ownerId,
                        name = "TVS RP 3200 Star (Receipt / Token Thermal)",
                        type = "THERMAL",
                        connectionType = "USB",
                        ipAddress = "USB001",
                        colorSupported = false,
                        duplexSupported = false,
                        status = "ONLINE",
                        paperLevelPercent = 60,
                        inkLevelPercent = 100,
                        queueCount = 0
                    ),
                    Printer(
                        id = "large_plotter_1",
                        ownerId = ownerId,
                        name = "Canon imagePROGRAF iPF770 (A3/Legal Plotter)",
                        type = "PLOTTER",
                        connectionType = "NETWORK",
                        ipAddress = "192.168.1.105",
                        colorSupported = true,
                        duplexSupported = true,
                        status = "ONLINE",
                        paperLevelPercent = 88,
                        inkLevelPercent = 70,
                        queueCount = 0
                    )
                )
                for (prt in defaults) {
                    val doc = colRef.document(prt.id)
                    doc.set(
                        hashMapOf(
                            "ownerId" to ownerId,
                            "name" to prt.name,
                            "type" to prt.type,
                            "connectionType" to prt.connectionType,
                            "ipAddress" to prt.ipAddress,
                            "colorSupported" to prt.colorSupported,
                            "duplexSupported" to prt.duplexSupported,
                            "status" to prt.status,
                            "paperLevelPercent" to prt.paperLevelPercent,
                            "inkLevelPercent" to prt.inkLevelPercent,
                            "queueCount" to prt.queueCount,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, colRef.path)
        }
    }

    /**
     * Triggers Android Print Spooler (PrintManager) for an order invoice or document.
     */
    fun triggerAndroidPrint(context: Context, order: Order, printAdapter: PrintDocumentAdapter) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "RPG_Print_${order.id}"
        val printAttributes = PrintAttributes.Builder()
            .setColorMode(if (order.colorMode == "COLOR") PrintAttributes.COLOR_MODE_COLOR else PrintAttributes.COLOR_MODE_MONOCHROME)
            .setMediaSize(
                when (order.paperSize) {
                    "A3" -> PrintAttributes.MediaSize.ISO_A3
                    "LEGAL" -> PrintAttributes.MediaSize.NA_LEGAL
                    else -> PrintAttributes.MediaSize.ISO_A4
                }
            )
            .build()
        printManager.print(jobName, printAdapter, printAttributes)
    }
}
