package com.example.fmplace.model

/**
 * Product data class representing products added by farmers
 */
data class Product(
    val id: String = "",
    val name: String = "",
    val category: ProductCategory = ProductCategory.OTHER,
    val price: Double = 0.0,
    val unit: String = "kg",
    val description: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val sellerContact: String = "",
    val sellerName: String = "",
    val sellerProfilePictureUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Product {
            val id = map["id"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val category = ProductCategory.valueOf(map["category"] as? String ?: ProductCategory.OTHER.name)
            val price = (map["price"] as? Double ?: 0.0)
            val unit = map["unit"] as? String ?: "kg"
            val description = map["description"] as? String ?: ""
            val imageUrl = map["imageUrl"] as? String ?: ""
            val sellerId = map["sellerId"] as? String ?: ""
            val sellerContact = map["sellerContact"] as? String ?: ""
            val sellerName = map["sellerName"] as? String ?: ""
            val sellerProfilePictureUrl = map["sellerProfilePictureUrl"] as? String ?: ""
            val createdAt = (map["createdAt"] as? Long ?: System.currentTimeMillis())
            
            return Product(
                id = id,
                name = name,
                category = category,
                price = price,
                unit = unit,
                description = description,
                imageUrl = imageUrl,
                sellerId = sellerId,
                sellerContact = sellerContact,
                sellerName = sellerName,
                sellerProfilePictureUrl = sellerProfilePictureUrl,
                createdAt = createdAt
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "category" to category.name,
            "price" to price,
            "unit" to unit,
            "description" to description,
            "imageUrl" to imageUrl,
            "sellerId" to sellerId,
            "sellerContact" to sellerContact,
            "sellerName" to sellerName,
            "sellerProfilePictureUrl" to sellerProfilePictureUrl,
            "createdAt" to createdAt
        )
    }
}

/**
 * Enum class representing product categories
 */
enum class ProductCategory {
    VEGETABLES, FRUITS, DAIRY, GRAINS, OTHER
}
