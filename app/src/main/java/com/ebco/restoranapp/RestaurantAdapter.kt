package com.ebco.restoranapp

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ebco.restoranapp.databinding.ItemRestaurantBinding

class RestaurantAdapter(
    private var restaurants: List<Restaurant>,
    private val onClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant) {
            binding.tvRestaurantName.text = restaurant.name
            binding.tvRestaurantCity.text = restaurant.city
            
            // Resim Yükleme Mantığı (Glide)
            if (restaurant.imageUrl.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(restaurant.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.bg_rating) // Yüklenirken gösterilecek placeholder
                    .into(binding.ivRestaurantImage)
            } else {
                binding.ivRestaurantImage.setImageResource(restaurant.imageResId)
            }
            
            // RW Puanı
            binding.tvRating.text = restaurant.rwScore.toString()

            // Yeni Eklenen Detaylı Puanlar (Tam İsimleriyle)
            binding.tvHygieneScore.text = "Hijyen: ${restaurant.hygieneScore}"
            binding.tvTasteScore.text = "Lezzet: ${restaurant.tasteScore}"
            binding.tvPriceScore.text = "Fiyat: ${restaurant.priceScore}"

            // Harita İkonuna Tıklama Olayı
            binding.ivMap.setOnClickListener {
                val label = Uri.encode(restaurant.name)
                val gmmIntentUri = Uri.parse("geo:${restaurant.lat},${restaurant.lng}?q=${restaurant.lat},${restaurant.lng}($label)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                
                try {
                    binding.root.context.startActivity(mapIntent)
                } catch (e: Exception) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, 
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=${restaurant.lat},${restaurant.lng}"))
                    binding.root.context.startActivity(browserIntent)
                }
            }

            // Kartın geneline tıklama (Detay sayfasına git)
            binding.root.setOnClickListener {
                onClick(restaurant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurants[position])
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateList(newList: List<Restaurant>) {
        restaurants = newList
        notifyDataSetChanged()
    }
}
