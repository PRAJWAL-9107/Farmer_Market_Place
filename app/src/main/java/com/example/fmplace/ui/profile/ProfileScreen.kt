package com.example.fmplace.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fmplace.R
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.model.User
import com.example.fmplace.model.UserRole
import com.example.fmplace.ui.common.ErrorText
import com.example.fmplace.utils.FirestoreTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// TODO: Fix: All usages of ProfileScreen must provide firebaseAuth and db as arguments.
// Example:
// ProfileScreen(navController, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = AuthRepository(
        firebaseAuth = firebaseAuth,
        db = db
    )
    
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFirestoreConnected by remember { mutableStateOf(false) }
    
    //load user data
    LaunchedEffect(Unit) {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val result = authRepository.getUserData(currentUser.uid ?: "")
                if (result.isSuccess) {
                    user = result.getOrNull()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load user data"
                }
            } else {
                //if no user is found, navigate back to login
                navController.navigate("welcome") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                ErrorText(errorMessage!!, modifier = Modifier.align(Alignment.Center))
            } else if (user != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    //add settings button at the top
                    TextButton(
                        onClick = {
                            navController.navigate("settings")
                        }
                    ) {
                        Text(stringResource(R.string.edit_profile))
                    }

                    //user info
                    Text(
                        text = stringResource(R.string.welcome, user!!.name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Email: ${user!!.email}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    //common options for all users
                    OutlinedButton(
                        onClick = {
                            navController.navigate("add_product")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.add_product))
                    }
                        
                    Spacer(modifier = Modifier.height(16.dp))
                        
                    OutlinedButton(
                        onClick = {
                            navController.navigate("farmer_dashboard")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.my_products))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = {
                            navController.navigate("buyer_home")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.products))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    //firestore connection test button
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    isFirestoreConnected = FirestoreTest.testConnection(context)
                                } catch (e: Exception) {
                                    Log.e("ProfileScreen", "Error testing Firestore connection", e)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isFirestoreConnected) 
                                "Firestore Connection: Success" 
                            else 
                                "Test Firestore Connection"
                        )
                    }
                }
            }
        }
    }
}
