package com.ebco.restoranapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ebco.restoranapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            binding.etName.setText(user.displayName)
            binding.etEmail.setText(user.email)

            if (user.photoUrl != null) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.ivProfileLarge)
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            if (name.isNotEmpty()) {
                updateProfile(name)
            } else {
                Toast.makeText(context, "İsim boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.tvChangePhoto.setOnClickListener {
            Toast.makeText(context, "Fotoğraf yükleme özelliği yakında eklenecek", Toast.LENGTH_SHORT).show()
            // TODO: Implement image picker and upload to Firebase Storage
        }
    }

    private fun updateProfile(name: String) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Profil güncellendi", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




