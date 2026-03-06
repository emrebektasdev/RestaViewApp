# 📊 RestoranApp - Sunum Planı

Bu doküman, RestoranApp uygulaması için hazırlanacak sunumun sayfa sayfa planını içerir. Her sayfa için gerekli görseller ve açıklamalar belirtilmiştir.

---

## 📑 Sayfa 1: Kapak Sayfası
**Başlık:** RestoranApp  
**Alt Başlık:** Modern Restoran Deneyimi İçin Mobil Çözüm

**Görsel:** 
- Uygulama logosu/icon
- Arka planda modern restoran görseli (opsiyonel)

**Açıklama:**
- Uygulama adı ve tagline
- Sunum tarihi (opsiyonel)
- Geliştirici adı (opsiyonel)

---

## 📑 Sayfa 2: Uygulama Tanıtımı (Genel Bakış)
**Başlık:** RestoranApp Nedir?

**Görsel:**
- Uygulama ana ekranının (HomeFragment) ekran görüntüsü
- 3-4 önemli özellik ikonu görsel olarak gösterilebilir

**Açıklama:**
- Restoran keşfetme ve değerlendirme platformu
- Müşteriler restoranları görüntüleyebilir, puanlayabilir ve yorum yapabilir
- QR kod ile hızlı masa seçimi
- Gerçek zamanlı garson çağırma ve istek gönderme

**Ana Özellikler (Madde İşaretleri):**
- ✅ Şehir bazlı restoran filtreleme
- ✅ Çoklu kriterli değerlendirme sistemi (Hijyen, Lezzet, Fiyat)
- ✅ QR kod ile masa seçimi
- ✅ Garson çağırma ve özel istek gönderme
- ✅ Kullanıcı yorumları ve restoran cevapları

---

## 📑 Sayfa 3: Teknik Mimari
**Başlık:** Kullanılan Teknolojiler

