# 🗄️ Firebase Veritabanı Yapısı ve İki Uygulama Arasındaki Bağlantılar

Bu dokümantasyon, Firebase Firestore veritabanındaki koleksiyonları, bunların ne işe yaradığını ve iki uygulama (Müşteri ve Restoran Yönetim) arasında nasıl bağlantılı olduğunu açıklar.

---

## 📊 Genel Bakış: Firebase Veritabanı Mantığı

Firebase Firestore, **NoSQL** (doküman tabanlı) bir veritabanıdır. Klasik SQL veritabanlarından farklı olarak:

- **Koleksiyonlar (Collections)** = Tablolar gibi düşünülebilir
- **Dokümanlar (Documents)** = Tablodaki satırlar gibi düşünülebilir
- **Alanlar (Fields)** = Kolonlar gibi düşünülebilir

**Önemli Fark:** Firestore'da bir dokümanın içinde **liste**, **map**, **nested object** gibi karmaşık veri yapıları saklanabilir. Örneğin bir restoran dokümanının içinde, o restorana ait tüm yorumlar bir liste olarak saklanabilir.

---

## 🏗️ Veritabanı Yapısı: Koleksiyonlar ve İlişkiler

Projede toplam **4 ana koleksiyon** kullanılıyor:

### 1. 📍 `restaurants` Koleksiyonu
**Ne İşe Yarar:** Tüm restoranların bilgilerini saklar.

**Hangi Uygulamalar Kullanıyor:**
- ✅ **Müşteri Uygulaması (RestoranApp):** Restoranları listeler, detaylarını gösterir
- ✅ **Restoran Yönetim Uygulaması (RestaView):** Restoran ekler, düzenler, yorumlara cevap verir

**İçinde Neler Var:**
- Restoran adı, açıklama, adres, şehir
- Puanlar (Hijyen, Lezzet, Fiyat, RW Puanı)
- Konum bilgileri (Enlem, Boylam)
- Resim URL'si
- **Yorumlar listesi** (Reviews) - Bu restorana yapılan tüm yorumlar burada
- Restoran sahibinin ID'si (ownerId)
- Masa sayısı

**Önemli Özellik:**
- Yorumlar, restoran dokümanının **içinde** bir liste olarak saklanır
- Bu sayede bir restoranı çektiğinde, yorumları da birlikte gelir
- Yeni yorum eklendiğinde, restoran dokümanı **güncellenir** (yeni yorum listeye eklenir)

**İki Uygulama Arasındaki Bağlantı:**
- Müşteri uygulaması → Restoranları **okur** (liste, detay)
- Müşteri uygulaması → Yorum **ekler** (restoran dokümanını günceller)
- Restoran uygulaması → Restoran **ekler/düzenler**
- Restoran uygulaması → Yorumlara **cevap verir** (restoran dokümanını günceller)

---

### 2. 👥 `users` Koleksiyonu
**Ne İşe Yarar:** Müşteri kullanıcılarının bilgilerini saklar.

**Hangi Uygulamalar Kullanıyor:**
- ✅ **Müşteri Uygulaması (RestoranApp):** Kullanıcı kaydı yapar, profil bilgilerini saklar
- ❌ **Restoran Yönetim Uygulaması:** Kullanmıyor

**İçinde Neler Var:**
- Kullanıcı ID'si (uid) - Firebase Authentication'dan gelir
- İsim, e-posta
- Kayıt tarihi

**Önemli Özellik:**
- Her kullanıcı, kendi ID'si ile doküman olarak saklanır
- İlk giriş yapıldığında otomatik oluşturulur
- Yorum yaparken, kullanıcının ID'si yorumun içine kaydedilir (kim yorum yaptığını bilmek için)

**İki Uygulama Arasındaki Bağlantı:**
- Sadece müşteri uygulaması kullanır
- Restoran uygulaması ile doğrudan bağlantısı yok

---

### 3. 🛎️ `requests` Koleksiyonu
**Ne İşe Yarar:** Müşterilerden gelen istekleri (Garson çağır, Hesap iste, Özel istek) saklar.

**Hangi Uygulamalar Kullanıyor:**
- ✅ **Müşteri Uygulaması (RestoranApp):** İstek **gönderir** (yeni doküman oluşturur)
- ✅ **Restoran Yönetim Uygulaması (RestaView):** İstekleri **dinler** (canlı takip), tamamlar

