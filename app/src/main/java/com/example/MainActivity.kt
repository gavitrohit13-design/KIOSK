package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Order
import com.example.ui.MainViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.components.InvoiceDialog
import com.example.ui.customer.CustomerPortalScreen
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.Strings
import com.example.ui.owner.OwnerDashboardScreen
import com.example.ui.owner.PrinterManagementDialog
import com.example.ui.owner.ReportsDialog
import com.example.ui.owner.ShopQrCodeDialog
import com.example.ui.owner.ShopSettingsDialog
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { auth ->
                        currentUser = auth.currentUser
                    }
                    Firebase.auth.addAuthStateListener(listener)
                    onDispose {
                        Firebase.auth.removeAuthStateListener(listener)
                    }
                }

                if (currentUser == null) {
                    AuthScreen(
                        onAuthSuccess = {
                            currentUser = Firebase.auth.currentUser
                        }
                    )
                } else {
                    AppMainContent(
                        viewModel = viewModel,
                        currentUser = currentUser!!,
                        onSignOut = {
                            Firebase.auth.signOut()
                            currentUser = null
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainContent(
    viewModel: MainViewModel,
    currentUser: FirebaseUser,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val language by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val shop by viewModel.shopState.collectAsStateWithLifecycle()
    val orders by viewModel.ordersState.collectAsStateWithLifecycle()
    val printers by viewModel.printersState.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activeOrder.collectAsStateWithLifecycle()
    val activeInvoice by viewModel.activeInvoice.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

    var showLanguageMenu by remember { mutableStateOf(false) }
    var showShopSettings by remember { mutableStateOf(false) }
    var showPrinterManager by remember { mutableStateOf(false) }
    var showReports by remember { mutableStateOf(false) }
    var showShopQr by remember { mutableStateOf(false) }
    var showServicesDialog by remember { mutableStateOf(false) }

    val pendingQueueCount = remember(orders) {
        orders.count { it.orderStatus == "PENDING" || it.orderStatus == "ACCEPTED" || it.orderStatus == "PRINTING" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A),
                    titleContentColor = Color.White
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Print,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.get("app_title", language),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = Strings.get("tagline", language),
                                fontSize = 10.sp,
                                color = Color(0xFF93C5FD)
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher
                    Box {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.displayName, fontWeight = if (lang.code == language) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.setLanguage(lang.code)
                                        showLanguageMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Sign-Out
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = Color(0xFFCBD5E1))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Customer") },
                    label = { Text(Strings.get("customer_portal", language), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E3A8A),
                        selectedTextColor = Color(0xFF1E3A8A),
                        indicatorColor = Color(0xFFDBEAFE)
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        if (pendingQueueCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = Color(0xFFDC2626)) {
                                        Text("$pendingQueueCount")
                                    }
                                }
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Owner")
                            }
                        } else {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Owner")
                        }
                    },
                    label = { Text(Strings.get("owner_dashboard", language), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E3A8A),
                        selectedTextColor = Color(0xFF1E3A8A),
                        indicatorColor = Color(0xFFDBEAFE)
                    )
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFFF8FAFC)
        ) {
            when (currentTab) {
                0 -> CustomerPortalScreen(
                    shop = shop,
                    language = language,
                    currentOrder = activeOrder,
                    isSubmitting = isSubmitting,
                    onPlaceAndPayOrder = { order -> viewModel.placeAndPayOrder(order) },
                    onViewInvoice = {
                        activeOrder?.let { viewModel.showInvoice(it) }
                    },
                    onNewOrder = { viewModel.resetOrderFlow() }
                )
                1 -> OwnerDashboardScreen(
                    shop = shop,
                    orders = orders,
                    printers = printers,
                    language = language,
                    onUpdateOrderStatus = { id, status, prt -> viewModel.updateOrderStatus(id, status, prt) },
                    onToggleAutoPrint = { enabled -> viewModel.toggleAutoPrint(enabled) },
                    onOpenSettings = { showShopSettings = true },
                    onOpenPrinters = { showPrinterManager = true },
                    onOpenReports = { showReports = true },
                    onOpenShopQr = { showShopQr = true },
                    onOpenServices = { showServicesDialog = true },
                    onViewInvoice = { order -> viewModel.showInvoice(order) },
                    onTriggerPrint = { order -> viewModel.triggerPrint(context, order) }
                )
            }
        }
    }

    // Modal Dialogs
    if (activeInvoice != null) {
        InvoiceDialog(
            order = activeInvoice!!,
            shop = shop,
            onDismiss = { viewModel.dismissInvoice() },
            onTriggerPrint = { order -> viewModel.triggerPrint(context, order) }
        )
    }

    if (showShopSettings) {
        ShopSettingsDialog(
            shop = shop,
            onDismiss = { showShopSettings = false },
            onSave = { updated ->
                viewModel.updateShopSettings(updated)
                showShopSettings = false
            }
        )
    }

    if (showPrinterManager) {
        PrinterManagementDialog(
            printers = printers,
            selectedPrinterId = shop.selectedPrinterId,
            onSelectPrinter = { id -> viewModel.selectPrinter(id) },
            onAddPrinter = { prt -> viewModel.addPrinter(prt) },
            onDeletePrinter = { id -> viewModel.deletePrinter(id) },
            onDismiss = { showPrinterManager = false }
        )
    }

    if (showReports) {
        ReportsDialog(
            orders = orders,
            onDismiss = { showReports = false }
        )
    }

    if (showShopQr) {
        ShopQrCodeDialog(
            shop = shop,
            onDismiss = { showShopQr = false }
        )
    }

    if (showServicesDialog) {
        com.example.ui.owner.AdditionalServicesDialog(
            onDismiss = { showServicesDialog = false },
            onCreateServiceOrder = { order -> viewModel.placeAndPayOrder(order) }
        )
    }
}
