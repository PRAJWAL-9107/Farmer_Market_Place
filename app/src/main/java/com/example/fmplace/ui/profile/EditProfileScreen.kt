package com.example.fmplace.ui.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fmplace.R
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.model.User
import com.example.fmplace.storage.CloudinaryRepository
import com.example.fmplace.ui.common.ErrorText
import com.example.fmplace.ui.common.PrimaryButton
import com.example.fmplace.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = AuthRepository(
        firebaseAuth = firebaseAuth,
        db = db
    )
    val cloudinaryRepository = remember { CloudinaryRepository(context) }
    
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isUpdatingProfile by remember { mutableStateOf(false) }
    
    // Form fields
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }
    
    // Original values for change detection
    var originalName by remember { mutableStateOf("") }
    var originalPhone by remember { mutableStateOf("") }
    var originalEmail by remember { mutableStateOf("") }
    var originalProfilePictureUrl by remember { mutableStateOf("") }
    
    // Check if any field has been changed
    val hasChanges = remember(name, phone, email, profilePictureUrl) {
        name != originalName || phone != originalPhone || email != originalEmail || profilePictureUrl != originalProfilePictureUrl
    }
    
    // Profile picture upload state
    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    
    // Function to upload profile picture
    suspend fun uploadProfilePicture(imageUri: android.net.Uri) {
        try {
            isUpdatingProfile = true
            val uploadResult = cloudinaryRepository.uploadImage(imageUri)
            if (uploadResult.isSuccess) {
                val imageUrl = uploadResult.getOrNull()
                imageUrl?.let { url ->
                    profilePictureUrl = url
                }
            } else {
                errorMessage = "Failed to upload profile picture"
            }
        } catch (e: Exception) {
            errorMessage = "Error uploading profile picture: ${e.message}"
        } finally {
            isUpdatingProfile = false
        }
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        imageUri = uri
        // Upload new profile picture
        uri?.let { 
            coroutineScope.launch {
                uploadProfilePicture(it)
            }
        }
    }
    
    // Load user data
    LaunchedEffect(Unit) {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val result = authRepository.getUserData(currentUser.uid ?: "")
                if (result.isSuccess) {
                    val userData = result.getOrNull()
                    userData?.let {
                        user = it
                        name = it.name
                        phone = it.phone
                        email = it.email
                        profilePictureUrl = it.profilePictureUrl
                        
                        // Set original values for change detection
                        originalName = it.name
                        originalPhone = it.phone
                        originalEmail = it.email
                        originalProfilePictureUrl = it.profilePictureUrl
                    }
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load user data"
                }
            } else {
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
    
    // Function to update profile
    suspend fun updateProfile() {
        try {
            isUpdatingProfile = true
            errorMessage = null
            
            user?.let { currentUser ->
                val updatedUser = currentUser.copy(
                    name = name,
                    phone = phone,
                    email = email,
                    profilePictureUrl = profilePictureUrl
                )
                
                val updateResult = authRepository.updateUserData(currentUser.id, updatedUser)
                if (updateResult.isSuccess) {
                    user = updatedUser
                    Utils.showToast(context, "Profile updated successfully")
                    navController.navigateUp()
                } else {
                    errorMessage = "Failed to update profile"
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error updating profile: ${e.message}"
        } finally {
            isUpdatingProfile = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Show save button only when there are changes
                    if (hasChanges) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    updateProfile()
                                }
                            },
                            enabled = !isUpdatingProfile && name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()
                        ) {
                            if (isUpdatingProfile) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Filled.Edit, contentDescription = "Save Profile")
                            }
                        }
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Profile Picture Section
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { launcher.launch("image/*") }
                    ) {
                        AsyncImage(
                            model = if (profilePictureUrl.isNotBlank()) profilePictureUrl else null,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Camera icon overlay
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = "Change Profile Picture",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Phone Field
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Error message
                    if (errorMessage != null) {
                        ErrorText(text = errorMessage!!)
                    }
                    
                    // Save button at bottom (optional, since save is in top bar)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (hasChanges) 
                            "💡 Tap the edit icon in the top-right corner to save your changes" 
                        else 
                            "💡 Make changes to your profile to see the save option",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