**İçinde Neler Var:**
- İstek ID'si (otomatik oluşturulur)
- Restoran ID'si (hangi restorana gönderildi)
- Masa adı (hangi masadan gönderildi)
- İstek tipi ("Garson", "Hesap", "Özel İstek")
- Not (varsa)
- Durum ("pending" = bekliyor, "completed" = tamamlandı)
- Zaman damgası (ne zaman gönderildi)

**Önemli Özellik:**
- Her istek, **ayrı bir doküman** olarak saklanır
- Restoran uygulaması, bu koleksiyonu **canlı dinler** (Snapshot Listener)
- Yeni istek geldiğinde, restoran uygulaması anında bildirim alır

**İki Uygulama Arasındaki Bağlantı:**
- **Müşteri Uygulaması → Restoran Uygulaması:** Tek yönlü iletişim
  1. Müşteri "Garson Çağır" butonuna basar
  2. Yeni bir istek dokümanı `requests` koleksiyonuna eklenir
  3. Restoran uygulaması canlı dinleme sayesinde anında görür
  4. Restoran uygulaması isteği "tamamlandı" olarak işaretler

**Canlı Dinleme Mantığı:**
- Restoran uygulaması, `requests` koleksiyonunu sürekli dinler
- Filtre: Sadece kendi restoranının (`restaurantId`) ve bekleyen (`status: "pending"`) istekleri
- Yeni istek geldiğinde, Firebase otomatik olarak restoran uygulamasına bildirim gönderir
- Uygulama ekranı anında güncellenir (yeni istek listeye eklenir)

---

### 4. 👨‍🍳 `owners` Koleksiyonu
**Ne İşe Yarar:** Restoran sahiplerinin bilgilerini saklar.

**Hangi Uygulamalar Kullanıyor:**
- ❌ **Müşteri Uygulaması:** Kullanmıyor
- ✅ **Restoran Yönetim Uygulaması (RestaView):** Restoran sahibi kaydı yapar, profil bilgilerini saklar

**İçinde Neler Var:**
- Sahip ID'si (uid) - Firebase Authentication'dan gelir
- İsim, e-posta
- Kayıt tarihi

**Önemli Özellik:**
- `users` koleksiyonuna benzer, ama restoran sahipleri için ayrı tutulur
- Her restoran dokümanında `ownerId` alanı vardır, bu sayede hangi restoranın kime ait olduğu bilinir

**İki Uygulama Arasındaki Bağlantı:**
- Sadece restoran uygulaması kullanır
- Müşteri uygulaması ile doğrudan bağlantısı yok

---

## 🔗 İki Uygulama Arasındaki Veri Akışı

### Senaryo 1: Müşteri Restoran Listesini Görüyor

```
Müşteri Uygulaması
    ↓ (Okuma isteği)
Firebase: restaurants koleksiyonu
    ↓ (Tüm restoranlar)
Müşteri Uygulaması (Liste gösterilir)
```

**Açıklama:**
- Müşteri uygulaması, `restaurants` koleksiyonundan tüm restoranları çeker
- Her restoran dokümanı, içindeki yorumları da birlikte getirir
- Ekranda restoran listesi gösterilir

---

### Senaryo 2: Müşteri Yorum Yapıyor

```
Müşteri Uygulaması
    ↓ (Yorum yazıldı, gönder butonuna basıldı)
Firebase: restaurants koleksiyonu → "restaurant-1" dokümanı
    ↓ (Doküman güncellenir: Yeni yorum listeye eklenir, puanlar yeniden hesaplanır)
Firebase: restaurants koleksiyonu (güncellenmiş hali)
    ↓ (Her iki uygulama da güncel veriyi görür)
Müşteri Uygulaması (Yorum eklendi mesajı)
Restoran Uygulaması (Yeni yorum görünür - eğer o restoranı açıksa)
```

**Açıklama:**
- Müşteri yorum yazdığında, ilgili restoran dokümanı **güncellenir**
- Yorum, restoran dokümanının içindeki `reviews` listesine eklenir
- Puanlar (Hijyen, Lezzet, Fiyat) otomatik yeniden hesaplanır
- Restoran uygulaması, o restoranı açtığında yeni yorumu görür

---

### Senaryo 3: Müşteri Garson Çağırıyor (EN ÖNEMLİSİ!)

