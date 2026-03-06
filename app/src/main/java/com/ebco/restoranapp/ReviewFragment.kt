package com.ebco.restoranapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ebco.restoranapp.databinding.FragmentReviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private var restaurantId: String = ""
    private val firestoreHelper = FirestoreHelper()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private var isPositive = true 

    companion object {
        private const val ARG_ID = "restaurant_id"
        fun newInstance(id: String) = ReviewFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID, id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restaurantId = arguments?.getString(ARG_ID) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVerifyLocation.visibility = View.GONE
        binding.btnScanQR.visibility = View.GONE
        
        updateVerificationUI()

        binding.btnSubmitReview.setOnClickListener {
            if (validateReview()) {
                fetchUserAndSubmit()
            }
        }
    }

    private fun updateVerificationUI() {
        if (isPositive) {
            binding.tvVerificationStatus.text = "Durum: Olumlu (Demo)"
            binding.tvVerificationStatus.setTextColor(0xFF4CAF50.toInt()) 
            binding.tvVerificationStatus.setBackgroundColor(0xFFE8F5E9.toInt())
        } else {
            binding.tvVerificationStatus.text = "Durum: Olumsuz"
            binding.tvVerificationStatus.setTextColor(0xFFD32F2F.toInt()) 
            binding.tvVerificationStatus.setBackgroundColor(0xFFFFEBEE.toInt())
        }
    }

    private fun validateReview(): Boolean {
        if (binding.rbHygiene.rating == 0f) {
            Toast.makeText(context, "Lütfen hijyen puanı verin.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun fetchUserAndSubmit() {
        val userId = auth.currentUser?.uid ?: ""
        
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val realName = document.getString("name") ?: "Kullanıcı"
                    submitReview(userId, realName)
                }
                .addOnFailureListener {
                    // Hata olursa anonim gibi davran
                    submitReview(userId, "Misafir Kullanıcı")
                }
        } else {
             submitReview("", "Misafir Kullanıcı")
        }
    }

    private fun submitReview(userId: String, realName: String) {
        val hygiene = binding.rbHygiene.rating.toInt()
        val taste = binding.rbTaste.rating.toInt()
        val price = binding.rbPrice.rating.toInt()
        val comment = binding.etComment.text.toString()
        val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        val isAnonymous = binding.cbAnonymous.isChecked

        // İsim belirleme
        val displayName = if (isAnonymous) "Misafir Kullanıcı" else realName

        val randomId = (System.currentTimeMillis() % 10000).toString()

        val newReview = Review(
            id = randomId,
            restaurantId = restaurantId, 
            userId = userId, // YENİ: Hangi kullanıcı attı
            userName = displayName,
            hygieneRating = hygiene,
            tasteRating = taste,
            priceRating = price,
            comment = comment,
            isPositive = isPositive,
            date = date
        )

        binding.btnSubmitReview.isEnabled = false
        firestoreHelper.addReview(restaurantId, newReview,
            onSuccess = {
                Toast.makeText(context, "Değerlendirmeniz kaydedildi!", Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            },
            onFailure = { e ->
                binding.btnSubmitReview.isEnabled = true
                Toast.makeText(context, "Hata: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}