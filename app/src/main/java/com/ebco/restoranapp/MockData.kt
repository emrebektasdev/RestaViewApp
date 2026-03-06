package com.ebco.restoranapp

object MockData {
    // Mutable list to allow adding new reviews dynamically
    val reviews = mutableListOf(
        Review(
            id = "1", 
            restaurantId = "1", 
            userName = "Ahmet Y.", 
            hygieneRating = 5, 
            tasteRating = 4, 
            priceRating = 3, 
            comment = "Mutfak tertemizdi, güvenle yedik.", 
            isPositive = true, 
            date = "10.11.2023"
        ),
        Review(
            id = "2", 
            restaurantId = "2", 
            userName = "Ayşe K.", 
            hygieneRating = 2, 
            tasteRating = 5, 
            priceRating = 4, 
            comment = "Yemekler harika ama masalar biraz yapış yapıştı.", 
            isPositive = true, 
            date = "12.11.2023"
        ),
        Review(
            id = "3", 
            restaurantId = "3", 
            userName = "Mehmet T.", 
            hygieneRating = 4, 
            tasteRating = 3, 
            priceRating = 5, 
            comment = "Fiyat performans yeri.", 
            isPositive = false, 
            date = "15.11.2023"
        ),
        Review(
            id = "4", 
            restaurantId = "1", 
            userName = "Canan S.", 
            hygieneRating = 5, 
            tasteRating = 5, 
            priceRating = 5, 
            comment = "Bayıldım, her şey çok güzeldi.", 
            isPositive = true, 
            date = "16.11.2023"
        ),
        Review(
            id = "5", 
            restaurantId = "4", 
            userName = "Burak Ö.", 
            hygieneRating = 3, 
            tasteRating = 4, 
            priceRating = 2, 
            comment = "Fiyatlar biraz yüksek.", 
            isPositive = true, 
            date = "17.11.2023"
        )
    )

    // Cities for dropdown
    val cities = listOf("İstanbul", "İzmir", "Çanakkale", "Tokat", "Konya", "Ankara", "Bursa")

    // RW Puanı Hesaplama Yardımcısı
    private fun calculateRW(h: Double, t: Double, p: Double): Double {
        return (h * 0.5) + (t * 0.25) + (p * 0.25)
    }

