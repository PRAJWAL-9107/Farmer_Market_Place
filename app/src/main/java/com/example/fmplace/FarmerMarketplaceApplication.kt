package com.example.fmplace

import android.app.Application
import com.example.fmplace.firebase.FirebaseManager
import com.cloudinary.android.MediaManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.fmplace.utils.LanguageManager
import com.bugsee.library.Bugsee
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FarmerMarketplaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val saved = LanguageManager.getSavedLanguage(this)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(saved))
        FirebaseManager.initialize(this)
        
        // Initialize Bugsee for crash reporting and analytics
        // Example: Bugsee.launch(this, "abc123def456-ghi789-jkl012")
        Bugsee.launch(this, "YOUR_BUGSEE_API_TOKEN")
        
        val config = mapOf(
            "cloud_name" to "dodhcgctg",
            "api_key" to "569876324763573",
            "api_secret" to "VDK9dXvbxhAPhB6WtEaHbbzCOPg"
        )
        MediaManager.init(this, config)
    }
}