```
Müşteri Uygulaması
    ↓ ("Garson Çağır" butonuna basıldı)
Firebase: requests koleksiyonu
    ↓ (Yeni istek dokümanı oluşturulur)
    {
        id: "abc123",
        restaurantId: "restaurant-1",
        tableName: "Masa 5",
        requestType: "Garson",
        status: "pending",
        timestamp: 1699624800000
    }
    ↓ (Canlı dinleme tetiklenir)
Restoran Uygulaması
    ↓ (Anında bildirim alır)
Restoran Uygulaması Ekranı (Yeni istek listeye eklenir)
```

**Açıklama:**
- Müşteri "Garson Çağır" butonuna bastığında, `requests` koleksiyonuna **yeni bir doküman** eklenir
- Restoran uygulaması, bu koleksiyonu **canlı dinliyor** (Snapshot Listener)
- Yeni doküman eklendiğinde, Firebase otomatik olarak restoran uygulamasına bildirim gönderir
- Restoran uygulaması, ekranı anında günceller (yeni istek görünür)

**Neden Canlı Dinleme Kullanılıyor?**
- Restoran uygulaması sürekli "Yeni istek var mı?" diye sormak yerine, Firebase otomatik bildirim gönderir
- Bu sayede anlık güncelleme sağlanır
- İnternet bağlantısı olsa bile, Firebase değişiklikleri otomatik senkronize eder

---

### Senaryo 4: Restoran Sahibi Yorumlara Cevap Veriyor

```
Restoran Uygulaması
    ↓ (Yoruma cevap yazıldı, gönder butonuna basıldı)
Firebase: restaurants koleksiyonu → "restaurant-1" dokümanı
    ↓ (İlgili yorumun "ownerReply" alanı güncellenir)
Firebase: restaurants koleksiyonu (güncellenmiş hali)
    ↓ (Her iki uygulama da güncel veriyi görür)
Restoran Uygulaması (Cevap eklendi mesajı)
Müşteri Uygulaması (Yorumun altında restoran cevabı görünür)
```

**Açıklama:**
- Restoran sahibi bir yoruma cevap verdiğinde, ilgili restoran dokümanı güncellenir
- Yorumun içindeki `ownerReply` alanı doldurulur
- Müşteri uygulaması, restoran detayını açtığında yorumun altında restoran cevabını görür

---

## 🎯 Veritabanı Mantığı: Neden Bu Yapı?

### 1. Yorumlar Neden Restoran İçinde Saklanıyor?

**Seçenek A (Şu anki yapı):** Yorumlar, restoran dokümanının içinde liste olarak
```
restaurants/restaurant-1
    ├── name: "Nusr-Et"
    ├── reviews: [
    │     { id: "review-1", comment: "Harika!" },
    │     { id: "review-2", comment: "Çok iyi!" }
    │   ]
```

**Seçenek B (Alternatif):** Yorumlar ayrı koleksiyonda
```
restaurants/restaurant-1
    └── name: "Nusr-Et"

reviews/review-1
    ├── restaurantId: "restaurant-1"
    └── comment: "Harika!"
```

**Neden Seçenek A?**
- ✅ Bir restoranı çektiğinde, yorumları da birlikte gelir (tek sorgu)
- ✅ Yorum sayısı çok fazla değilse (örneğin 100-200), performans sorunu olmaz
- ✅ Daha basit yapı, daha az karmaşık sorgular

**Seçenek B Ne Zaman Kullanılır?**
- Yorum sayısı çok fazlaysa (binlerce)
- Sadece belirli yorumları filtrelemek gerekiyorsa
- Yorumlar üzerinde arama yapılacaksa

---

### 2. İstekler Neden Ayrı Koleksiyonda?

**Neden `requests` ayrı bir koleksiyon?**
- ✅ İstekler sürekli artar (her müşteri isteği yeni bir doküman)
- ✅ İsteklerin kendi yaşam döngüsü vardır (pending → completed)
- ✅ Restoran uygulaması sadece istekleri dinlemek ister (restoran bilgilerine gerek yok)
- ✅ Filtreleme kolaydır (sadece bekleyen istekleri göster)

**Eğer restoran içinde saklansaydı:**
- Her istek için tüm restoran dokümanı güncellenmeliydi
- Canlı dinleme daha karmaşık olurdu
- Performans sorunları olabilirdi

---

### 3. Kullanıcılar Neden Ayrı Koleksiyonlarda?

