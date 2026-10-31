package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Order
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PrintRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createOrder_authenticatedUser_succeeds() = runBlocking {
        val uid = signInTestUser("customer1@example.com")
        val repository = PrintRepository(firestore)

        val testOrder = Order(
            userId = uid,
            customerName = "Test Customer",
            customerPhone = "+919876543210",
            fileName = "Project_Report.pdf",
            fileType = "PDF",
            fileSize = 204800L,
            pageCount = 10,
            copies = 1,
            colorMode = "BW",
            duplexMode = "DOUBLE",
            paperSize = "A4",
            pageRange = "ALL",
            orientation = "PORTRAIT",
            subtotal = 100.0,
            gstAmount = 18.0,
            serviceCharge = 2.0,
            discountAmount = 0.0,
            totalAmount = 120.0,
            paymentMethod = "UPI",
            paymentStatus = "PENDING",
            orderStatus = "PENDING"
        )

        val orderId = repository.createOrder(testOrder)
        assertNotNull(orderId)
        assertTrue(orderId.startsWith("ORD-"))

        val orders = repository.observeOrders(uid, isOwner = false).first()
        assertTrue(orders.any { it.fileName == "Project_Report.pdf" })
    }

    @Test
    fun createOrder_unauthenticatedUser_fails() = runBlocking {
        auth.signOut()
        val repository = PrintRepository(firestore)

        val testOrder = Order(
            userId = "anon_user",
            fileName = "Hack.pdf",
            pageCount = 1,
            copies = 1,
            colorMode = "BW",
            duplexMode = "SINGLE",
            paperSize = "A4",
            totalAmount = 10.0,
            paymentStatus = "PENDING",
            orderStatus = "PENDING"
        )

        try {
            repository.createOrder(testOrder)
            fail("Expected exception when unauthenticated user creates order")
        } catch (e: Exception) {
            assertTrue(e.message?.contains("User must be signed in") == true || e.unwrapFirestoreException() != null)
        }
    }
}
