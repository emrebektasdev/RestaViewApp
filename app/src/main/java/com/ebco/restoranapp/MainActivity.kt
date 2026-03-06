package com.ebco.restoranapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ebco.restoranapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            // MOCK DATA YÜKLEME (Sadece geliştirme aşaması için)
            // Veritabanı boşsa Mock verileri yükler.
            val firestoreHelper = FirestoreHelper()
            firestoreHelper.uploadMockData(
                onSuccess = {
                    // Yükleme bitti veya zaten veri var, HomeFragment'ı aç
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                },
                onFailure = { e ->
                    // Hata olsa bile uygulamayı açmaya çalışalım
                    e.printStackTrace()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                }
            )
        }
    }
}