**`users` vs `owners`:**
- ✅ Farklı roller (müşteri vs restoran sahibi)
- ✅ Farklı izinler (müşteri sadece yorum yapar, sahip restoran yönetir)
- ✅ Gelecekte farklı özellikler eklenebilir (müşteri için favoriler, sahip için istatistikler)

---

## 📈 Veri Akış Diyagramı

```
┌─────────────────────┐         ┌─────────────────────┐
│  Müşteri Uygulaması │         │ Restoran Uygulaması │
└──────────┬──────────┘         └──────────┬──────────┘
           │                                │
           │                                │
           ▼                                ▼
    ┌─────────────────────────────────────────────┐
    │         Firebase Firestore Veritabanı        │
    ├─────────────────────────────────────────────┤
    │                                             │
    │  📍 restaurants (Koleksiyon)                │
    │     ├── restaurant-1 (Doküman)               │
    │     │   ├── name, address, puanlar          │
    │     │   └── reviews: [yorum1, yorum2]       │
    │     └── restaurant-2                         │
    │                                             │
    │  🛎️ requests (Koleksiyon)                   │
    │     ├── request-abc (Doküman)                │
    │     │   ├── restaurantId: "restaurant-1"     │
    │     │   ├── requestType: "Garson"            │
    │     │   └── status: "pending"                │
    │     └── request-xyz                          │
    │                                             │
    │  👥 users (Koleksiyon)                      │
    │     └── user-123 (Müşteri bilgileri)         │
    │                                             │
    │  👨‍🍳 owners (Koleksiyon)                    │
    │     └── owner-456 (Restoran sahibi bilgileri)│
    │                                             │
    └─────────────────────────────────────────────┘
```

---

## 🔄 Gerçek Zamanlı Senkronizasyon

### Canlı Dinleme (Real-time Listener) Nasıl Çalışır?

**Restoran Uygulaması:**
1. Uygulama açıldığında, `requests` koleksiyonunu dinlemeye başlar
2. Filtre: Sadece kendi restoranının bekleyen istekleri
3. Firebase, her değişiklikte otomatik bildirim gönderir:
   - Yeni istek eklendi → Listeye ekle
   - İstek tamamlandı → Listeden çıkar
   - İstek güncellendi → Güncelle

**Avantajları:**
- ✅ Anlık güncelleme (müşteri butona bastığı saniye, restoran görür)
- ✅ İnternet bağlantısı kesilse bile, bağlantı kurulunca otomatik senkronize olur
- ✅ Manuel yenileme butonuna gerek yok

**Dezavantajları:**
- ⚠️ Sürekli dinleme = Sürekli veri transferi (biraz daha fazla veri kullanımı)
- ⚠️ Uygulama kapanırken dinlemeyi durdurmak gerekir (bellek sızıntısını önlemek için)

---

## 🎓 Özet: İki Uygulama Arasındaki Bağlantılar

| Koleksiyon | Müşteri Uygulaması | Restoran Uygulaması | Bağlantı Tipi |
|------------|-------------------|-------------------|---------------|
| **restaurants** | ✅ Okur, Yorum ekler | ✅ Okur/Yazar, Yorumlara cevap verir | **Çift Yönlü** (Her ikisi de günceller) |
| **requests** | ✅ Yeni istek gönderir | ✅ Canlı dinler, Tamamlar | **Tek Yönlü** (Müşteri → Restoran) |
| **users** | ✅ Kayıt yapar | ❌ Kullanmaz | **Bağlantısız** |
| **owners** | ❌ Kullanmaz | ✅ Kayıt yapar | **Bağlantısız** |

---

## 💡 Önemli Notlar

1. **Aynı Firebase Projesi:** Her iki uygulama da aynı Firebase projesini kullanır (`restoranapp-c16f7`). Bu sayede veriler paylaşılır.

2. **Kimlik Doğrulama:** Her iki uygulama da Firebase Authentication kullanır, ama farklı roller için (`users` vs `owners`).

3. **Canlı Dinleme Sadece İsteklerde:** Şu an sadece `requests` koleksiyonu canlı dinleniyor. Restoranlar ve yorumlar için canlı dinleme yok (her açılışta yeniden çekiliyor).

4. **Güvenlik:** Firebase konsolunda Firestore Rules ayarlanmalı. Şu an muhtemelen herkes okuyup yazabiliyor (test aşaması için normal, production'da değiştirilmeli).

---

**Soruların varsa çekinme, sorabilirsin!** 🚀


