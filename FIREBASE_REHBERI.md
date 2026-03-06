# 🔥 Firebase Veritabanı Kullanım Rehberi

Bu rehber, projede Firebase Firestore veritabanının nasıl eklendiğini, kurulduğunu ve kullanıldığını adım adım açıklar.

---

## 📋 İçindekiler

1. [Firebase Nedir?](#1-firebase-nedir)
2. [Firebase Projesi Oluşturma](#2-firebase-projesi-oluşturma)
3. [Android Projesine Firebase Ekleme](#3-android-projesine-firebase-ekleme)
4. [Veritabanı Yapısı (Firestore)](#4-veritabanı-yapısı-firestore)
5. [Kodda Firebase Kullanımı](#5-kodda-firebase-kullanımı)
6. [Pratik Örnekler](#6-pratik-örnekler)

---

## 1. Firebase Nedir?

**Firebase**, Google'ın sunduğu hazır bir "Bulut Sunucu" hizmetidir. Sen kendi sunucunu kurmakla uğraşmadan, Firebase üzerinden:
- ✅ Kullanıcı girişi (Authentication)
- ✅ Veritabanı (Firestore)
- ✅ Dosya depolama (Storage)
- ✅ Anlık bildirimler (Cloud Messaging)

gibi özellikleri kullanabilirsin.

**Firestore** ise Firebase'in veritabanı sistemidir. Klasik SQL veritabanlarından farklı olarak **NoSQL** (doküman tabanlı) bir yapıdır.

---

## 2. Firebase Projesi Oluşturma

### Adım 1: Firebase Konsoluna Git
1. `https://console.firebase.google.com` adresine git
2. Google hesabınla giriş yap
3. "Proje Ekle" butonuna tıkla

### Adım 2: Proje Bilgilerini Gir
- **Proje Adı:** `restoranapp-c16f7` (örnek)
- **Google Analytics:** İstersen açabilirsin (opsiyonel)

### Adım 3: Android Uygulamasını Bağla
1. Firebase konsolunda "Android" ikonuna tıkla
2. **Paket Adı (Package Name):** `com.ebco.restoranapp` (Android Studio'daki `build.gradle.kts` dosyasındaki `applicationId` ile aynı olmalı)
3. **Uygulama Takma Adı:** İstediğin bir isim (örn: "RestoranApp")
4. **SHA-1 İmzası:** Google Sign-In için gerekli (Android Studio'dan alabilirsin)

### Adım 4: `google-services.json` Dosyasını İndir
- Firebase konsol, sana bir `google-services.json` dosyası verir
- Bu dosyayı **Android Studio projenin `app/` klasörüne** kopyala

**⚠️ ÖNEMLİ:** Bu dosya, uygulamanın Firebase ile konuşması için gerekli "kimlik kartı"dır. İçinde API anahtarları ve proje bilgileri vardır.

---

## 3. Android Projesine Firebase Ekleme

### 3.1. Root `build.gradle.kts` Dosyasına Plugin Ekle

**Dosya:** `build.gradle.kts` (proje kök dizininde)

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false  // ✅ Firebase plugin'i
}
```

### 3.2. App Modülü `build.gradle.kts` Dosyasına Ekle

**Dosya:** `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")  // ✅ Firebase plugin'i aktif et
}

dependencies {
    // ... diğer bağımlılıklar ...
    
    // ✅ Firebase Bağımlılıkları
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))  // Versiyon yönetimi
    implementation("com.google.firebase:firebase-auth")                 // Kullanıcı girişi
    implementation("com.google.firebase:firebase-firestore")             // Veritabanı
}
```

**Açıklama:**
- `firebase-bom`: Tüm Firebase kütüphanelerinin aynı versiyonda olmasını sağlar
- `firebase-auth`: Kullanıcı girişi için
- `firebase-firestore`: Veritabanı işlemleri için

### 3.3. `google-services.json` Dosyasını Yerleştir

Dosyayı `app/google-services.json` konumuna koy. Android Studio otomatik olarak tanır.

---

## 4. Veritabanı Yapısı (Firestore)

Firestore, **Koleksiyonlar (Collections)** ve **Dokümanlar (Documents)** yapısıyla çalışır.

### Yapı Örneği:

```
Firestore
├── restaurants (Koleksiyon)
│   ├── "1" (Doküman ID)
│   │   ├── id: "1"
│   │   ├── name: "Nusr-Et Steakhouse"
│   │   ├── description: "Dünyaca ünlü et restoranı."
│   │   ├── hygieneScore: 4.9
│   │   ├── reviews: [ ... ] (Liste)
│   │   └── ...
│   ├── "2"
│   │   └── ...
│   └── ...
│
├── users (Koleksiyon)
│   ├── "user123" (Kullanıcı ID'si)
│   │   ├── uid: "user123"
│   │   ├── name: "Ahmet Yılmaz"
│   │   ├── email: "ahmet@example.com"
│   │   └── registerDate: "10.11.2023 14:30"
│   └── ...
│
└── requests (Koleksiyon)
    ├── "request456" (Otomatik ID)
    │   ├── id: "request456"
    │   ├── restaurantId: "1"
    │   ├── tableName: "Masa 5"
    │   ├── requestType: "Garson"
    │   ├── status: "pending"
    │   └── timestamp: 1699624800000
    └── ...
```

**Önemli Noktalar:**
- Her **Koleksiyon** bir tablo gibi düşünülebilir
- Her **Doküman** bir satır gibi düşünülebilir
- Dokümanlar içinde **Liste**, **Map**, **String**, **Number** gibi veri tipleri saklanabilir

---

## 5. Kodda Firebase Kullanımı

### 5.1. Firestore Bağlantısı Kurma

**Dosya:** `FirestoreHelper.kt`

```kotlin
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {
    // ✅ Firestore veritabanına bağlan
    private val db = FirebaseFirestore.getInstance()
    
    // ✅ Koleksiyonlara referans al
    private val restaurantsCollection = db.collection("restaurants")
    private val usersCollection = db.collection("users")
    private val requestsCollection = db.collection("requests")
}
```

**Açıklama:**
- `FirebaseFirestore.getInstance()`: Firebase'e bağlanır (tek bir örnek oluşturur - Singleton Pattern)
- `db.collection("restaurants")`: "restaurants" koleksiyonuna referans alır

---

### 5.2. Veri Okuma (READ)

#### Örnek 1: Tüm Restoranları Getir

```kotlin
fun getRestaurants(onSuccess: (List<Restaurant>) -> Unit, onFailure: (Exception) -> Unit) {
    restaurantsCollection
        .orderBy("id", Query.Direction.ASCENDING)  // ID'ye göre sırala
        .get()  // Verileri al
        .addOnSuccessListener { snapshot ->  // ✅ Başarılı olursa
            val restaurants = snapshot.toObjects(Restaurant::class.java)  // Kotlin nesnesine çevir
            onSuccess(restaurants)
        }
        .addOnFailureListener { exception ->  // ❌ Hata olursa
            onFailure(exception)
        }
}
```

**Kullanım:**
```kotlin
val helper = FirestoreHelper()
helper.getRestaurants(
    onSuccess = { restaurants ->
        // restaurants listesini kullan
        println("Toplam ${restaurants.size} restoran bulundu")
    },
    onFailure = { error ->
        println("Hata: ${error.message}")
    }
)
```

#### Örnek 2: Tek Bir Restoranı ID ile Getir

```kotlin
fun getRestaurantById(id: String, onSuccess: (Restaurant?) -> Unit, onFailure: (Exception) -> Unit) {
    restaurantsCollection
        .document(id)  // Belirli bir dokümana git
        .get()  // Veriyi al
        .addOnSuccessListener { document ->
            if (document.exists()) {  // Doküman varsa
                val restaurant = document.toObject(Restaurant::class.java)
                onSuccess(restaurant)
            } else {
                onSuccess(null)  // Doküman yoksa
            }
        }
        .addOnFailureListener { onFailure(it) }
}
```

---

### 5.3. Veri Ekleme (CREATE)

#### Örnek 1: Yeni Restoran Ekle

```kotlin
fun addRestaurant(restaurant: Restaurant, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val docId = "restoran-adi-sehir-benzersizkod"  // Benzersiz ID oluştur
    val newDoc = restaurantsCollection.document(docId)  // Yeni doküman referansı
    
    newDoc.set(restaurant)  // Veriyi kaydet
        .addOnSuccessListener { 
            onSuccess()  // ✅ Başarılı
        }
        .addOnFailureListener { exception ->
            onFailure(exception)  // ❌ Hata
        }
}
```

#### Örnek 2: Otomatik ID ile İstek Gönderme

```kotlin
fun sendRequest(restaurantId: String, tableName: String, type: String, note: String = "", 
                onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    // ✅ Firebase otomatik benzersiz ID oluşturur
    val newDoc = requestsCollection.document()
    
    val request = CustomerRequest(
        id = newDoc.id,  // Firebase'in verdiği ID
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
```

**Kullanım:**
```kotlin
helper.sendRequest(
    restaurantId = "1",
    tableName = "Masa 5",
    type = "Garson",
    note = "Ketçap istiyorum",
    onSuccess = {
        Toast.makeText(context, "İstek gönderildi!", Toast.LENGTH_SHORT).show()
    },
    onFailure = { error ->
        Toast.makeText(context, "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
    }
)
```

---

### 5.4. Veri Güncelleme (UPDATE)

#### Örnek: Yorum Ekleme ve Puanları Güncelleme

```kotlin
fun addReview(restaurantId: String, review: Review, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val docRef = restaurantsCollection.document(restaurantId)
    
    // ✅ Transaction kullan (Aynı anda birden fazla işlem yapılacaksa)
    db.runTransaction { transaction ->
        // 1. Mevcut restoranı al
        val snapshot = transaction.get(docRef)
        val restaurant = snapshot.toObject(Restaurant::class.java)
        
        if (restaurant != null) {
            // 2. Yeni yorumu listeye ekle
            val newReviews = restaurant.reviews.toMutableList()
            newReviews.add(0, review)  // En başa ekle
            
            // 3. Puanları yeniden hesapla
            val newHygiene = newReviews.map { it.hygieneRating }.average()
            val newTaste = newReviews.map { it.tasteRating }.average()
            val newPrice = newReviews.map { it.priceRating }.average()
            val newRwScore = (newHygiene * 0.5) + (newTaste * 0.25) + (newPrice * 0.25)
            
            // 4. Güncellemeleri yap
            transaction.update(docRef, "reviews", newReviews)
            transaction.update(docRef, "hygieneScore", newHygiene)
            transaction.update(docRef, "tasteScore", newTaste)
            transaction.update(docRef, "priceScore", newPrice)
            transaction.update(docRef, "rwScore", newRwScore)
        }
    }
    .addOnSuccessListener { onSuccess() }
    .addOnFailureListener { onFailure(it) }
}
```

**Açıklama:**
- `runTransaction`: Birden fazla işlemi atomik (ya hepsi başarılı ya da hiçbiri) yapar
- `transaction.update()`: Belirli bir alanı günceller

---

### 5.5. Veri Silme (DELETE)

#### Örnek: Yorum Silme

```kotlin
fun deleteReview(restaurantId: String, reviewId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val docRef = restaurantsCollection.document(restaurantId)
    
    db.runTransaction { transaction ->
        val snapshot = transaction.get(docRef)
        val restaurant = snapshot.toObject(Restaurant::class.java)
        
        if (restaurant != null) {
            val newReviews = restaurant.reviews.toMutableList()
            newReviews.removeIf { it.id == reviewId }  // ✅ Yorumu listeden çıkar
            
            // Puanları yeniden hesapla ve güncelle
            // ... (yukarıdaki gibi)
        }
    }
    .addOnSuccessListener { onSuccess() }
    .addOnFailureListener { onFailure(it) }
}
```

---

### 5.6. Canlı Dinleme (Real-time Listener)

**ÖNEMLİ:** Bu özellik, veritabanında değişiklik olduğunda anında bildirim almanı sağlar.

#### Örnek: Garson İsteklerini Canlı Dinleme

```kotlin
fun listenForRequests(restaurantId: String, onUpdate: (List<CustomerRequest>) -> Unit): ListenerRegistration {
    return requestsCollection
        .whereEqualTo("restaurantId", restaurantId)  // Filtrele: Sadece bu restoranın istekleri
        .whereEqualTo("status", "pending")  // Filtrele: Sadece bekleyenler
        .addSnapshotListener { snapshot, error ->  // ✅ Canlı dinleme başlat
            if (error != null) {
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val requests = snapshot.toObjects(CustomerRequest::class.java)
                val sortedRequests = requests.sortedByDescending { it.timestamp }  // En yeni önce
                onUpdate(sortedRequests)  // ✅ Her değişiklikte bu fonksiyon çağrılır
            }
        }
}
```

**Kullanım:**
```kotlin
// Activity veya Fragment'te
private var listenerRegistration: ListenerRegistration? = null

override fun onResume() {
    super.onResume()
    val helper = RestaViewFirestoreHelper()
    
    // ✅ Dinlemeyi başlat
    listenerRegistration = helper.listenForRequests(
        restaurantId = "1",
        onUpdate = { requests ->
            // Her yeni istek geldiğinde buraya düşer
            adapter.updateRequests(requests)
            showNotification("Yeni istek geldi!")
        }
    )
}

override fun onPause() {
    super.onPause()
    // ✅ Dinlemeyi durdur (Bellek sızıntısını önlemek için)
    listenerRegistration?.remove()
}
```

**⚠️ ÖNEMLİ:** `ListenerRegistration`'ı `onPause()` veya `onDestroy()`'da mutlaka kaldır, yoksa bellek sızıntısı olur!

---

## 6. Pratik Örnekler

### Örnek 1: Müşteri Uygulamasında Restoran Listesi Gösterme

**Dosya:** `HomeFragment.kt`

```kotlin
class HomeFragment : Fragment() {
    private val firestoreHelper = FirestoreHelper()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRestaurants()
    }
    
    private fun loadRestaurants() {
        firestoreHelper.getRestaurants(
            onSuccess = { restaurants ->
                // RecyclerView'a ver
                adapter.submitList(restaurants)
            },
            onFailure = { error ->
                Toast.makeText(context, "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
```

### Örnek 2: Garson Çağırma İsteği Gönderme

**Dosya:** `DetailFragment.kt`

```kotlin
binding.btnCallWaiter.setOnClickListener {
    firestoreHelper.sendRequest(
        restaurantId = restaurantId,
        tableName = "Masa 5",
        type = "Garson",
        note = "",
        onSuccess = {
            Toast.makeText(context, "Garson çağrıldı!", Toast.LENGTH_SHORT).show()
        },
        onFailure = { error ->
            Toast.makeText(context, "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    )
}
```

### Örnek 3: Restoran Yönetim Uygulamasında Canlı İstek Listesi

**Dosya:** `ManageRestaurantActivity.kt`

```kotlin
class ManageRestaurantActivity : AppCompatActivity() {
    private val helper = RestaViewFirestoreHelper()
    private var listenerRegistration: ListenerRegistration? = null
    
    override fun onResume() {
        super.onResume()
        
        // ✅ Canlı dinlemeyi başlat
        listenerRegistration = helper.listenForRequests(
            restaurantId = restaurantId,
            onUpdate = { requests ->
                // Her yeni istek geldiğinde adapter güncellenir
                adapter.updateRequests(requests)
                
                // Bildirim göster
                if (requests.isNotEmpty()) {
                    showNotification("${requests.size} bekleyen istek var!")
                }
            }
        )
    }
    
    override fun onPause() {
        super.onPause()
        // ✅ Dinlemeyi durdur
        listenerRegistration?.remove()
    }
}
```

---

## 🎯 Özet: Firebase Kullanım Adımları

1. **Firebase Konsol:** Proje oluştur, Android uygulamasını bağla
2. **`google-services.json`:** Dosyayı `app/` klasörüne koy
3. **`build.gradle.kts`:** Firebase bağımlılıklarını ekle
4. **Kod:** `FirebaseFirestore.getInstance()` ile bağlan
5. **CRUD İşlemleri:**
   - **Create:** `.set()` veya `.add()`
   - **Read:** `.get()` veya `.addSnapshotListener()`
   - **Update:** `.update()` veya Transaction içinde
   - **Delete:** `.delete()` veya listeden çıkar

---

## ⚠️ Önemli Notlar

1. **İnternet Bağlantısı:** Firebase çalışması için internet gerekir
2. **Güvenlik Kuralları:** Firebase konsolunda Firestore Rules'u ayarla (varsayılan olarak herkes okuyup yazabilir - production'da değiştir!)
3. **Bellek Yönetimi:** `addSnapshotListener()` kullandığında mutlaka `remove()` çağır
4. **Hata Yönetimi:** Her Firebase işleminde `onFailure` callback'ini mutlaka kullan

---

## 📚 Ek Kaynaklar

- [Firebase Dokümantasyonu](https://firebase.google.com/docs)
- [Firestore Rehberi](https://firebase.google.com/docs/firestore)
- [Android Firebase Örnekleri](https://github.com/firebase/snippets-android)

---

**Soruların varsa çekinme, sorabilirsin!** 🚀


