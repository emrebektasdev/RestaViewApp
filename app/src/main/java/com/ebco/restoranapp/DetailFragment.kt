package com.ebco.restoranapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ebco.restoranapp.databinding.FragmentDetailBinding
import com.ebco.restoranapp.databinding.ItemReviewBinding
import com.google.firebase.auth.FirebaseAuth

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var restaurantId: String = ""
    private var tableName: String = ""
    private val firestoreHelper = FirestoreHelper()
    private val auth = FirebaseAuth.getInstance() // YENİ: Auth Eklendi

    companion object {
        private const val ARG_ID = "restaurant_id"
        private const val ARG_TABLE = "table_name"
        
        fun newInstance(id: String, tableName: String) = DetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID, id)
                putString(ARG_TABLE, tableName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restaurantId = arguments?.getString(ARG_ID) ?: ""
        tableName = arguments?.getString(ARG_TABLE) ?: "Masa Belirtilmedi"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRestaurantDetails()
    }

    private fun loadRestaurantDetails() {
        val cleanId = restaurantId.trim()
        
        firestoreHelper.getRestaurantById(cleanId, 
            onSuccess = { restaurant ->
                if (restaurant != null) {
                    updateUI(restaurant)
                } else {
                    showToast("Restoran bulunamadı (ID: $cleanId)")
                    parentFragmentManager.popBackStack()
                }
            },
            onFailure = { e ->
                showToast("Hata: ${e.localizedMessage}")
            }
        )
    }

    private fun updateUI(restaurant: Restaurant) {
        if (_binding == null) return

        with(binding) {
            tvDetailName.text = restaurant.name
            tvTableName.text = "Seçili: $tableName"
            tvDetailDesc.text = restaurant.description
            
            if (restaurant.imageUrl.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(restaurant.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.bg_rating)
                    .into(ivDetailImage)
            } else {
                ivDetailImage.setImageResource(restaurant.imageResId)
            }
            
            tvDetailHygiene.text = "Hijyen: ${restaurant.hygieneScore}"
            tvDetailTaste.text = "Lezzet: ${restaurant.tasteScore}"
            tvDetailPrice.text = "Fiyat: ${restaurant.priceScore}"

            btnCallWaiter.setOnClickListener {
                sendRequestToFirestore("Garson")
            }

            btnRequestBill.setOnClickListener {
                sendRequestToFirestore("Hesap")
            }

            btnSpecialRequest.setOnClickListener {
                showSpecialRequestDialog()
            }

            btnAddReview.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ReviewFragment.newInstance(restaurant.id))
                    .addToBackStack(null)
                    .commit()
            }
            
            setupCommentsList(restaurant.reviews)
        }
    }

    private fun setupCommentsList(reviews: List<Review>) {
        val adapter = object : androidx.recyclerview.widget.RecyclerView.Adapter<ReviewViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
                val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ReviewViewHolder(binding)
            }

            override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
                holder.bind(reviews[position])
            }

            override fun getItemCount(): Int = reviews.size
        }
        
        binding.rvComments.layoutManager = LinearLayoutManager(context)
        binding.rvComments.adapter = adapter
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvUserName.text = review.userName
            binding.tvDate.text = review.date
            binding.tvComment.text = review.comment
            binding.tvRatings.text = "H:${review.hygieneRating} L:${review.tasteRating} F:${review.priceRating}"
            binding.ivVerified.visibility = if (review.isPositive) View.VISIBLE else View.GONE
            
            if (review.ownerReply.isNotEmpty()) {
                binding.layoutOwnerReply.visibility = View.VISIBLE
                binding.tvOwnerReplyText.text = review.ownerReply
            } else {
                binding.layoutOwnerReply.visibility = View.GONE
            }

            // YENİ: Silme Butonu Kontrolü
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != null && review.userId == currentUserId) {
                binding.btnDeleteReview.visibility = View.VISIBLE
                binding.btnDeleteReview.setOnClickListener {
                    confirmDelete(review.id)
                }
            } else {
                binding.btnDeleteReview.visibility = View.GONE
            }
        }
    }

    private fun confirmDelete(reviewId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Yorumu Sil")
            .setMessage("Bu yorumu silmek istediğinize emin misiniz?")
            .setPositiveButton("Sil") { _, _ ->
                deleteReview(reviewId)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun deleteReview(reviewId: String) {
        firestoreHelper.deleteReview(restaurantId, reviewId,
            onSuccess = {
                showToast("Yorum silindi")
                loadRestaurantDetails() // Sayfayı yenile
            },
            onFailure = {
                showToast("Silinemedi: ${it.message}")
            }
        )
    }

    private fun showSpecialRequestDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_special_request, null)
        val etNote = dialogView.findViewById<EditText>(R.id.etNote)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Özel İstek & Not")
            .setView(dialogView)
            .setPositiveButton("Gönder") { _, _ ->
                val note = etNote.text.toString()
                if (note.isNotEmpty()) {
                    sendRequestToFirestore("Özel İstek", note)
                } else {
                    showToast("Lütfen bir not yazın.")
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun sendRequestToFirestore(type: String, note: String = "") {
        firestoreHelper.sendRequest(restaurantId, tableName, type, note,
            onSuccess = {
                showToast("$type isteği başarıyla gönderildi!")
            },
            onFailure = {
                showToast("İstek gönderilirken hata oluştu: ${it.message}")
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}