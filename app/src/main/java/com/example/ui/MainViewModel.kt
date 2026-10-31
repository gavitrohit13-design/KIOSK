package com.example.ui

import android.app.Application
import android.content.Context
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Order
import com.example.data.model.Printer
import com.example.data.model.Shop
import com.example.data.repository.PrintRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrintRepository(application)
    private val auth = Firebase.auth

    private val _currentTab = MutableStateFlow(0) // 0: Customer Portal, 1: Owner Panel
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    private val _activeInvoice = MutableStateFlow<Order?>(null)
    val activeInvoice: StateFlow<Order?> = _activeInvoice.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    // Real-time shop settings from Firestore
    val shopState: StateFlow<Shop> = repository.observeShop()
        .catch { emit(Shop()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = Shop()
        )

    // Real-time orders from Firestore
    val ordersState: StateFlow<List<Order>> = repository.observeOrders(
        currentUserId = auth.currentUser?.uid,
        isOwner = true
    ).catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Real-time hardware printers
    val printersState: StateFlow<List<Printer>> = repository.observePrinters()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        // Initialize shop and default printers if first launch
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: "owner_root"
            repository.createShopIfAbsent(Shop(ownerId = uid))
            repository.seedDefaultPrintersIfEmpty(uid)
        }
    }

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun showInvoice(order: Order) {
        _activeInvoice.value = order
    }

    fun dismissInvoice() {
        _activeInvoice.value = null
    }

    fun resetOrderFlow() {
        _activeOrder.value = null
    }

    fun placeAndPayOrder(order: Order) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val orderId = repository.createOrder(order)
                val submittedOrder = order.copy(id = orderId)
                _activeOrder.value = submittedOrder

                // Auto-print engine: If auto-print enabled, transition through spooler
                if (shopState.value.autoPrintEnabled) {
                    delay(1500)
                    repository.updateOrderStatus(orderId, "ACCEPTED", shopState.value.selectedPrinterId, true)
                    delay(2000)
                    repository.updateOrderStatus(orderId, "PRINTING", shopState.value.selectedPrinterId, true)
                    delay(3500)
                    repository.updateOrderStatus(orderId, "COMPLETED", shopState.value.selectedPrinterId, true)
                    _activeOrder.value = _activeOrder.value?.copy(orderStatus = "COMPLETED")
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Order failed: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String, printerName: String? = null) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, status, printerName)
                if (_activeOrder.value?.id == orderId) {
                    _activeOrder.value = _activeOrder.value?.copy(orderStatus = status)
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateShopSettings(updatedShop: Shop) {
        viewModelScope.launch {
            try {
                repository.updateShop(updatedShop)
                Toast.makeText(getApplication(), "Settings & Pricing Saved!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toggleAutoPrint(enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateShop(shopState.value.copy(autoPrintEnabled = enabled))
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Failed to toggle: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addPrinter(printer: Printer) {
        viewModelScope.launch {
            try {
                repository.savePrinter(printer)
                Toast.makeText(getApplication(), "Printer Added!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deletePrinter(printerId: String) {
        viewModelScope.launch {
            try {
                repository.deletePrinter(printerId)
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun selectPrinter(printerId: String) {
        viewModelScope.launch {
            try {
                repository.updateShop(shopState.value.copy(selectedPrinterId = printerId))
                Toast.makeText(getApplication(), "Assigned Default Printer", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Dispatches order to Android PrintManager spooler.
     */
    fun triggerPrint(context: Context, order: Order) {
        val adapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: android.os.Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder("RPG_AutoPrint_${order.id}.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(order.pageCount)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }
        }

        repository.triggerAndroidPrint(context, order, adapter)
        updateOrderStatus(order.id, "PRINTING")
    }
}
