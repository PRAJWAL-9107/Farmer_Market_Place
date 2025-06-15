package com.example.fmplace.firebase

import android.util.Log
import com.example.fmplace.model.Product
import com.example.fmplace.model.ProductCategory
import com.example.fmplace.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseManager.firestore
    private val productsCollection = db.collection("products")
    
    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val documentRef = productsCollection.document()
            val productWithId = product.copy(id = documentRef.id)
            
            documentRef.set(productWithId.toMap()).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProductsByUser(userId: String): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection
                .whereEqualTo("sellerId", userId)
                .get()
                .await()
                
            val products = querySnapshot.documents.mapNotNull { document ->
                document.data?.let { data ->
                    val map = data as Map<String, Any>
                    try {
                        Product.fromMap(map)
                    } catch (e: Exception) {
                        Log.e("ProductRepository", "Error converting document to Product: ${e.message}")
                        null
                    }
                } ?: Product()
            }
            
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection.get().await()
            val products = querySnapshot.documents.mapNotNull { document ->
                document.data?.let { data ->
                    val map = data as Map<String, Any>
                    try {
                        Product.fromMap(map)
                    } catch (e: Exception) {
                        Log.e("ProductRepository", "Error converting document to Product: ${e.message}")
                        null
                    }
                } ?: Product()
            }
            
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProductsByCategory(category: ProductCategory): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection.get().await()
            
            val products = querySnapshot.documents.mapNotNull { document ->
                document.data?.let { data ->
                    val map = data as Map<String, Any>
                    try {
                        Product.fromMap(map)
                    } catch (e: Exception) {
                        Log.e("ProductRepository", "Error converting document to Product: ${e.message}")
                        null
                    }
                } ?: Product()
            }.filter { product ->
                product.category == category
            }
            
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productsCollection.document(product.id).set(product.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
