package com.ebco.restoranapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide // Glide Eklendi
import com.ebco.restoranapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val firestoreHelper = FirestoreHelper()
    private lateinit var restaurantAdapter: RestaurantAdapter
    private var allRestaurants = listOf<Restaurant>()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupRecyclerView()
        loadRestaurants()
        
        // QR Butonu Tıklama
        binding.btnScanQR.setOnClickListener {
            startQRScanner()
        }
        
        // Profil Tıklama
        binding.cvProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Masadaki QR Kodu Okutun")
        integrator.setCameraId(0) // Arka kamera
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Tarama iptal edildi", Toast.LENGTH_LONG).show()
            } else {
                handleQRResult(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleQRResult(qrContent: String) {
        try {
            val parts = qrContent.split(":")
            if (parts.size == 2) {
                val restaurantId = parts[0]
                val tableId = parts[1]
                
                Toast.makeText(context, "Masa $tableId doğrulandı!", Toast.LENGTH_SHORT).show()
                openRestaurantDetail(restaurantId, tableId)
            } else {
                Toast.makeText(context, "Geçersiz QR Kod Formatı!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupHeader() {
        val user = auth.currentUser
        if (user != null) {
             // İsim Çekme
             db.collection("users").document(user.uid).get()
                 .addOnSuccessListener { 
                     val name = it.getString("name") ?: "Kullanıcı"
                     binding.tvUserName.text = name
                 }

             // Fotoğraf Yükleme (Google'dan)
             if (user.photoUrl != null) {
                 try {
                     Glide.with(requireContext())
                         .load(user.photoUrl)
                         .placeholder(R.drawable.profile_placeholder)
                         .circleCrop()
                         .into(binding.ivProfileImage)
                 } catch (e: Exception) {
                     // Hata olursa placeholder kalır
                 }
             }
        }

        val cities = MockData.cities
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter

        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = cities[position]
                filterRestaurants(selectedCity)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        binding.ivSort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter(emptyList()) { restaurant ->
            openRestaurantDetail(restaurant.id, "Belirtilmedi")
        }
        binding.rvRestaurants.layoutManager = LinearLayoutManager(context)
        binding.rvRestaurants.adapter = restaurantAdapter
    }

    private fun openRestaurantDetail(restaurantId: String, tableName: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, DetailFragment.newInstance(restaurantId, tableName))
            .addToBackStack(null)
            .commit()
    }

    private fun loadRestaurants() {
        binding.progressBar.visibility = View.VISIBLE
        firestoreHelper.getRestaurants(
            onSuccess = { restaurants ->
                binding.progressBar.visibility = View.GONE
                allRestaurants = restaurants
                val currentCity = binding.spinnerCity.selectedItem?.toString() ?: "İstanbul"
                filterRestaurants(currentCity)
            },
            onFailure = {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Veriler alınamadı: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun filterRestaurants(city: String) {
        val filtered = allRestaurants.filter { it.city == city }
        restaurantAdapter.updateList(filtered)
    }
    
    private fun showSortDialog() {
        val options = arrayOf("RW Puanı (Azalan)", "Hijyen (Azalan)", "Lezzet (Azalan)", "Fiyat (Artan)")
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Sırala")
        builder.setItems(options) { _, which ->
            val sortedList = when (which) {
                0 -> allRestaurants.sortedByDescending { it.rwScore }
                1 -> allRestaurants.sortedByDescending { it.hygieneScore }
                2 -> allRestaurants.sortedByDescending { it.tasteScore }
                3 -> allRestaurants.sortedBy { it.priceScore }
                else -> allRestaurants
            }
            val city = binding.spinnerCity.selectedItem.toString()
            restaurantAdapter.updateList(sortedList.filter { it.city == city })
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}