package com.example.fmplace.domain.model

data class Product(
    val id: String = "",
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val category: String,
    val imageUrl: String,
    val farmerId: String,
    val timestamp: Long = System.currentTimeMillis()
)
