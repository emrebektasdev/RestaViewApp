# 📱 RestoranApp Projesi: Baştan Sona Geliştirme Rehberi

Bu rehber, `RestoranApp` projesinin nasıl geliştirildiğini, hangi teknolojilerin kullanıldığını ve arka plandaki mantığı yeni başlayan biri için adım adım anlatır.

---

## 1. 🛠️ Kullanılan Teknolojiler ve Araçlar

Bu projeyi geliştirirken aşağıdaki modern Android geliştirme teknolojilerini kullandık:

*   **Dil:** **Kotlin** (Android'in resmi ve modern dili).
*   **Arayüz (UI):** **XML Layouts** (Klasik tasarım yöntemi).
*   **Bağlantı:** **ViewBinding** (Kod ile tasarımdaki görünümleri güvenli bir şekilde bağlamak için `findViewById` yerine kullanılır).
*   **Veri Tabanı (Şimdilik):** **Mock Data** (Prototip aşamasında olduğumuz için verileri geçici olarak kodun içinde, `MockData` nesnesinde tutuyoruz).
*   **Resim Yükleme:** **Glide** (İnternet üzerindeki veya dosya yolundaki resimleri hızlıca göstermek için).
*   **Kimlik Doğrulama (Backend):** **Firebase Authentication** (Kullanıcıların giriş yapması ve kayıt olması için).
*   **Bulut Servisi:** **Google Cloud** (Firebase'in arka planda çalıştığı altyapı).

---

## 2. ☁️ Firebase ve Google Cloud Mantığı

En çok kafa karıştıran kısım burasıdır. "Uygulama Firebase'i nasıl tanıyor?" sorusunun cevabı:

### A. Bağlantı Nasıl Kuruldu? (Handshake)
1.  **Firebase Konsol:** `console.firebase.google.com` adresinde bir proje oluşturduk.
2.  **Paket Adı:** Android Studio'daki `build.gradle` dosyasında yazan `applicationId` ("com.ebco.restoranapp") bilgisini Firebase'e girdik. Bu, "Ben bu uygulamayım" demektir.
3.  **SHA-1 İmzası:** Google ile Giriş (Google Sign-In) özelliğinin çalışması için bilgisayarımızın dijital parmak izini (SHA-1) Firebase'e kaydettik. Bu olmadan Google girişi güvenlik hatası verir.
4.  **`google-services.json`:** Firebase bize bir kimlik kartı dosyası verdi. Bunu projenin `app/` klasörüne attık. Bu dosyanın içinde API anahtarları ve proje ID'leri gizlidir.

### B. Kod Tarafındaki Karşılığı
`build.gradle` dosyasında şu satırları ekleyerek Android Studio'ya "Bu projede Firebase kullanılacak" dedik:
```kotlin
// app/build.gradle.kts
implementation(libs.firebase.auth) // Kimlik doğrulama kütüphanesi
implementation(platform(libs.firebase.bom)) // Versiyon uyumluluğu için
```

---

## 3. 📂 Proje Yapısı (Kodlar Nereye Yazılır?)

Android Studio'da sol taraftaki dosya ağacında kaybolmamak için şu haritayı kullanabilirsin:

### 1. `manifests/AndroidManifest.xml` (Kimlik Kartı)
Uygulamanın adı, ikonu ve izinleri (İnternet izni gibi) burada tanımlanır. Ayrıca hangi sayfanın (Activity) ilk açılacağı burada belirlenir.

### 2. `java/com.ebco.restoranapp/` (Beyin Kısmı - Kotlin Kodları)
Tüm mantık buradadır. Dosyalarımız şöyle ayrılmıştır:

*   **Activity'ler (Ana Sayfalar):**
    *   `LoginActivity`: Kullanıcıyı karşılayan, giriş yaptıran ekran. Firebase kodları buradadır.
    *   `RegisterActivity`: Yeni üye kaydı yapan ekran.
    *   `MainActivity`: Girişten sonraki ana kap ("Container"). İçinde sayfalar (Fragment) değişir ama MainActivity sabit kalır.

*   **Fragment'ler (Parça Sayfalar):**
    *   `HomeFragment`: Restoran listesinin olduğu ana sayfa.
    *   `DetailFragment`: Bir restorana tıklandığında açılan detay sayfası.
    *   `ProfileFragment`: Kullanıcı profil sayfası.

*   **Adapter'lar (Veri Listeleme):**
    *   `RestaurantAdapter`: Restoran listesini (RecyclerView) tek tek satırlara dönüştüren ve ekrana çizen "araç"tır.

*   **Data (Veri):**
    *   `Models.kt`: Veri kalıplarımız (Örn: `Restaurant` diye bir nesne neye benzer? Adı, puanı, resmi vardır).
    *   `MockData.kt`: Sahte veri depomuz. Gerçek bir sunucuya bağlanmadan önce uygulamayı test etmek için elle yazdığımız restoran listesi.

### 3. `res/layout/` (Görünüm Kısmı - Tasarım)
Ekranların nasıl görüneceğini çizdiğimiz XML dosyalarıdır.
*   `activity_login.xml`: Giriş ekranı tasarımı.
*   `item_restaurant.xml`: Listedeki **tek bir** restoran kartının tasarımı.

---

## 4. 🚀 Adım Adım Nasıl Çalışıyor?

### Adım 1: Uygulama Açılışı ve Giriş (`LoginActivity`)
Uygulama açıldığında `LoginActivity` başlar.
1.  `FirebaseAuth.getInstance()` ile Firebase sistemine bir kanca atarız.
2.  `if (firebaseAuth.currentUser != null)` kontrolü yaparız. Eğer kullanıcı daha önce giriş yapmışsa, direkt Ana Sayfaya atarız.
3.  **Google Girişi:** Google butonu tıklandığında, Google'ın kendi giriş ekranını açarız. Oradan gelen "Başarılı" sonucunu (Token) alıp Firebase'e "Bak bu kullanıcı Google tarafından onaylandı, sen de içeri al" deriz (`signInWithCredential`).

### Adım 2: Ana Sayfa ve Listeleme (`MainActivity` & `HomeFragment`)
Giriş başarılı olunca `MainActivity` açılır. Bu Activity hemen `HomeFragment`ı ekrana yerleştirir.
1.  `HomeFragment`, `MockData.restaurants` listesini alır.
2.  Bu listeyi `RestaurantAdapter`'a verir.
3.  Adapter, listedeki her restoran için bir kutucuk oluşturur ve ekrana dizer.

### Adım 3: Detay ve Yorumlar
Listeden bir restorana tıklayınca:
1.  Tıklanan restoranın ID'sini alırız.
2.  `DetailFragment`ı açarız ve bu ID'ye göre restoranın detaylarını `MockData`dan bulup gösteririz.
3.  Yorum ekle dediğimizde, `MockData`daki listeye yeni bir yorum ekleriz (Uygulama kapanınca bu yeni yorum silinir çünkü sunucuya değil, geçici hafızaya yazdık).

---

## 5. 💡 Merak Edilen Kavramlar (SSS)

### 📌 Mock Data Nedir?
*   **Nedir?** Gerçek veritabanı kurulana kadar kullanılan **"Sahte Veri"**dir (Kukla Veri).
*   **Farkı:** Veritabanı kalıcıdır ve herkes görür. Mock Data geçicidir, uygulama kapanınca silinir ve sadece senin telefonunda görünür.
*   **Neden Kullandık?** Veritabanıyla uğraşmadan önce tasarımı ve listeleme mantığını (Adapter, RecyclerView) test etmek için.

### 📌 REST API & Retrofit Nedir?
*   **REST API (Garson):** Sunucunun (Backend) dış dünyayla konuşmak için koyduğu kurallardır. "Bana `GET` dersen veri veririm, `POST` dersen veri alırım" diyen standarttır.
*   **Retrofit (Müşteri):** Android tarafında çalışan, REST API ile konuşan kütüphanedir. Bizim yerimize sunucuya gider, veriyi ister, gelen JSON verisini Kotlin nesnesine çevirir.

### 📌 "Kendi Backend'ini Yazmak" Ne Demek?
*   Trendyol gibi şirketler hazır sistemler kullanmaz. Kendi sunucularını (Backend) ve bu sunucunun kurallarını (API) kendileri yazarlar.
*   **API Yazmak:** Sunucunun hangi adresten (`/urunler`) ne cevap vereceğini kodlamaktır.
*   **API Kullanmak:** Başkasının (veya kendi ekibinin) yazdığı o adrese Retrofit ile istek atmaktır.

### 📌 Kariyer İçin Hangisi: Firebase mi SQL mi?
*   **Firebase (Firestore):** Mobilciler için öğrenmesi en kolay, en hızlı sonuç veren modern çözümdür. Bu projede bunu kullanmak en mantıklısıdır.
*   **MySQL/PostgreSQL:** Büyük kurumsal şirketlerin backend tarafında kullandığı dev veritabanlarıdır. Mobil geliştirici olarak bunlara **doğrudan bağlanmazsın**, Retrofit aracılığıyla konuşursun.

---

## 6. 🎓 Yeni Başlayanlar İçin Tavsiyeler

1.  **Firebase Korkutmasın:** Firebase sadece Google'ın bizim için hazırladığı hazır bir sunucudur. Biz sadece "Giriş yap", "Veri getir" gibi komutlar yollarız.
2.  **Debug (Hata Ayıklama):** Kod yazarken `Log.d("Etiket", "Mesaj")` komutunu sık sık kullan. Logcat ekranında kodun o satıra gelip gelmediğini görürsün.
3.  **Sıradaki Adım:** Şu an veriler `MockData` içinde statik duruyor. Bir sonraki seviyede `Firebase Firestore` öğrenerek bu verileri buluta taşıyabilirsin. Böylece A kişisinin eklediği yorumu B kişisi de görebilir.

Bu proje, modern bir Android uygulamasının temel iskeletini (Login -> Liste -> Detay) oluşturur ve gerçek bir iş projesinin "prototip" halidir.
