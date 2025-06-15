package com.example.fmplace

import android.app.Application
import com.example.fmplace.firebase.FirebaseManager
import com.cloudinary.android.MediaManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.fmplace.utils.LanguageManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FarmerMarketplaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val saved = LanguageManager.getSavedLanguage(this)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(saved))
        FirebaseManager.initialize(this)
                val config = mapOf(
            "cloud_name" to "dodhcgctg",
            "api_key" to "569876324763573",
            "api_secret" to "VDK9dXvbxhAPhB6WtEaHbbzCOPg"
        )
        MediaManager.init(this, config)
    }
}
