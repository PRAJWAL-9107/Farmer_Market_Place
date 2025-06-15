package com.example.fmplace.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.model.UserRole
import com.example.fmplace.ui.common.ErrorText
import com.example.fmplace.ui.common.HeaderText
import com.example.fmplace.ui.common.PrimaryButton
import com.example.fmplace.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Login screen for user authentication
 */
@Composable
fun LoginScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    val authRepository = remember { AuthRepository(
        firebaseAuth = firebaseAuth,
        db = db
    ) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Removed auto-redirect if already logged in to always show the login form
    LaunchedEffect(Unit) {
        if (firebaseAuth.currentUser != null) {
            navController.navigate("home") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeaderText(
            text = "Login",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (errorMessage != null) {
            ErrorText(text = errorMessage!!)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PrimaryButton(
            text = "Login",
            onClick = {
                // Validate inputs
                when {
                    email.isBlank() -> errorMessage = "Email cannot be empty"
                    !Utils.isValidEmail(email) -> errorMessage = "Invalid email format"
                    password.isBlank() -> errorMessage = "Password cannot be empty"
                    else -> {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            try {
                                withTimeout(30000) {
                                    val result = authRepository.login(email, password)
                                    isLoading = false
                                    if (result.isSuccess) {
                                        navController.navigate("home")
                                    } else {
                                        val ex = result.exceptionOrNull()
                                        errorMessage = ex?.message ?: "Login failed"
                                    }
                                }
                            } catch (e: TimeoutCancellationException) {
                                isLoading = false
                                errorMessage = "Login timed out. Please try again."
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = if (e.message?.contains("network", true) == true) 
                                    "Network error. Please check your connection." 
                                else e.message ?: "Login failed"
                            }
                        }
                    }
                }
            },
            isLoading = isLoading,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = { navController.navigate("register") }
        ) {
            Text("New user? Register here")
        }
    }
}

// TODO: Fix: All usages of LoginScreen must provide firebaseAuth and db as arguments.
// Example:
// LoginScreen(navController, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
