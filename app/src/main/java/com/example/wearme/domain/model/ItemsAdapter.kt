package com.example.wearme.domain.model

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wearme.R
import com.example.wearme.databinding.ItemProductBinding

class ItemsAdapter(private var items: List<String>):
  RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

  class ViewHolder(val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemProductBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val imageUrl = items[position]
    holder.binding.productImage.load(imageUrl) {
      crossfade(300)
      placeholder(R.drawable.placeholder_image)
      error(R.drawable.placeholder_image) // Добавлено
      listener(onSuccess = { _, _ -> }, onError = { _, throwable ->
        Log.e("ProductsAdapter", "Error loading image")
      })
    }
  }

  override fun getItemCount() = items.size

  @SuppressLint("NotifyDataSetChanged")
  fun updateData(newItems: List<String>) {
    items = newItems
    notifyDataSetChanged()
  }
}