    // Mutable list to allow updating restaurant scores
    val restaurants = mutableListOf(
        // İSTANBUL
        Restaurant(
            id = "1", ownerId = "", name = "Nusr-Et Steakhouse", description = "Dünyaca ünlü et restoranı.",
            hygieneScore = 4.9, tasteScore = 4.9, priceScore = 1.5, rwScore = calculateRW(4.9, 4.9, 1.5), 
            imageResId = R.drawable.img_rest_1, imageUrl = "", 
            address = "Kapalıçarşı, Beyazıt, İstanbul", city = "İstanbul", lat = 41.0108, lng = 28.9680, tableCount = 10,
            reviews = mutableListOf(reviews[0], reviews[3])
        ),
        Restaurant(
            id = "2", ownerId = "", name = "Hafız Mustafa 1864", description = "Geleneksel Türk tatlıları.",
            hygieneScore = 4.8, tasteScore = 5.0, priceScore = 3.5, rwScore = calculateRW(4.8, 5.0, 3.5), 
            imageResId = R.drawable.img_rest_2, imageUrl = "", 
            address = "Hobyar, Hamidiye Cd. No:84, 34112 Fatih/İstanbul", city = "İstanbul", lat = 41.0152, lng = 28.9757, tableCount = 10,
            reviews = mutableListOf(reviews[1])
        ),
        Restaurant(
            id = "3", ownerId = "", name = "Hamdi Restaurant", description = "Haliç manzaralı kebap keyfi.",
            hygieneScore = 4.5, tasteScore = 4.7, priceScore = 2.5, rwScore = calculateRW(4.5, 4.7, 2.5), 
            imageResId = R.drawable.img_rest_3, imageUrl = "", 
            address = "Rüstem Paşa, Kalçin Sk. No:11, 34116 Fatih/İstanbul", city = "İstanbul", lat = 41.0182, lng = 28.9706, tableCount = 10,
            reviews = mutableListOf()
        ),
        Restaurant(
            id = "4", ownerId = "", name = "Çiya Sofrası", description = "Anadolu lezzetlerinin adresi.",
            hygieneScore = 4.6, tasteScore = 4.8, priceScore = 3.0, rwScore = calculateRW(4.6, 4.8, 3.0), 
            imageResId = R.drawable.img_rest_4, imageUrl = "", 
            address = "Caferağa, Güneşli Bahçe Sk. No:43, 34710 Kadıköy/İstanbul", city = "İstanbul", lat = 40.9894, lng = 29.0269, tableCount = 10,
            reviews = mutableListOf(reviews[2])
        ),

        // İZMİR
        Restaurant(
            id = "5", ownerId = "", name = "Deniz Restaurant", description = "Kordon boyunda taze balık.",
            hygieneScore = 4.7, tasteScore = 4.8, priceScore = 2.0, rwScore = calculateRW(4.7, 4.8, 2.0), 
            imageResId = R.drawable.img_rest_5, imageUrl = "", 
            address = "Kültür, Atatürk Cd. No: 188/B, 35220 Konak/İzmir", city = "İzmir", lat = 38.4357, lng = 27.1388, tableCount = 10,
            reviews = mutableListOf(reviews[0])
        ),
        Restaurant(
            id = "6", ownerId = "", name = "Tavacı Recep Usta", description = "Meşhur saç tava lezzeti.",
            hygieneScore = 4.6, tasteScore = 4.7, priceScore = 3.0, rwScore = calculateRW(4.6, 4.7, 3.0), 
            imageResId = R.drawable.img_rest_6, imageUrl = "", 
            address = "Kültür, Atatürk Cd. No:364, 35220 Konak/İzmir", city = "İzmir", lat = 38.4378, lng = 27.1412, tableCount = 10,
            reviews = mutableListOf()
        ),
        Restaurant(
            id = "7", ownerId = "", name = "Dostlar Fırını", description = "İzmir boyozu denince akla gelen ilk yer.",
            hygieneScore = 4.5, tasteScore = 4.9, priceScore = 5.0, rwScore = calculateRW(4.5, 4.9, 5.0), 
            imageResId = R.drawable.img_rest_7, imageUrl = "", 
            address = "Kıbrıs Şehitleri Cd. No: 120, 35220 Konak/İzmir", city = "İzmir", lat = 38.4325, lng = 27.1450, tableCount = 10,
            reviews = mutableListOf(reviews[1])
        ),

        // ÇANAKKALE
        Restaurant(
            id = "8", ownerId = "", name = "Yalova Restaurant", description = "Boğaz manzaralı şık akşam yemeği.",
            hygieneScore = 4.8, tasteScore = 4.7, priceScore = 2.5, rwScore = calculateRW(4.8, 4.7, 2.5), 
            imageResId = R.drawable.img_rest_1, imageUrl = "", 
            address = "Kemalpaşa, Gümrük Sk. No:7, 17100 Çanakkale Merkez/Çanakkale", city = "Çanakkale", lat = 40.1510, lng = 26.4030, tableCount = 10,
            reviews = mutableListOf(reviews[2])
        ),
        Restaurant(
            id = "9", ownerId = "", name = "Sardalye", description = "Ekmek arası balığın en lezzetli hali.",
            hygieneScore = 4.5, tasteScore = 4.8, priceScore = 4.5, rwScore = calculateRW(4.5, 4.8, 4.5), 
            imageResId = R.drawable.img_rest_2, imageUrl = "", 
            address = "Kemalpaşa, Fetvane Sk. No:11, 17100 Çanakkale Merkez/Çanakkale", city = "Çanakkale", lat = 40.1478, lng = 26.4056, tableCount = 10,
            reviews = mutableListOf(reviews[3])
        ),
        Restaurant(
            id = "10", ownerId = "", name = "Hangover Cafe", description = "Gençlerin buluşma noktası.",
            hygieneScore = 4.2, tasteScore = 4.0, priceScore = 4.0, rwScore = calculateRW(4.2, 4.0, 4.0), 
            imageResId = R.drawable.img_rest_3, imageUrl = "", 
            address = "Kemalpaşa, Cumhuriyet Blv. No:13, 17100 Çanakkale Merkez/Çanakkale", city = "Çanakkale", lat = 40.1520, lng = 26.4100, tableCount = 10,
            reviews = mutableListOf()
        ),

        // TOKAT
        Restaurant(
            id = "11", ownerId = "", name = "Pirpirim", description = "Yöresel Tokat kebabı.",
            hygieneScore = 4.7, tasteScore = 4.9, priceScore = 3.5, rwScore = calculateRW(4.7, 4.9, 3.5), 
            imageResId = R.drawable.img_rest_4, imageUrl = "", 
            address = "Yarahmet, Gaziosmanpaşa Blv. No:136, 60100 Tokat Merkez/Tokat", city = "Tokat", lat = 40.3167, lng = 36.5500, tableCount = 10,
            reviews = mutableListOf(reviews[0])
        ),
        Restaurant(
            id = "12", ownerId = "", name = "Saklıbahçe", description = "Doğa ile iç içe kahvaltı.",
            hygieneScore = 4.8, tasteScore = 4.6, priceScore = 4.0, rwScore = calculateRW(4.8, 4.6, 4.0), 
            imageResId = R.drawable.img_rest_5, imageUrl = "", 
            address = "Karşıyaka, Geyras Cd., 60200 Tokat Merkez/Tokat", city = "Tokat", lat = 40.3000, lng = 36.5600, tableCount = 10,
            reviews = mutableListOf()
        ),
        Restaurant(
            id = "13", ownerId = "", name = "İşeri Restaurant", description = "Tokat'ın meşhur etli ekmeği.",
            hygieneScore = 4.5, tasteScore = 4.7, priceScore = 4.2, rwScore = calculateRW(4.5, 4.7, 4.2), 
            imageResId = R.drawable.img_rest_6, imageUrl = "", 
            address = "Gülbaharhatun, Gaziosmanpaşa Blv. 60100 Tokat Merkez/Tokat", city = "Tokat", lat = 40.3250, lng = 36.5450, tableCount = 10,
            reviews = mutableListOf(reviews[4])
        ),

        // KONYA
        Restaurant(
            id = "14", ownerId = "", name = "Tiritçi Mithat", description = "Konya'nın en iyi tiriti.",
            hygieneScore = 4.6, tasteScore = 4.8, priceScore = 3.8, rwScore = calculateRW(4.6, 4.8, 3.8), 
            imageResId = R.drawable.img_rest_7, imageUrl = "", 
            address = "Aziziye, İstanbul Cd. No:22, 42030 Karatay/Konya", city = "Konya", lat = 37.8720, lng = 32.5040, tableCount = 10,
            reviews = mutableListOf(reviews[1])
        ),
        Restaurant(
            id = "15", ownerId = "", name = "Hacı Şükrü", description = "Fırın kebabının adresi.",
            hygieneScore = 4.7, tasteScore = 4.9, priceScore = 3.0, rwScore = calculateRW(4.7, 4.9, 3.0), 
            imageResId = R.drawable.img_rest_1, imageUrl = "", 
            address = "Ferhuniye, Müneccimbaşı Sk. No:20, 42040 Selçuklu/Konya", city = "Konya", lat = 37.8780, lng = 32.4850, tableCount = 10,
            reviews = mutableListOf(reviews[2])
        ),
        Restaurant(
            id = "16", ownerId = "", name = "Cemo Etliekmek", description = "Konya etliekmeği burada yenir.",
            hygieneScore = 4.5, tasteScore = 4.8, priceScore = 4.0, rwScore = calculateRW(4.5, 4.8, 4.0), 
            imageResId = R.drawable.img_rest_2, imageUrl = "", 
            address = "Nalçacı Cd. No: 12/C, Konya", city = "Konya", lat = 37.8900, lng = 32.4900, tableCount = 10,
            reviews = mutableListOf()
        )
    )

    fun getRestaurantById(id: String): Restaurant? {
        return restaurants.find { it.id == id }
    }

    fun addReview(restaurantId: String, review: Review) {
        val restaurant = getRestaurantById(restaurantId) ?: return
        
        reviews.add(review)
        val currentReviews = restaurant.reviews.toMutableList()
        currentReviews.add(0, review) 
        
        val newHygiene = currentReviews.map { it.hygieneRating }.average()
        val newTaste = currentReviews.map { it.tasteRating }.average()
        val newPrice = currentReviews.map { it.priceRating }.average()

        val index = restaurants.indexOfFirst { it.id == restaurantId }
        if (index != -1) {
            restaurants[index] = restaurant.copy(
                reviews = currentReviews,
                hygieneScore = String.format("%.1f", newHygiene).replace(',', '.').toDouble(),
                tasteScore = String.format("%.1f", newTaste).replace(',', '.').toDouble(),
                priceScore = String.format("%.1f", newPrice).replace(',', '.').toDouble()
            )
        }
    }
}