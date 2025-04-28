package com.example.wearme.domain.model

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wearme.R
import com.example.wearme.databinding.ItemProductBinding
import com.example.wearme.domain.model.api.Cloth

class ItemsAdapter(
    private var cloths: List<Cloth>, private val onInfoClickListener: (Cloth) -> Unit
): RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cloth = cloths[position] // Исправлено: получаем объект Cloth

        // Загрузка изображения
        holder.binding.productImage.load(cloth.photoUrl) { // Используем поле photo_url
            crossfade(300)
            placeholder(R.drawable.placeholder_image)
            error(R.drawable.placeholder_image)
            listener(onSuccess = { _, _ -> }, onError = { _, _ ->
                Log.e("ItemsAdapter", "Error loading image")
            })
        }

        // Заполнение текстовых полей
        holder.binding.productName.text = cloth.name
        holder.binding.ratingText.text = "★ ${cloth.stars}"
        holder.binding.btnInfo.text = "${cloth.matchScore}"
        holder.binding.reviewsText.text = "${cloth.comments} reviews"

        holder.binding.btnInfo.setOnClickListener {
            onInfoClickListener.invoke(cloth)
        }
    }

    override fun getItemCount() = cloths.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Cloth>) {
        cloths = filterUniqueMaxScore(newList).sortedByDescending { it.matchScore }
        notifyDataSetChanged()
    }

    private fun filterUniqueMaxScore(items: List<Cloth>): List<Cloth> {
        return items.groupBy { it.name }.values.mapNotNull { group ->
            group.maxByOrNull { it.matchScore }
        }
    }
}