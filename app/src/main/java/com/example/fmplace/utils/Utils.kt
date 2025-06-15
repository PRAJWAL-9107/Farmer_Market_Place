package com.example.fmplace.utils

import android.content.Context
import android.widget.Toast

/**
 * Utility functions for the app
 */
object Utils {
    /**
     * Show a toast message
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validate password (at least 6 characters)
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    /**
     * Format price with currency symbol
     */
    fun formatPrice(price: Double): String {
        return "₹%.2f".format(price)
    }
}
