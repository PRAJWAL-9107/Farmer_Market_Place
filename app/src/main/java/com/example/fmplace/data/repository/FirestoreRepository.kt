package com.example.fmplace.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.example.fmplace.domain.model.Product
import com.example.fmplace.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val productsCollection = firestore.collection("products")
    private val usersCollection = firestore.collection("users")
    suspend fun addProduct(product: Product): String {
        return productsCollection.add(product).await().id
    }
    suspend fun getProduct(productId: String): Product? {
        return productsCollection.document(productId).get().await().toObject<Product>()
    }
    suspend fun getProducts(): List<Product> {
        return productsCollection.get().await().documents.mapNotNull { it.toObject<Product>() }
    }
    suspend fun getUser(userId: String): User? {
        return usersCollection.document(userId).get().await().toObject<User>()
    }
    suspend fun updateUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }
}
