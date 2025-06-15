package com.example.fmplace.model

/**
 * User data class representing all users
 */
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val language: String = "en" // Default to English
)

/**
 * Enum class representing user roles in the app
 */
enum class UserRole {
    FARMER, BUYER
}

/**
 * Extension function to get the display name for a language code
 */
fun String.getLanguageDisplayName(): String {
    return when(this) {
        "mr" -> "मराठी"
        else -> "English"
    }
}
