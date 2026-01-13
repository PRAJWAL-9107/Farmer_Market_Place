package com.example.fmplace.ui.farmer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fmplace.R
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.model.User
import com.example.fmplace.ui.common.DrawerMenu
import com.example.fmplace.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDashboardScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    // Firebase instances and coroutine scope
    val authRepository = remember { AuthRepository(
        firebaseAuth = firebaseAuth, // Firebase authentication instance
        db = db // Firestore database instance
    ) }
    val context = LocalContext.current // Get current context
    val coroutineScope = rememberCoroutineScope() // Create coroutine scope

    // State variables
    var drawerOpen by remember { mutableStateOf(false) } // Drawer state
    var user by remember { mutableStateOf<User?>(null) } // User state

    LaunchedEffect(Unit) { // Run on first composition
        // Get current user and navigate to welcome screen if not logged in
        val currentUser = firebaseAuth.currentUser // Get current user
        if (currentUser == null) {
            navController.navigate("welcome") {
                popUpTo("welcome") { inclusive = true }
            }
            return@LaunchedEffect
        }
        
        // Fetch user data
        val result = authRepository.getUserData(userId = firebaseAuth.currentUser?.uid.toString())
        if (result.isSuccess) {
            user = result.getOrNull() // Set user if fetch is successful
        } else {
            Utils.showToast(context, "Failed to load user data")
        }
    }
    
    Box(Modifier.fillMaxSize()) {
        // Scaffold with top app bar and drawer
        Scaffold(
            topBar = {
                // Top app bar with title and navigation icon
                TopAppBar(
                    title = { Text(stringResource(R.string.dashboard)) },
                    navigationIcon = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate("support")
                        }) {
                            Icon(Icons.Filled.Info, contentDescription = "Support")
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Main content column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Welcome message and buttons
                user?.let {
                    Text(
                        text = "Welcome, ${it.name}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                Button(
                    onClick = { navController.navigate("add_product") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Product")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_product))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { navController.navigate("my_products") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "My Products")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.my_products))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { navController.navigate("buyer_home") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Browse Products")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.products))
                }
            }
        }

        // Animated visibility for drawer
        AnimatedVisibility(
            visible = drawerOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            // Drawer content
            Box(
                Modifier
                    .fillMaxSize()
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { drawerOpen = false }
                )
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    DrawerMenu(
                        drawerState = null,
                        currentUser = user,
                        navController = navController,
                        onLogout = {
                            authRepository.logout()
                            navController.navigate("welcome") {
                                popUpTo("welcome") { inclusive = true }
                            }
                            drawerOpen = false
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
