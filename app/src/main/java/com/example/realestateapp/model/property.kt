package com.example.realestateapp.model

data class Property(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val priceValue: Long = 0L,
    val location: String = "",
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val sqft: Int = 0,
    val description: String = "",
    val imageRes: Int = 0,
    val imageUri: String = "",
    val type: String = "Apartment",
    val images: List<String> = emptyList(),
    val latitude: Double = 9.0192,
    val longitude: Double = 38.7525,
    val listingType: String = "Buy",
    val viewCount: Int = 0
)
