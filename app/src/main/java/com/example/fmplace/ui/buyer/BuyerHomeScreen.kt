package com.example.fmplace.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.firebase.ProductRepository
import com.example.fmplace.model.Product
import com.example.fmplace.model.ProductCategory
import com.example.fmplace.model.User
import com.example.fmplace.ui.common.DrawerMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip

// TODO: Fix: All usages of BuyerHomeScreen must provide firebaseAuth and db as arguments.
// Example:
// BuyerHomeScreen(navController, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

//home screen for buyers to browse products

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerHomeScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    val productRepository = remember { ProductRepository() }
    val authRepository = remember { AuthRepository(
        firebaseAuth = firebaseAuth,
        db = db
    ) }
    val coroutineScope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var drawerOpen by remember { mutableStateOf(false) }
    
    //load user data
    LaunchedEffect(Unit) {
        if (firebaseAuth.currentUser != null) {
            val result = authRepository.getUserData(firebaseAuth.currentUser!!.uid)
            if (result.isSuccess) {
                currentUser = result.getOrNull()
            }
        }
    }
    
    //load products
    LaunchedEffect(selectedCategory) {
        isLoading = true
        error = null
        
        val result = if (selectedCategory == null) {
            productRepository.getAllProducts()
        } else {
            productRepository.getProductsByCategory(selectedCategory!!)
        }
        
        isLoading = false
        
        if (result.isSuccess) {
            products = result.getOrNull() ?: emptyList()
        } else {
            error = result.exceptionOrNull()?.message ?: "Failed to load products"
        }
    }
    
    Box(Modifier.fillMaxSize()) {
        // Main content (Scaffold)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Browse Products") },
                    navigationIcon = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                //category filter
                ScrollableTabRow(
                    selectedTabIndex = if (selectedCategory == null) 0 else selectedCategory!!.ordinal + 1,
                    edgePadding = 16.dp
                ) {
                    Tab(
                        selected = selectedCategory == null,
                        onClick = { 
                            println("Selected: All")
                            selectedCategory = null 
                        },
                        text = { Text("All") }
                    )
                    
                    ProductCategory.values().forEachIndexed { index, category ->
                        Tab(
                            selected = selectedCategory == category,
                            onClick = { 
                                println("Selected category: ${category.name} at index $index")
                                selectedCategory = category 
                            },
                            text = { Text(category.name) }
                        )
                    }
                }
                
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No products found")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                onClick = {
                                    navController.navigate("product_details/${product.id}")
                                }
                            )
                        }
                    }
                }
            }
        }

        // Custom Drawer Overlay
        AnimatedVisibility(
            visible = drawerOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            Box(Modifier.fillMaxSize()) {
                // Scrim to close drawer
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { drawerOpen = false }
                )
                // Drawer content
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    DrawerMenu(
                        drawerState = null,
                        currentUser = currentUser,
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

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Seller info with profile picture
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = if (product.sellerProfilePictureUrl.isNotBlank()) product.sellerProfilePictureUrl else null,
                        contentDescription = "Seller Profile",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Text(
                        text = product.sellerName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "₹${product.price} / ${product.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.category.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
