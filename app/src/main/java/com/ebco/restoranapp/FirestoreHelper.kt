package com.ebco.restoranapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()
    private val restaurantsCollection = db.collection("restaurants")
    private val usersCollection = db.collection("users") 
    private val requestsCollection = db.collection("requests")

    fun uploadMockData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        restaurantsCollection.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                val batch = db.batch()
                MockData.restaurants.forEach { restaurant ->
                    val rwScore = (restaurant.hygieneScore * 0.5) + (restaurant.tasteScore * 0.25) + (restaurant.priceScore * 0.25)
                    val updatedRestaurant = restaurant.copy(rwScore = String.format("%.1f", rwScore).replace(',', '.').toDouble())
                    val docRef = restaurantsCollection.document(updatedRestaurant.id.toString())
                    batch.set(docRef, updatedRestaurant)
                }
                batch.commit().addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
            } else {
                onSuccess()
            }
        }.addOnFailureListener { onFailure(it) }
    }

    fun getRestaurants(onSuccess: (List<Restaurant>) -> Unit, onFailure: (Exception) -> Unit) {
        restaurantsCollection.orderBy("id", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { snapshot -> onSuccess(snapshot.toObjects(Restaurant::class.java)) }
            .addOnFailureListener { onFailure(it) }
    }

    fun getRestaurantById(id: String, onSuccess: (Restaurant?) -> Unit, onFailure: (Exception) -> Unit) {
        restaurantsCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) onSuccess(document.toObject(Restaurant::class.java)) else onSuccess(null)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun addReview(restaurantId: String, review: Review, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val docRef = restaurantsCollection.document(restaurantId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val restaurant = snapshot.toObject(Restaurant::class.java)
            if (restaurant != null) {
                val newReviews = restaurant.reviews.toMutableList()
                newReviews.add(0, review)
                
                // Puanları yeniden hesapla
                val newHygiene = if(newReviews.isNotEmpty()) newReviews.map { it.hygieneRating }.average() else 0.0
                val newTaste = if(newReviews.isNotEmpty()) newReviews.map { it.tasteRating }.average() else 0.0
                val newPrice = if(newReviews.isNotEmpty()) newReviews.map { it.priceRating }.average() else 0.0
                val newRwScore = (newHygiene * 0.5) + (newTaste * 0.25) + (newPrice * 0.25)
                
                transaction.update(docRef, "reviews", newReviews)
                transaction.update(docRef, "hygieneScore", newHygiene)
                transaction.update(docRef, "tasteScore", newTaste)
                transaction.update(docRef, "priceScore", newPrice)
                transaction.update(docRef, "rwScore", newRwScore)
            }
        }.addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
    }

    // YENİ: Yorum Silme
    fun deleteReview(restaurantId: String, reviewId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val docRef = restaurantsCollection.document(restaurantId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val restaurant = snapshot.toObject(Restaurant::class.java)
            if (restaurant != null) {
                val newReviews = restaurant.reviews.toMutableList()
                // İlgili yorumu listeden çıkar
                val removed = newReviews.removeIf { it.id == reviewId }
                
                if (removed) {
                    // Puanları yeniden hesapla
                    val newHygiene = if(newReviews.isNotEmpty()) newReviews.map { it.hygieneRating }.average() else 0.0
                    val newTaste = if(newReviews.isNotEmpty()) newReviews.map { it.tasteRating }.average() else 0.0
                    val newPrice = if(newReviews.isNotEmpty()) newReviews.map { it.priceRating }.average() else 0.0
                    val newRwScore = (newHygiene * 0.5) + (newTaste * 0.25) + (newPrice * 0.25)
                    
                    transaction.update(docRef, "reviews", newReviews)
                    transaction.update(docRef, "hygieneScore", newHygiene)
                    transaction.update(docRef, "tasteScore", newTaste)
                    transaction.update(docRef, "priceScore", newPrice)
                    transaction.update(docRef, "rwScore", newRwScore)
                }
            }
        }.addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
    }

    fun saveUser(uid: String, name: String, email: String, onSuccess: () -> Unit) {
        val userRef = usersCollection.document(uid)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
                val user = User(uid, name, email, date)
                userRef.set(user).addOnSuccessListener { onSuccess() }.addOnFailureListener { onSuccess() }
            } else {
                onSuccess()
            }
        }.addOnFailureListener { onSuccess() }
    }

    fun sendRequest(restaurantId: String, tableName: String, type: String, note: String = "", onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = requestsCollection.document()
        val request = CustomerRequest(
            id = newDoc.id,
            restaurantId = restaurantId,
            tableName = tableName,
            requestType = type,
            note = note,
            status = "pending",
            timestamp = System.currentTimeMillis()
        )
        
        newDoc.set(request)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}