package com.example.fmplace.ui.buyer

import android.util.Log
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fmplace.R
import com.example.fmplace.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


//screen for buyers to view product details and contact sellers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(navController: NavController, productId: String) {
    val context = LocalContext.current
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    //load product details

    LaunchedEffect(productId) {
        isLoading = true
        error = null
        Log.d("ProductDetailsScreen", "Attempting to fetch product with ID: $productId")

        try {
            //get product from firestore
            val db = FirebaseFirestore.getInstance()
            val document = db.collection("products").document(productId).get().await()

            if (document.exists()) {
                Log.d("ProductDetailsScreen", "Document snapshot found: ${document.data}")
                val data = document.data
                if (data != null) {
                    product = Product.fromMap(data)
                    Log.d("ProductDetailsScreen", "Product object mapped: $product")
                } else {
                    error = "Product data is null"
                    Log.e("ProductDetailsScreen", "Product data is null")
                }
            } else {
                error = "Product not found"
                Log.w("ProductDetailsScreen", "Product with ID '$productId' not found in Firestore.")
            }
        } catch (e: Exception) {
            error = e.message ?: "Failed to load product details"
            Log.e("ProductDetailsScreen", "An error occurred while fetching product details.", e)
        } finally {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (product != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    //product image
                    val imageUri = try {
                        //try to parse the image URL as a URI
                        Uri.parse(product!!.imageUrl)
                    } catch (e: Exception) {
                        //if parsing fails, use the string as is
                        product!!.imageUrl
                    }
                    
                    AsyncImage(
                        model = imageUri,
                        contentDescription = product!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    
                    //product details
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = product!!.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "₹${product!!.price} / ${product!!.unit}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Category: ${product!!.category.name}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        if (product!!.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = product!!.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        //seller information
                        Text(
                            text = "Seller Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Seller: ${product!!.sellerName}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        //contact buttons
                        Text(
                            text = "Contact Seller",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        //call button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${product!!.sellerContact}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = "Call")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.call_seller))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        //WhatsApp button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/${product!!.sellerContact}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "WhatsApp")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.whatsapp_app))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        //SMS button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:${product!!.sellerContact}")
                                    putExtra("sms_body", "I'm interested in your product: ${product!!.name}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = "SMS")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.send_sms))
                        }
                    }
                }
            }
        }
    }
}
