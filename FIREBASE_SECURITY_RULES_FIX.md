# 🔒 Firebase Security Rules - Permission Denied Hatası Çözümü

## ❌ Sorun
Uygulamada "veriler alınamadı permission denied" hatası alınıyor.

## 🔍 Neden Olur?
Firebase Firestore'da güvenlik kuralları (Security Rules) tüm okuma/yazma işlemlerini engelliyor olabilir.

---

## ✅ Çözüm: Firebase Konsolunda Security Rules Güncelleme

### Adım 1: Firebase Konsoluna Git
1. `https://console.firebase.google.com` adresine git
2. Projeni seç (`restoranapp-c16f7` veya proje adın)
3. Sol menüden **"Firestore Database"** seçeneğine tıkla
4. Üstte **"Rules"** sekmesine tıkla

### Adım 2: Security Rules Kodunu Güncelle

**⚠️ ÖNEMLİ:** İki seçenek var:
- **Test/Geliştirme için:** Açık kurallar (herkese izin ver)
- **Production için:** Güvenli kurallar (sadece giriş yapan kullanıcılar)

---

## 🔓 Seçenek 1: Test/Geliştirme Kuralları (AÇIK - Hızlı Çözüm)

**⚠️ GÜVENLİK UYARISI:** Bu kurallar herkese izin verir. Sadece test için kullan!

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Tüm koleksiyonlara herkes erişebilir (TEST İÇİN)
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

**Ne yapar:**
- ✅ Herkes okuyabilir
- ✅ Herkes yazabilir
- ⚠️ Güvenli DEĞİL (sadece test için)

---

## 🔒 Seçenek 2: Production Kuralları (GÜVENLİ - Önerilen)

**✅ GÜVENLİ:** Sadece giriş yapan kullanıcılar işlem yapabilir.

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper function: Kullanıcı giriş yapmış mı?
    function isAuthenticated() {
      return request.auth != null;
    }
    
    // Helper function: Kullanıcı kendi dokümanını mı düzenliyor?
    function isOwner(userId) {
      return isAuthenticated() && request.auth.uid == userId;
    }
    
    // ========================================
    // RESTAURANTS Koleksiyonu
    // ========================================
    match /restaurants/{restaurantId} {
      // Herkes okuyabilir (müşteriler restoranları görebilmeli)
      allow read: if true;
      
      // Sadece giriş yapanlar yorum ekleyebilir veya restoran sahibi düzenleyebilir
      allow write: if isAuthenticated();
    }
    
    // ========================================
    // USERS Koleksiyonu
    // ========================================
    match /users/{userId} {
      // Herkes kullanıcı bilgilerini okuyabilir (profil görüntüleme için)
      allow read: if true;
      
      // Sadece kendi profili oluşturulabilir/güncellenebilir
      allow create: if isOwner(userId);
      allow update: if isOwner(userId);
      allow delete: if isOwner(userId);
    }
    
    // ========================================
    // REQUESTS Koleksiyonu
    // ========================================
    match /requests/{requestId} {
      // Sadece giriş yapanlar okuyabilir
      allow read: if isAuthenticated();
      
      // Herkes istek oluşturabilir (müşteriler)
      // Sadece restoran sahibi güncelleyebilir/silebilir (restoran uygulaması)
      allow create: if isAuthenticated();
      allow update: if isAuthenticated();
      allow delete: if isAuthenticated();
    }
    
    // ========================================
    // OWNERS Koleksiyonu (RestaView uygulaması için)
    // ========================================
    match /owners/{ownerId} {
      // Sadece giriş yapan restoran sahipleri erişebilir
      allow read: if isAuthenticated();
      allow write: if isOwner(ownerId);
    }
  }
}
```

**Ne yapar:**
- ✅ Restaurants: Herkes okuyabilir, sadece giriş yapanlar yazabilir
- ✅ Users: Herkes okuyabilir, sadece kendi profili düzenlenebilir
- ✅ Requests: Sadece giriş yapanlar erişebilir
- ✅ Owners: Sadece sahip erişebilir

---

## 📝 Adım 3: Kuralları Kaydet ve Yayınla

1. Yukarıdaki kurallardan birini seç (test için Seçenek 1, production için Seçenek 2)
2. Firebase konsolunda **Rules** sekmesindeki kodu sil ve yenisini yapıştır
3. **"Publish"** (Yayınla) butonuna tıkla
4. Birkaç saniye bekle (kuralların aktif olması için)

---

## 🔄 Adım 4: Uygulamayı Test Et

1. Uygulamayı kapat ve yeniden aç
2. Giriş yap (eğer giriş yapmadıysan)
3. Restoran listesinin yüklendiğini kontrol et

---

## 🐛 Hala Çalışmıyor mu?

### Kontrol Listesi:

1. ✅ Firebase konsolunda Rules sekmesinde kod doğru mu?
2. ✅ "Publish" butonuna tıkladın mı?
3. ✅ Uygulamada giriş yaptın mı? (Eğer Seçenek 2 kullanıyorsan)
4. ✅ İnternet bağlantısı var mı?
5. ✅ `google-services.json` dosyası doğru mu?

### Log Kontrolü:

Android Studio'da Logcat'te şu hataları ara:
```
Firestore: PERMISSION_DENIED
```

Eğer bu hata varsa, Rules'u kontrol et.

---

## 💡 Test Kuralları İçin Hızlı Kod (Kopyala-Yapıştır)

Eğer hemen test etmek istiyorsan, Firebase konsolunda Rules sekmesine şunu yapıştır:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

**Publish** butonuna tıkla ve birkaç saniye bekle. Uygulama artık çalışmalı!

---

## ⚠️ ÖNEMLİ UYARILAR

1. **Test Kuralları (Seçenek 1):**
   - ❌ Production'da ASLA kullanma!
   - ❌ Herkese açık veritabanı = Veri güvenliği yok
   - ✅ Sadece geliştirme/test için

2. **Production Kuralları (Seçenek 2):**
   - ✅ Güvenli
   - ✅ Kullanıcı doğrulaması gerektirir
   - ✅ Production için önerilen

3. **Kuralları Değiştirdikten Sonra:**
   - Firebase'de değişikliklerin aktif olması birkaç saniye sürebilir
   - Uygulamayı yeniden başlat

---

## 🎯 Sonuç

Bu adımları takip ettikten sonra "permission denied" hatası çözülmeli. Eğer hala sorun varsa, Logcat'teki hata mesajını paylaş.

**Başarılar! 🚀**
