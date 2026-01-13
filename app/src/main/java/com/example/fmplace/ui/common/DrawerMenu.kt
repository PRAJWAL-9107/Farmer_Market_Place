package com.example.fmplace.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fmplace.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenu(
    drawerState: DrawerState?,
    currentUser: User?,
    navController: NavController,
    onLogout: () -> Unit,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Farmer Marketplace",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                currentUser?.let {
                    Text(
                        text = it.name,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    
                    Text(
                        text = it.email,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Menu items
        DrawerMenuItem(
            icon = Icons.Filled.Person,
            title = "Profile",
            onClick = {
                coroutineScope.launch {
                    drawerState?.close()
                    navController.navigate("profile")
                }
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Filled.ShoppingCart,
            title = "Browse Products",
            onClick = {
                coroutineScope.launch {
                    drawerState?.close()
                    navController.navigate("buyer_home")
                }
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Filled.Add,
            title = "Sell Products",
            onClick = {
                coroutineScope.launch {
                    drawerState?.close()
                    navController.navigate("farmer_dashboard")
                }
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Filled.Info,
            title = "Support",
            onClick = {
                coroutineScope.launch {
                    drawerState?.close()
                    navController.navigate("support")
                }
            }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Settings option
        DrawerMenuItem(
            icon = Icons.Filled.Settings,
            title = "Settings",
            onClick = {
                coroutineScope.launch {
                    drawerState?.close()
                    navController.navigate("settings")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Logout button
        DrawerMenuItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Logout",
            onClick = {
                coroutineScope.launch {
                    drawerState?.close()
                    onLogout()
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(32.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
