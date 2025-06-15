package com.example.fmplace.ui.farmer

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.stringResource
import com.example.fmplace.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.firebase.ProductRepository
import com.example.fmplace.model.Product
import com.example.fmplace.model.ProductCategory
import com.example.fmplace.model.UnitOption
import com.example.fmplace.storage.CloudinaryRepository
import com.example.fmplace.ui.common.ErrorText
import com.example.fmplace.ui.common.PrimaryButton
import com.example.fmplace.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// TODO: Fix: All usages of AddProductScreen must provide firebaseAuth and db as arguments.
// Example:
// AddProductScreen(navController, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

//screen for farmers to add new products

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    val productRepository = remember { ProductRepository() }
    val context = LocalContext.current
    val cloudinaryRepository = remember { CloudinaryRepository(context) }
    val authRepository = remember { AuthRepository(
        firebaseAuth = firebaseAuth,
        db = db
    ) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.VEGETABLES) }
    var price by remember { mutableStateOf("") }
    var unit: UnitOption by remember { mutableStateOf(UnitOption.KG) }
    var description by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    //category dropdown state
    var categoryExpanded by remember { mutableStateOf(false) }
    
    //image picker
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_product)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //image selection
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(48.dp)
                        )
                        Text(stringResource(R.string.image))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            //form fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            //category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.category)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    ProductCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            //price and unit
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(2f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                //unit dropdown
                var unitExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = unit.name,
                        onValueChange = { },
                        label = { Text(stringResource(R.string.unit_option)) },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Unit",
                                modifier = Modifier.clickable { unitExpanded = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    DropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        UnitOption.values().forEach { unitOption ->
                            DropdownMenuItem(
                                text = { Text(stringResource(when (unitOption) {
                                    UnitOption.KG -> R.string.unit_kg
                                    UnitOption.GRAM -> R.string.unit_gram
                                    UnitOption.PIECE -> R.string.unit_piece
                                    UnitOption.DOZEN -> R.string.unit_dozen
                                    UnitOption.LITER -> R.string.unit_liter
                                })) },
                                onClick = {
                                    unit = unitOption
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                placeholder = { Text(stringResource(R.string.description_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.phone)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (errorMessage != null) {
                ErrorText(text = errorMessage!!)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PrimaryButton(
                text = "Add Product",
                onClick = {
                    //validate inputs
                    when {
                        imageUri == null -> errorMessage = "Please select an image"
                        name.isBlank() -> errorMessage = "Product name cannot be empty"
                        price.isBlank() -> errorMessage = "Price cannot be empty"
                        price.toDoubleOrNull() == null -> errorMessage = "Invalid price"
                        phone.isBlank() -> errorMessage = "Contact phone cannot be empty"
                        else -> {
                            isLoading = true
                            errorMessage = null
                            
                            coroutineScope.launch {
                                //get current user
                                val currentUser = firebaseAuth.currentUser
                                if (currentUser == null) {
                                    isLoading = false
                                    errorMessage = "User not authenticated"
                                    return@launch
                                }
                                
                                //get user data
                                val userResult = authRepository.getUserData(currentUser.uid)
                                if (userResult.isFailure) {
                                    isLoading = false
                                    errorMessage = "Failed to get user data"
                                    return@launch
                                }
                                
                                val user = userResult.getOrNull()!!
                                
                                try {
                                    //upload image to cloudinary
                                    val imageResult = cloudinaryRepository.uploadImage(imageUri!!)
                                    if (imageResult.isFailure) {
                                        isLoading = false
                                        val exception = imageResult.exceptionOrNull()
                                        errorMessage = "Failed to upload image: ${exception?.message}"
                                        Log.e("AddProductScreen", "Image upload failed", exception)
                                        return@launch
                                    }
                                    
                                    val imageUrl = imageResult.getOrNull()!!
                                    Log.d("AddProductScreen", "Image uploaded successfully: $imageUrl")
                                    
                                    //create product with cloudinary image URL
                                    val priceValue = price.toDoubleOrNull() ?: 0.0
                                    val product = Product(
                                        name = name,
                                        category = selectedCategory,
                                        price = priceValue,
                                        unit = unit.name,
                                        description = description,
                                        imageUrl = imageUrl, //this is now a cloudinary URL
                                        sellerId = currentUser.uid,
                                        sellerContact = phone,
                                        sellerName = user.name
                                    )
                                    
                                    //add product to firestore
                                    val addResult = productRepository.addProduct(product)
                                    isLoading = false
                                    
                                    if (addResult.isSuccess) {
                                        Utils.showToast(context, "Product added successfully")
                                        navController.navigateUp()
                                    } else {
                                        val addException = addResult.exceptionOrNull()
                                        errorMessage = "Failed to add product: ${addException?.message}"
                                        Log.e("AddProductScreen", "Failed to add product", addException)
                                    }
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMessage = "Error during product creation: ${e.message}"
                                    Log.e("AddProductScreen", "Error during product creation", e)
                                }
                            }
                        }
                    }
                },
                isLoading = isLoading,
                enabled = !isLoading
            )
        }
    }
}

// Usage in previews or other calls must pass firebaseAuth and db
// Example for preview:
//@Preview
//@Composable
//fun PreviewAddProductScreen() {
//    AddProductScreen(
//        navController = rememberNavController(),
//        firebaseAuth = FirebaseAuth.getInstance(),
//        db = FirebaseFirestore.getInstance()
//    )
//}
