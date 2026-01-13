package com.example.fmplace.firebase

import com.example.fmplace.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    init {
        // Persistence is now enabled by default, no need to explicitly set it
        // Firebase Firestore will use default settings automatically
    }
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    private val usersCollection = db.collection("users")
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun register(email: String, password: String, user: User): Result<FirebaseUser> {
        return try {
            println("Firebase Register: Starting registration for $email")
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                println("Firebase Register: User created successfully with UID: ${authResult.user?.uid}")
                val userId = authResult.user!!.uid
                val userWithId = user.copy(id = userId)
                println("Firebase Register: Saving user data to Firestore for user: $userId")
                usersCollection.document(userId).set(userWithId).await()
                println("Firebase Register: User data saved successfully to Firestore")
                Result.success(authResult.user!!)
            } catch (e: Exception) {
                println("Firebase Register Error during operation: ${e.message}")
                println("Firebase Register Error type: ${e.javaClass.simpleName}")
                e.printStackTrace()
                Result.failure(e)
            }
        } catch (e: Exception) {
            println("Firebase Register Outer Error: ${e.message}")
            println("Firebase Register Outer Error type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                val defaultUser = User(
                    id = userId,
                    email = firebaseAuth.currentUser?.email ?: "",
                    name = "",
                    phone = "",
                    whatsapp = "",
                    language = "en"
                )
                usersCollection.document(userId).set(defaultUser).await()
                Result.success(defaultUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateUserData(userId: String, user: User): Result<Unit> {
        return try {
            usersCollection.document(userId).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun logout() = firebaseAuth.signOut()
}
