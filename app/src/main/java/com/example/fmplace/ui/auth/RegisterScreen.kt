package com.example.fmplace.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.fmplace.model.User
import com.example.fmplace.ui.common.ErrorText
import com.example.fmplace.ui.common.HeaderText
import com.example.fmplace.ui.common.PrimaryButton
import com.example.fmplace.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * Registration screen for new users
 */
@Composable
fun RegisterScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    val authRepository = remember { AuthRepository(
        firebaseAuth = firebaseAuth,
        db = db
    ) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HeaderText(
            text = "Register",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm Password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Phone field
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // WhatsApp field
        OutlinedTextField(
            value = whatsapp,
            onValueChange = { whatsapp = it },
            label = { Text("WhatsApp Number (Optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMessage != null) {
            ErrorText(text = errorMessage!!)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PrimaryButton(
            text = "Register",
            onClick = {
                // Validate inputs
                when {
                    name.isBlank() -> errorMessage = "Name cannot be empty"
                    email.isBlank() -> errorMessage = "Email cannot be empty"
                    !Utils.isValidEmail(email) -> errorMessage = "Invalid email format"
                    password.isBlank() -> errorMessage = "Password cannot be empty"
                    !Utils.isValidPassword(password) -> errorMessage = "Password must be at least 6 characters"
                    password != confirmPassword -> errorMessage = "Passwords do not match"
                    phone.isBlank() -> errorMessage = "Phone number cannot be empty"
                    else -> {
                        isLoading = true
                        errorMessage = null
                        
                        val user = User(
                            name = name,
                            email = email,
                            phone = phone,
                            whatsapp = whatsapp.ifBlank { phone }
                        )
                        
                        coroutineScope.launch {
                            try {
                                // Add a timeout of 30 seconds for the registration process
                                withTimeout(30000) {
                                    val result = authRepository.register(email, password, user)
                                    isLoading = false
                                    
                                    if (result.isSuccess) {
                                        // Registration successful, navigate to home
                                        navController.navigate("home") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    } else {
                                        val exception = result.exceptionOrNull()
                                        errorMessage = when {
                                            exception?.message?.contains("network") == true -> 
                                                "Network error. Please check your internet connection."
                                            exception?.message?.contains("API key") == true -> 
                                                "Firebase API key error. Please contact support."
                                            exception?.message?.contains("email") == true -> 
                                                "Email already in use. Try another email or login."
                                            else -> exception?.message ?: "Registration failed. Please try again."
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = when (e) {
                                    is TimeoutCancellationException -> "Registration timed out. Please try again."
                                    else -> "Error: ${e.message ?: "Unknown error occurred"}"
                                }
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
            onClick = { navController.navigate("login") }
        ) {
            Text("Already have an account? Login")
        }
    }
}

// TODO: Fix: All usages of RegisterScreen must provide firebaseAuth and db as arguments.
// Example:
// RegisterScreen(navController, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
