package com.ebco.restoranapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ebco.restoranapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val surname = binding.editTextSurname.text.toString()
            val email = binding.editTextEmailRegister.text.toString()
            val password = binding.editTextPasswordRegister.text.toString()

            if (validateInput(name, surname, email, password)) {
                registerUser(email, password, "$name $surname")
            }
        }

        binding.textViewLogin.setOnClickListener {
            finish() // Go back to LoginActivity
        }
    }

    private fun registerUser(email: String, password: String, fullName: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Kayıt başarılı, Firestore'a kullanıcıyı kaydet
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        firestoreHelper.saveUser(user.uid, fullName, email) {
                            Toast.makeText(this, "Kayıt başarılı! Otomatik giriş yapılıyor...", Toast.LENGTH_SHORT).show()
                            // Kullanıcı oluştu, direkt login ekranına veya ana sayfaya atabiliriz
                            // Şimdilik login ekranına dönelim ki giriş yapsın
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "Kayıt başarısız: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun validateInput(name: String, surname: String, email: String, password: String): Boolean {
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
