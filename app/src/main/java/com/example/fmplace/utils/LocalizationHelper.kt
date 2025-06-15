package com.example.fmplace.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import java.util.Locale

// Helper for locale and keyboard handling (English only)
object LocalizationHelper {
    /**
     * Returns a Context with updated locale configuration.
     */
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale("en") // Always use English
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    /**
     * Shows the input method picker so user can manually select keyboard language.
     * (No-op now)
     */
    fun promptKeyboardPicker(context: Context) {
        // No-op since only English is supported
    }
}
