package com.example.wearme.domain.model

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wearme.R
import com.example.wearme.databinding.ItemProductBinding
import com.example.wearme.domain.model.api.Cloth

class ItemsAdapter(
    private var cloths: List<Cloth>,
    private val onInfoClickListener: (Cloth) -> Unit,
): RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n", "QueryPermissionsNeeded")
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
        holder.binding.ratingText.text = "${cloth.stars}"
        holder.binding.reviewsText.text = "${cloth.comments} reviews"

        holder.binding.btnInfo.setOnClickListener {
            onInfoClickListener.invoke(cloth)
        }

        holder.binding.btnBuy.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = cloth.url.toUri()
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                holder.itemView.context.startActivity(intent)

//                // Проверяем возможность обработки ссылки
//                if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
//                    holder.itemView.context.startActivity(intent)
//                } else {
//                    Toast.makeText(
//                        holder.itemView.context, "No app to handle this link", Toast.LENGTH_SHORT
//                    ).show()
//                }
            } catch (e: Exception) {
                Log.e("ItemsAdapter", "Error opening link: ${e.message}")
                Toast.makeText(
                    holder.itemView.context, "Invalid link", Toast.LENGTH_SHORT
                ).show()
            }
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