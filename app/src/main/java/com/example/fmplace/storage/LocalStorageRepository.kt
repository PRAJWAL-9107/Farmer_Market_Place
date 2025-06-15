package com.example.fmplace.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

/**
 * Repository class for local storage operations
 * Handles saving and retrieving images from the device's internal storage
 */
class LocalStorageRepository(private val context: Context) {
    private val TAG = "LocalStorageRepository"
    
    // Directory for storing product images
    private val productImagesDir by lazy {
        File(context.filesDir, "product_images").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * Save an image from Uri to local storage
     * @param imageUri The Uri of the image to save
     * @return Result containing the local Uri of the saved image
     */
    suspend fun saveImage(imageUri: Uri): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val imageId = UUID.randomUUID().toString()
                val imageFile = File(productImagesDir, "$imageId.jpg")
                
                Log.d(TAG, "Saving image to ${imageFile.absolutePath}")
                
                // Copy the image from the Uri to our local file
                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    // Compress and resize the image before saving
                    val bitmap = compressAndResizeImage(inputStream)
                    
                    FileOutputStream(imageFile).use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        outputStream.flush()
                    }
                    
                    Log.d(TAG, "Image saved successfully, size: ${imageFile.length()} bytes")
                    
                    // Convert the file to a content Uri using FileProvider
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        imageFile
                    )
                    
                    Result.success(contentUri)
                } ?: run {
                    Log.e(TAG, "Failed to open input stream for Uri: $imageUri")
                    Result.failure(Exception("Failed to open input stream"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Delete an image from local storage
     * @param imageUri The Uri of the image to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteImage(imageUri: Uri): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Extract the filename from the Uri
                val path = imageUri.path
                if (path != null) {
                    val file = File(path)
                    if (file.exists() && file.delete()) {
                        Log.d(TAG, "Image deleted successfully: $path")
                        return@withContext Result.success(Unit)
                    } else {
                        Log.e(TAG, "Failed to delete image: $path")
                        return@withContext Result.failure(Exception("Failed to delete image"))
                    }
                } else {
                    Log.e(TAG, "Invalid image Uri: $imageUri")
                    return@withContext Result.failure(Exception("Invalid image Uri"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting image: ${e.message}", e)
                return@withContext Result.failure(e)
            }
        }
    }
    
    /**
     * Compress and resize an image to reduce storage size
     * @param inputStream The input stream of the image
     * @return A compressed and resized bitmap
     */
    private fun compressAndResizeImage(inputStream: InputStream): Bitmap {
        // Decode the image dimensions first
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.reset()
        
        // Calculate the sample size to resize the image
        val maxDimension = 1024 // Max width or height
        val width = options.outWidth
        val height = options.outHeight
        var sampleSize = 1
        
        if (width > maxDimension || height > maxDimension) {
            val widthRatio = Math.ceil(width.toDouble() / maxDimension).toInt()
            val heightRatio = Math.ceil(height.toDouble() / maxDimension).toInt()
            sampleSize = Math.max(widthRatio, heightRatio)
        }
        
        // Decode the image with the calculated sample size
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        
        return BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            ?: throw IllegalStateException("Failed to decode image")
    }
}
