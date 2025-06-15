package com.example.fmplace.storage

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
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
}
