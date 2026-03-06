package com.ebco.restoranapp

data class Restaurant(
    val id: String = "", 
    val ownerId: String = "", 
    val name: String = "",
    val description: String = "",
    val hygieneScore: Double = 0.0,
    val tasteScore: Double = 0.0,
    val priceScore: Double = 0.0,
    val rwScore: Double = 0.0, 
    val imageResId: Int = 0, 
    val imageUrl: String = "", 
    val address: String = "",
    val city: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val tableCount: Int = 10,
    val reviews: List<Review> = emptyList() 
)

data class Review(
    val id: String = "", 
    val restaurantId: String = "",
    val userId: String = "", // YENİ: Kullanıcı ID'si (Silme yetkisi için)
    val userName: String = "",
    val hygieneRating: Int = 0,
    val tasteRating: Int = 0,
    val priceRating: Int = 0,
    val comment: String = "",
    val date: String = "",
    val isPositive: Boolean = false, 
    val ownerReply: String = ""
)

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val registerDate: String = ""
)

data class CustomerRequest(
    val id: String = "",
    val restaurantId: String = "",
    val tableName: String = "",
    val requestType: String = "",
    val note: String = "",
    val status: String = "pending",
    val timestamp: Long = 0
)