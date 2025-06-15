package com.example.fmplace.model

// Enum for supported app languages
enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ENGLISH("en", "English", "\uD83C\uDDEC\uD83C\uDDE7"); // Only English supported

    companion object {
        // Returns the language based on the provided code
        fun fromCode(code: String): AppLanguage {
            return ENGLISH // Always return English
        }

        // Returns the language based on the provided display name
        fun fromDisplayName(displayName: String): AppLanguage {
            return ENGLISH // Always return English
        }
    }
}
