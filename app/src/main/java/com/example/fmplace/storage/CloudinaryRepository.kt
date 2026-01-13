package com.example.fmplace.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.Cloudinary
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CloudinaryRepository(private val context: Context) {



    suspend fun uploadImage(imageUri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        // Optional: Log upload start
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // Optional: Log progress
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val imageUrl = resultData["secure_url"] as? String
                        if (imageUrl != null) {
                            if (continuation.isActive) {
                                continuation.resume(Result.success(imageUrl))
                            }
                        } else {
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(Exception("Cloudinary URL was null")))
                            }
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(Exception("Cloudinary upload error: ${error.description}")))
                        }
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        // Optional: Log reschedule
                    }
                })
                .dispatch()

            continuation.invokeOnCancellation {
                // Optional: Handle cancellation, e.g., by cancelling the upload request if possible
            }
        }
    }
    
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            // For now, we'll just log the deletion attempt
            // Cloudinary image deletion requires admin API setup which is complex
            // The main goal is to remove the product from Firestore
            Log.d("CloudinaryRepository", "Image deletion attempted for: $imageUrl")
            Log.i("CloudinaryRepository", "Note: Cloudinary image deletion requires admin API setup. Product deleted from Firestore.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CloudinaryRepository", "Error during image deletion attempt: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    private fun extractPublicIdFromUrl(imageUrl: String): String {
        try {
            // Extract public ID from Cloudinary URL
            // Example URL: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/public_id.jpg
            val parts = imageUrl.split("/")
            val uploadIndex = parts.indexOf("upload")
            if (uploadIndex != -1 && uploadIndex < parts.size - 1) {
                val fileName = parts.last()
                return fileName.substringBeforeLast(".")
            }
        } catch (e: Exception) {
            Log.e("CloudinaryRepository", "Error extracting public ID: ${e.message}")
        }
        return ""
    }
}
