package com.example.fmplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fmplace.firebase.AuthRepository
import com.example.fmplace.ui.SettingsScreen
import com.example.fmplace.ui.auth.LoginScreen
import com.example.fmplace.ui.auth.RegisterScreen
import com.example.fmplace.ui.auth.WelcomeScreen
import com.example.fmplace.ui.buyer.BuyerHomeScreen
import com.example.fmplace.ui.buyer.ProductDetailsScreen
import com.example.fmplace.ui.farmer.AddProductScreen
import com.example.fmplace.ui.farmer.FarmerDashboardScreen
import com.example.fmplace.ui.farmer.MyProductsScreen
import com.example.fmplace.ui.profile.ProfileScreen
import com.example.fmplace.ui.support.SupportScreen
import com.example.fmplace.ui.theme.FarmerMarketPlaceTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val startDestination = if (authRepository.currentUser != null) {
                if (authRepository.currentUser?.email?.contains("@farmer.com") == true) {
                    "farmer_dashboard"
                } else {
                    "buyer_home"
                }
            } else {
                "welcome"
            }

            FarmerMarketPlaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("welcome")       { WelcomeScreen(navController) }
                        composable("login")         { LoginScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }
                        composable("register")      { RegisterScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }

                        composable("farmer_dashboard") { FarmerDashboardScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }
                        composable("add_product")       { AddProductScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }
                        composable("my_products")       { MyProductsScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }

                        composable("buyer_home") { BuyerHomeScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }
                        composable("home")       { BuyerHomeScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }

                        composable(
                            "product_details/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            ProductDetailsScreen(navController, productId)
                        }

                        composable("support")  { SupportScreen(navController) }
                        composable("profile")  { ProfileScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }
                        composable("settings") { SettingsScreen(navController, firebaseAuth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance()) }
                    }
                }
            }
        }
    }
}