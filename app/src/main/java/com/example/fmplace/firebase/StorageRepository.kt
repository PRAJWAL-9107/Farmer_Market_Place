package com.example.fmplace.firebase

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository class for Firebase Storage operations
 */
class StorageRepository {
    private val TAG = "StorageRepository"
    private val storage = FirebaseManager.storage
    private val storageRef = storage.reference
    
    /**
     * Upload an image to Firebase Storage
     */
    suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            val imageId = UUID.randomUUID().toString()
            
            // Ensure the storage reference exists
            if (storageRef == null) {
                Log.e(TAG, "Storage reference is null")
                return Result.failure(Exception("Storage reference is null"))
            }
            
            // Create the product_images directory if it doesn't exist
            val imageRef = storageRef.child("product_images/$imageId.jpg")
            
            Log.d(TAG, "Starting image upload to ${imageRef.path}")
            
            try {
                // Upload the file
                val uploadTask = imageRef.putFile(imageUri).await()
                Log.d(TAG, "Upload completed successfully: ${uploadTask.metadata?.sizeBytes} bytes")
                
                // Get the download URL
                val downloadUrl = imageRef.downloadUrl.await()
                Log.d(TAG, "Download URL obtained: $downloadUrl")
                
                Result.success(downloadUrl.toString())
            } catch (e: Exception) {
                Log.e(TAG, "Error during upload or getting download URL", e)
                Result.failure(e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Storage Error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Delete an image from Firebase Storage
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            Log.d(TAG, "Attempting to delete image: $imageUrl")
            storage.getReferenceFromUrl(imageUrl).delete().await()
            Log.d(TAG, "Image deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image: ${e.message}", e)
            Result.failure(e)
        }
    }
}
