package com.example.data.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import org.json.JSONObject

enum class OperationType {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LIST
}

object FirestoreErrorHandler {
    fun handleFirestoreError(
        exception: Throwable,
        operation: OperationType,
        path: String
    ) {
        val json = JSONObject().apply {
            put("error", exception.message ?: "Unknown error")
            put("operation", operation.name)
            put("path", path)
            if (exception is FirebaseFirestoreException) {
                put("code", exception.code.name)
            }
        }
        Log.e("FirestoreError", json.toString(2), exception)
    }
}
