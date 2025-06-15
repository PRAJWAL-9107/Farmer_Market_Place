package com.example.fmplace.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.fmplace.firebase.FirebaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirestoreTest {
    private const val TAG = "FirestoreTest"
    suspend fun testConnection(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val db = FirebaseManager.firestore
                val testCollection = db.collection("test")
                val testDocument = testCollection.document("test_connection")
                val testData = hashMapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "message" to "Firestore connection test"
                )
                testDocument.set(testData).await()
                val snapshot = testDocument.get().await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Firestore connection successful!", Toast.LENGTH_SHORT).show()
                }
                true
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, 
                        "Firestore connection failed: ${e.message}", 
                        Toast.LENGTH_LONG
                    ).show()
                }
                false
            }
        }
    }
}
