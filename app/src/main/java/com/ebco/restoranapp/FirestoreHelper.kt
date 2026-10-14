package com.ebco.restoranapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.android.gms.tasks.Tasks
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()
    private val restaurantsCollection = db.collection("restaurants")
    private val usersCollection = db.collection("users")
    private val requestsCollection = db.collection("requests")

    // Production clients never seed or overwrite shared data.
    fun uploadMockData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) { onSuccess() }

    private fun withReviews(restaurant: Restaurant, reviews: List<Review>): Restaurant {
        val hygiene = if (reviews.isEmpty()) 0.0 else reviews.map { it.hygieneRating }.average()
        val taste = if (reviews.isEmpty()) 0.0 else reviews.map { it.tasteRating }.average()
        val price = if (reviews.isEmpty()) 0.0 else reviews.map { it.priceRating }.average()
        return restaurant.copy(reviews = reviews, hygieneScore = hygiene, tasteScore = taste,
            priceScore = price, rwScore = hygiene * 0.5 + taste * 0.25 + price * 0.25)
    }

    fun getRestaurants(onSuccess: (List<Restaurant>) -> Unit, onFailure: (Exception) -> Unit) {
        restaurantsCollection.orderBy("id", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.documents.mapNotNull { doc ->
                    val restaurant = doc.toObject(Restaurant::class.java) ?: return@mapNotNull null
                    doc.reference.collection("reviews").get().continueWith { task ->
                        val reviews = task.result!!.toObjects(Review::class.java)
                        val legacy = if (doc.getBoolean("reviewsMigrated") == true) emptyList() else restaurant.reviews
                        withReviews(restaurant, reviews + legacy.filter { old -> reviews.none { it.id == old.id } })
                    }
                }
                Tasks.whenAllSuccess<Restaurant>(tasks).addOnSuccessListener(onSuccess).addOnFailureListener(onFailure)
            }.addOnFailureListener(onFailure)
    }

    fun getRestaurantById(id: String, onSuccess: (Restaurant?) -> Unit, onFailure: (Exception) -> Unit) {
        restaurantsCollection.document(id).get().addOnSuccessListener { doc ->
            val restaurant = doc.toObject(Restaurant::class.java)
            if (restaurant == null) { onSuccess(null); return@addOnSuccessListener }
            doc.reference.collection("reviews").get().addOnSuccessListener { snapshot ->
                val reviews = snapshot.toObjects(Review::class.java)
                val legacy = if (doc.getBoolean("reviewsMigrated") == true) emptyList() else restaurant.reviews
                onSuccess(withReviews(restaurant, reviews + legacy.filter { old -> reviews.none { it.id == old.id } }))
            }.addOnFailureListener(onFailure)
        }.addOnFailureListener(onFailure)
    }

    fun addReview(restaurantId: String, review: Review, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return onFailure(IllegalStateException("Yorum yapmak için giriş yapın."))
        val doc = restaurantsCollection.document(restaurantId).collection("reviews").document()
        doc.set(review.copy(id = doc.id, restaurantId = restaurantId, userId = uid, ownerReply = ""))
            .addOnSuccessListener { onSuccess() }.addOnFailureListener(onFailure)
    }

    fun deleteReview(restaurantId: String, reviewId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        restaurantsCollection.document(restaurantId).collection("reviews").document(reviewId).delete()
            .addOnSuccessListener { onSuccess() }.addOnFailureListener(onFailure)
    }

    fun saveUser(uid: String, name: String, email: String, onSuccess: () -> Unit) {
        val userRef = usersCollection.document(uid)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
                userRef.set(User(uid, name, email, date)).addOnSuccessListener { onSuccess() }
            } else { onSuccess() }
        }
    }

    fun sendRequest(restaurantId: String, tableName: String, type: String, note: String = "",
                    onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return onFailure(IllegalStateException("İstek göndermek için giriş yapın."))
        val doc = requestsCollection.document()
        val request = CustomerRequest(id = doc.id, userId = uid, restaurantId = restaurantId,
            tableName = tableName, requestType = type, note = note, status = "pending",
            timestamp = System.currentTimeMillis())
        doc.set(request).addOnSuccessListener { onSuccess() }.addOnFailureListener(onFailure)
    }
}
