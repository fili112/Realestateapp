package com.example.realestateapp.repository

import com.example.realestateapp.model.Property
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object PropertyRepository {
    private val db = Firebase.firestore.collection("properties")

    fun getAll(): List<Property> = emptyList()

    suspend fun getAllFromFirestore(): List<Property> {
        return try {
            db.get().await().documents.mapNotNull { doc ->
                Property(
                    id          = doc.id,
                    title       = doc.getString("title") ?: "",
                    price       = doc.getString("price") ?: "",
                    priceValue  = doc.getLong("priceValue") ?: 0L,
                    location    = doc.getString("location") ?: "",
                    bedrooms    = doc.getLong("bedrooms")?.toInt() ?: 0,
                    bathrooms   = doc.getLong("bathrooms")?.toInt() ?: 0,
                    sqft        = doc.getLong("sqft")?.toInt() ?: 0,
                    description = doc.getString("description") ?: "",
                    imageUri    = doc.getString("imageUri") ?: "",
                    type        = doc.getString("type") ?: "Apartment",
                    listingType = doc.getString("listingType") ?: "Buy",
                    latitude    = doc.getDouble("latitude") ?: 9.0192,
                    longitude   = doc.getDouble("longitude") ?: 38.7525,
                    viewCount   = doc.getLong("viewCount")?.toInt() ?: 0,
                    images      = (doc.get("images") as? List<*>)
                        ?.filterIsInstance<String>()
                        ?: emptyList()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun add(property: Property): String {
        val data = hashMapOf(
            "title"       to property.title,
            "price"       to property.price,
            "priceValue"  to property.priceValue,
            "location"    to property.location,
            "bedrooms"    to property.bedrooms,
            "bathrooms"   to property.bathrooms,
            "sqft"        to property.sqft,
            "description" to property.description,
            "imageUri"    to property.imageUri,
            "type"        to property.type,
            "listingType" to property.listingType,
            "latitude"    to property.latitude,
            "longitude"   to property.longitude,
            "viewCount"   to property.viewCount,
            "images"      to property.images
        )
        // Let Firestore generate the ID, then store it inside the document too
        val docRef = db.add(data).await()
        db.document(docRef.id).update("id", docRef.id).await()
        return docRef.id
    }

    suspend fun update(updated: Property) {
        val data = hashMapOf(
            "id"          to updated.id,
            "title"       to updated.title,
            "price"       to updated.price,
            "priceValue"  to updated.priceValue,
            "location"    to updated.location,
            "bedrooms"    to updated.bedrooms,
            "bathrooms"   to updated.bathrooms,
            "sqft"        to updated.sqft,
            "description" to updated.description,
            "imageUri"    to updated.imageUri,
            "type"        to updated.type,
            "listingType" to updated.listingType,
            "latitude"    to updated.latitude,
            "longitude"   to updated.longitude,
            "viewCount"   to updated.viewCount,
            "images"      to updated.images
        )
        db.document(updated.id).set(data).await()
    }

    suspend fun delete(id: String) {
        db.document(id).delete().await()
    }
}
