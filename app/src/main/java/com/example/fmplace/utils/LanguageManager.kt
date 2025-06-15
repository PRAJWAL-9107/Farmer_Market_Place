package com.example.fmplace.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

// LanguageManager: Handles language settings (only English supported)
object LanguageManager {
    private const val PREFS_NAME = "app_prefs" // SharedPreferences name
    private const val KEY_LANGUAGE = "language_code" // Key for language code

    // Get SharedPreferences instance
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) // Get SharedPreferences
    }

    // Set app language to English
    fun setAppLanguage(context: Context, languageCode: String) {
        getSharedPreferences(context)
            .edit()
            .putString(KEY_LANGUAGE, "en") // Always save 'en' for English
            .apply()
        val locales = LocaleListCompat.forLanguageTags("en") // Set app locale to English
        AppCompatDelegate.setApplicationLocales(locales)
    }

    // Get saved language (always English)
    fun getSavedLanguage(context: Context): String {
        return "en" // Always return English
    }

    // Get language display name (always English)
    fun getLanguageDisplayName(code: String): String {
        return "English" // Always return English
    }

    // Get language code from display name (always English)
    fun getLanguageCodeFromDisplayName(displayName: String): String {
        return "en" // Always return English
    }
}