**Görsel:**
- Teknoloji stack görseli (logo'lar yan yana):
  - Kotlin
  - Firebase (Authentication + Firestore)
  - Android Jetpack (ViewBinding, Fragments)
  - Glide (Görsel yükleme)
  - ZXing (QR kod okuma)

**Açıklama:**
- **Platform:** Android (Kotlin)
- **Backend:** Firebase (Firestore NoSQL veritabanı)
- **Kimlik Doğrulama:** Firebase Authentication (E-posta ve Google Sign-In)
- **Görsel Yükleme:** Glide kütüphanesi
- **QR Kod:** ZXing entegrasyonu
- **Mimari:** Fragment tabanlı navigasyon

---

## 📑 Sayfa 4: Giriş ve Kimlik Doğrulama
**Başlık:** Kullanıcı Girişi ve Kayıt

**Görsel:**
- LoginActivity ekran görüntüsü
- RegisterActivity ekran görüntüsü (opsiyonel)
- Firebase Authentication konsol görüntüsü (kullanıcılar listesi)

**Açıklama:**
- E-posta/şifre ile kayıt ve giriş
- Google Sign-In entegrasyonu
- Firebase Authentication ile güvenli kimlik doğrulama
- Otomatik kullanıcı profili oluşturma

**Özellikler:**
- Google ile tek tıkla giriş
- Oturum yönetimi (session persistence)
- Güvenli şifre saklama

---

## 📑 Sayfa 5: Ana Ekran - Restoran Listesi
**Başlık:** Restoran Keşfetme

**Görsel:**
- HomeFragment ekran görüntüsü (restoran listesi görünür halde)
- Farklı şehir seçilmiş durumda ekran görüntüsü (filtreleme gösterimi)

**Açıklama:**
- Şehir bazlı restoran filtreleme (spinner ile)
- Çoklu sıralama seçenekleri:
  - RW Puanı (Hijyen %50 + Lezzet %25 + Fiyat %25)
  - Hijyen puanı
  - Lezzet puanı
  - Fiyat puanı
- QR kod tarama butonu
- Kullanıcı profil bilgileri (üst kısım)

**Özellikler:**
- Firebase Firestore'dan gerçek zamanlı veri çekme
- RecyclerView ile performanslı liste görüntüleme
- Glide ile hızlı görsel yükleme

---

## 📑 Sayfa 6: Restoran Detay Sayfası
**Başlık:** Detaylı Restoran Bilgileri

**Görsel:**
- DetailFragment ekran görüntüsü (tam görünüm)
- Yorumlar bölümü görünür olan ekran görüntüsü

**Açıklama:**
- Restoran bilgileri (isim, açıklama, adres, puanlar)
- Görsel gösterimi
- Seçili masa bilgisi
- **Hızlı Erişim Butonları:**
  - Garson Çağır
  - Hesap İste
  - Özel İstek
  - Yorum Ekle

**Özellikler:**
- Dinamik puan gösterimi (Hijyen, Lezzet, Fiyat)
- RW Puanı hesaplama (otomatik)
- Masa bilgisi takibi

---

## 📑 Sayfa 7: QR Kod ve Masa Seçimi
**Başlık:** QR Kod ile Hızlı Masa Seçimi

**Görsel:**
- QR kod tarama ekranı (kamera açık halde)
- QR kod örneği (masa üzerinde basılı halde - mock görsel)
- DetailFragment'te "Seçili: Masa X" gösterimi

**Açıklama:**
- QR kod formatı: `restaurantId:tableId`
- Hızlı masa seçimi ve restoran detayına yönlendirme
- Garson çağırma ve istek gönderme için masa bilgisi gerekli

**Avantajlar:**
- Kullanıcı deneyimi iyileştirmesi
- Temassız etkileşim
- Hızlı servis talebi

---

## 📑 Sayfa 8: İstek Gönderme Sistemi
**Başlık:** Gerçek Zamanlı İstek Yönetimi

**Görsel:**
- DetailFragment'te "Garson Çağır", "Hesap İste", "Özel İstek" butonları görünür
- Özel istek diyalog ekranı
- Firebase Firestore'da `requests` koleksiyonu görüntüsü (konsol)

**Açıklama:**
- Üç tip istek: Garson, Hesap, Özel İstek
- İstekler Firebase'e anlık kaydedilir
- Restoran yönetim uygulaması (RestaView) tarafından canlı dinlenir
- İstek durumu: pending → completed

**Veri Akışı:**
1. Müşteri istek gönderir (RestoranApp)
2. Firebase'e kaydedilir (`requests` koleksiyonu)
3. Restoran uygulaması anında bildirim alır (real-time listener)
4. İstek tamamlanır

---

## 📑 Sayfa 9: Yorum ve Değerlendirme Sistemi
**Başlık:** Kullanıcı Yorumları ve Puanlama

**Görsel:**
- ReviewFragment ekran görüntüsü (yorum formu)
- DetailFragment'te yorumlar listesi görünür halde
- Bir yorumun altında restoran cevabı görünen örnek

**Açıklama:**
- Üç kriterli puanlama:
  - Hijyen (1-5)
  - Lezzet (1-5)
  - Fiyat (1-5)
- Yorum metni ve tarih
- Otomatik puan hesaplama (restoran genel puanları güncellenir)
- Restoran sahibi cevap verebilir
- Kullanıcı kendi yorumunu silebilir

**Özellikler:**
- Real-time puan güncelleme
- Transaction ile veri bütünlüğü
- Kullanıcı bazlı yorum yönetimi

---

## 📑 Sayfa 10: Kullanıcı Profili
**Başlık:** Profil Yönetimi

**Görsel:**
- ProfileFragment ekran görüntüsü
- Firebase Authentication konsolunda kullanıcı bilgileri

**Açıklama:**
- Kullanıcı bilgileri görüntüleme (isim, e-posta)
- Google profil fotoğrafı entegrasyonu
- Profil bilgilerini güncelleme
- Çıkış yapma

**Özellikler:**
- Firebase Authentication profil yönetimi
- Firestore'da kullanıcı kaydı (`users` koleksiyonu)
- Oturum yönetimi

---

## 📑 Sayfa 11: Firebase Veritabanı Yapısı (Genel)
**Başlık:** Firebase Firestore Veritabanı Mimarisi

**Görsel:**
- Firebase konsol görüntüsü: Ana koleksiyonlar (restaurants, users, requests)
- Koleksiyonların genel yapısını gösteren görsel

**Açıklama:**
- **4 Ana Koleksiyon:**
  1. `restaurants` - Restoran bilgileri ve yorumları
  2. `users` - Müşteri kullanıcı bilgileri
  3. `requests` - Masa istekleri (gerçek zamanlı)
  4. `owners` - Restoran sahipleri (RestaView uygulaması için)

**NoSQL Yapı:**
- Doküman tabanlı veritabanı
- Koleksiyonlar = Tablolar
- Dokümanlar = Satırlar
- İç içe veri yapıları (yorumlar restoran içinde)

---

## 📑 Sayfa 12: Veritabanı İlişkileri - Restaurants
**Başlık:** Restaurants Koleksiyonu ve İlişkileri

**Görsel:**
- Firebase konsol: `restaurants` koleksiyonu açık
- Bir restoran dokümanının içeriği görünür (fields: name, description, reviews, scores vb.)
- Yorumlar (reviews) array'inin iç yapısı görüntüsü

**Açıklama:**
- **Restoran Dokümanı İçeriği:**
  - Temel bilgiler (isim, açıklama, adres, şehir)
  - Puanlar (hygieneScore, tasteScore, priceScore, rwScore)
  - Konum bilgisi (lat, lng)
  - **Reviews Array:** Restorana ait tüm yorumlar (nested)
  - ownerId: Restoran sahibi referansı

**İlişki:**
- Yorumlar restoran içinde saklanır (embedded)
- Puanlar otomatik hesaplanır (her yeni yorumda transaction)

**Neden Bu Yapı?**
- Tek sorgu ile restoran + yorumlar gelir
- Performans avantajı (yorum sayısı sınırlıyken)

---

## 📑 Sayfa 13: Veritabanı İlişkileri - Requests
**Başlık:** Requests Koleksiyonu ve Real-time İletişim

**Görsel:**
- Firebase konsol: `requests` koleksiyonu açık
- Bir request dokümanının içeriği (restaurantId, tableName, requestType, status, timestamp)
- Farklı durumlarda istekler (pending, completed)

**Açıklama:**
- **İstek Dokümanı:**
  - restaurantId: Hangi restorana gönderildi
  - tableName: Hangi masadan
  - requestType: Garson/Hesap/Özel İstek
  - status: pending/completed
  - timestamp: Gönderilme zamanı

**Real-time Özellik:**
- Restoran uygulaması canlı dinler (Snapshot Listener)
- Yeni istek geldiğinde anında bildirim
- İki uygulama arası gerçek zamanlı iletişim

**Veri Akışı:**
```
Müşteri App → Firebase (requests) → Restoran App (real-time)
```

---

## 📑 Sayfa 14: Veritabanı İlişkileri - Users
**Başlık:** Kullanıcı Yönetimi ve İlişkileri

**Görsel:**
- Firebase konsol: `users` koleksiyonu açık
- Bir user dokümanının içeriği (uid, name, email, registerDate)
- Firebase Authentication konsol görüntüsü (yan yana)

**Açıklama:**
- **User Dokümanı:**
  - uid: Firebase Auth'dan gelen benzersiz ID
  - name: Kullanıcı adı
  - email: E-posta
  - registerDate: Kayıt tarihi

**İlişki:**
- Authentication + Firestore birlikte çalışır
- Her yorumda userId saklanır (kim yaptığını bilmek için)
- Yorum silme yetkisi userId kontrolü ile

**Avantajlar:**
- Güvenli kullanıcı yönetimi
- Kullanıcı bazlı içerik kontrolü

---

## 📑 Sayfa 15: İki Uygulama Arası Veri Akışı
**Başlık:** Müşteri ve Restoran Uygulamaları Arası İletişim

**Görsel:**
- Diyagram görseli:
  - Sol: RestoranApp (Müşteri)
  - Sağ: RestaView (Restoran Yönetim) - opsiyonel görsel
  - Ortada: Firebase Firestore (bulut simgesi)
  - Ok'lar ile veri akışı

**Açıklama:**
- **Aynı Firebase Projesi:** Her iki uygulama aynı veritabanını kullanır
- **Restaurants Koleksiyonu:** Çift yönlü (müşteri okur/yorum ekler, restoran cevap verir)
- **Requests Koleksiyonu:** Tek yönlü (müşteri gönderir, restoran alır)
- **Gerçek Zamanlı Senkronizasyon:** Real-time listeners ile anlık güncelleme

**Senaryolar:**
1. Müşteri yorum yapar → Restoran görür
2. Müşteri istek gönderir → Restoran anında bildirim alır
3. Restoran cevap verir → Müşteri görür

---

## 📑 Sayfa 16: Teknik Özellikler ve Güvenlik
**Başlık:** Güvenlik ve Performans

**Görsel:**
- Firebase Security Rules ekran görüntüsü (konsol)
- Firebase Authentication ayarları (opsiyonel)

**Açıklama:**
- **Firebase Authentication:**
  - Güvenli şifre saklama (hash)
  - Google OAuth entegrasyonu
  - Oturum yönetimi

- **Firestore Security Rules:**
  - Kullanıcı bazlı erişim kontrolü (gelecek için)
  - Veri doğrulama

- **Transaction Kullanımı:**
  - Puan güncellemelerinde veri bütünlüğü
  - Aynı anda yorum ekleme durumlarında tutarlılık

- **Performans:**
  - RecyclerView ile liste optimizasyonu
  - Glide ile görsel cache
  - Firestore index'leme

---

## 📑 Sayfa 17: Özellikler Özeti
**Başlık:** Uygulama Özellikleri

**Görsel:**
- Ana ekranların küçük thumbnail'leri (4-6 ekran yan yana)
- Özellik ikonları

**Açıklama:**
- ✅ **Restoran Keşfetme:**
  - Şehir bazlı filtreleme
  - Çoklu sıralama kriterleri
  - Detaylı restoran bilgileri

- ✅ **QR Kod Entegrasyonu:**
  - Hızlı masa seçimi
  - Temassız etkileşim

- ✅ **Yorum ve Puanlama:**
  - 3 kriterli puanlama sistemi
  - Otomatik puan hesaplama
  - Restoran cevap özelliği

- ✅ **Gerçek Zamanlı İstekler:**
  - Garson çağırma
  - Hesap isteme
  - Özel istek gönderme

- ✅ **Kullanıcı Yönetimi:**
  - Google Sign-In
  - Profil yönetimi
  - Güvenli oturum

---

## 📑 Sayfa 18: Ticarileşme Potansiyeli - Genel Bakış
**Başlık:** Pazara Giriş Stratejisi

**Görsel:**
- Pazar analizi görseli (piramit/kartlar)
- Rekabet analizi (opsiyonel)

**Açıklama:**
- **Hedef Kitle:**
  - Restoran müşterileri (18-45 yaş)
  - Restoran işletmecileri
  - Yemek severler ve gezginler

- **Pazar Fırsatı:**
  - Dijitalleşen restoran sektörü
  - Temassız hizmet talebi
  - Müşteri deneyimi odaklı yaklaşım

---

## 📑 Sayfa 19: Ticarileşme Potansiyeli - Gelir Modelleri
**Başlık:** Gelir Kaynakları

**Görsel:**
- Gelir modeli diyagramı (kartlar/ikonal görsel)
- Premium özellikler mockup'ı (opsiyonel)

**Açıklama:**
- **1. Freemium Model:**
  - Temel özellikler ücretsiz
  - Premium özellikler abonelik bazlı
  - Restoranlar için gelişmiş yönetim paneli

- **2. Reklam Modeli:**
  - Restoran sponsoring
  - Banner reklamlar
  - Öne çıkan restoran listeleme

- **3. Komisyon Modeli:**
  - Rezervasyon komisyonu
  - Kampanya ve indirim yönetimi
  - Restoran iş ortaklıkları

- **4. Kurumsal Paketler:**
  - Restoran zincirleri için özel çözümler
  - Analitik ve raporlama
  - Çoklu şube yönetimi

---

## 📑 Sayfa 20: Ticarileşme Potansiyeli - Rekabet Avantajları
**Başlık:** Farklılaşma Stratejisi

**Görsel:**
- Özellik karşılaştırma tablosu (opsiyonel)
- Benzersiz özellikler vurgusu

**Açıklama:**
- **Teknik Avantajlar:**
  - Gerçek zamanlı iletişim (Firebase real-time)
  - QR kod entegrasyonu
  - Çoklu kriterli değerlendirme sistemi

- **Kullanıcı Deneyimi:**
  - Basit ve sezgisel arayüz
  - Hızlı masa seçimi
  - Anında istek gönderme

- **Ölçeklenebilirlik:**
  - Firebase altyapısı ile kolay ölçekleme
  - Yeni şehirler için hızlı genişleme
  - Yeni özellikler eklenebilir yapı

---

## 📑 Sayfa 21: Gelecek Geliştirmeler
**Başlık:** Yol Haritası (Roadmap)

**Görsel:**
- Zaman çizelgesi görseli (roadmap)
- Özellik ikonları ile timeline

**Açıklama:**
- **Kısa Vadede (1-3 ay):**
  - iOS uygulaması
  - Push notification entegrasyonu
  - Harita entegrasyonu (Google Maps)

- **Orta Vadede (3-6 ay):**
  - Rezervasyon sistemi
  - Favoriler listesi
  - Restoran arama özelliği

- **Uzun Vadede (6-12 ay):**
  - Online ödeme entegrasyonu
  - Sadakat programı
  - AI destekli restoran önerileri

---

## 📑 Sayfa 22: Sonuç ve Demo
**Başlık:** Özet ve Canlı Demo

**Görsel:**
- Uygulama logo/banner
- Ekran görüntüleri karışımı (collage)

**Açıklama:**
- Modern ve kullanıcı dostu restoran platformu
- Firebase ile güçlü altyapı
- Gerçek zamanlı iletişim özellikleri
- Ölçeklenebilir ve sürdürülebilir yapı

**Canlı Demo Hazırlığı:**
- Uygulama açılışından ana ekrana akış
- QR kod tarama gösterimi
- Yorum ekleme ve istek gönderme

---

## 📑 Sayfa 23: Sorular ve Cevaplar
**Başlık:** Sorularınızı Bekliyoruz

**Görsel:**
- QR kod (projeye link için - opsiyonel)
- İletişim bilgileri

**Açıklama:**
- Soru-cevap bölümü için hazır sayfa
- İletişim bilgileri (e-posta, LinkedIn vb.)

---

## 📝 Görsel İhtiyaç Listesi (Özet)

### Uygulama Ekran Görüntüleri (Screenshot):
1. ✅ LoginActivity (giriş ekranı)
2. ✅ HomeFragment (ana ekran - restoran listesi)
3. ✅ HomeFragment (şehir filtresi aktif)
4. ✅ DetailFragment (restoran detay sayfası - tam görünüm)
5. ✅ DetailFragment (yorumlar görünür)
6. ✅ ReviewFragment (yorum ekleme formu)
7. ✅ ProfileFragment (profil sayfası)
8. ✅ QR kod tarama ekranı (kamera açık)
9. ✅ Özel istek diyalog ekranı
10. ✅ Sıralama diyalog ekranı

### Firebase Konsol Görüntüleri:
1. ✅ Firebase konsol ana sayfa (proje genel bakış)
2. ✅ Firestore Database - Koleksiyonlar listesi
3. ✅ `restaurants` koleksiyonu görünümü
4. ✅ Bir restoran dokümanının detayları (fields görünür)
5. ✅ `requests` koleksiyonu görünümü
6. ✅ Bir request dokümanının detayları
7. ✅ `users` koleksiyonu görünümü
8. ✅ Authentication kullanıcı listesi
9. ✅ Security Rules ekranı (opsiyonel)

### Diyagramlar ve Görseller:
1. ✅ Teknoloji stack logosu (Kotlin, Firebase, Android vb.)
2. ✅ Veri akış diyagramı (iki uygulama arası)
3. ✅ Veritabanı ilişki diyagramı
4. ✅ Özellik ikonları/collage

---

## 💡 Sunum İpuçları

1. **Görsel Denge:** Her slaytta görsel ve metin dengesi korunmalı (az metin, çok görsel)
2. **Ekran Görüntüleri:** Gerçek kullanıcı senaryoları ile çekilmeli (örnek veriler ile dolu)
3. **Firebase Konsol:** Görseller temiz ve anlaşılır olmalı (hassas bilgiler gizlenmiş)
4. **Animasyonlar:** Slayt geçişlerinde basit animasyonlar kullanılabilir
5. **Demo:** Canlı demo için uygulama hazır tutulmalı (test verileri ile)

---

## 📌 Önemli Notlar

- Her slayt maksimum 5-6 madde içermeli
- Aşırı teknik detaylara girilmemeli (genel kitle için)
- Ticarileşme bölümünde somut örnekler verilmeli
- Görseller profesyonel görünmeli (blur yapılmamış, net)
- Firebase konsol görsellerinde gerçek veri kullanılabilir (test ortamı)

---

**Başarılar! 🚀**
