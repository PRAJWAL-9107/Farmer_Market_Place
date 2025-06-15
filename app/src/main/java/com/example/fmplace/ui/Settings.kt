package com.example.fmplace.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.model.AppLanguage
import com.example.fmplace.model.User
import com.example.fmplace.utils.LanguageManager
import com.example.fmplace.utils.LocalizationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.example.fmplace.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
    val context = LocalContext.current // Get current context
    val coroutineScope = rememberCoroutineScope() // Create coroutine scope
    val authRepository = remember { AuthRepository(
        firebaseAuth = firebaseAuth, 
        db = db 
    ) }
    
    val selectedLanguage = AppLanguage.ENGLISH // Set selected language
    
    Box( // Center the support button in the screen
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:jadhav9107@gmail.com") // Set email address for support
                }
                context.startActivity(emailIntent) // Launch email app
            },
            modifier = Modifier
                .width(180.dp)
                .height(48.dp) // Button size
        ) {
            Text(stringResource(R.string.contact_support), fontSize = 14.sp) // Support button label
        }
    }
}
