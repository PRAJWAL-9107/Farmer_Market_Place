package com.example.fmplace.domain.model

import com.example.fmplace.model.AppLanguage

data class User(
    val id: String = "",
    val name: String,
    val email: String,
    val role: String, // "farmer" or "buyer"
    val phoneNumber: String? = null,
    val address: String? = null,
    val profileImageUrl: String? = null,
    val language: String = AppLanguage.ENGLISH.code // Store language as String for Firestore compatibility
)
