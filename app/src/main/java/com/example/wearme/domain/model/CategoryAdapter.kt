package com.example.wearme.domain.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wearme.databinding.ItemCategoryBinding

class CategoriesAdapter(
  private val onCategoryClick: (String) -> Unit
): ListAdapter<String, CategoriesAdapter.ViewHolder>(DiffCallback()) {

  class ViewHolder(val binding: ItemCategoryBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(category: String) {
      binding.categoryName.text = category
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemCategoryBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
    holder.itemView.setOnClickListener {
      onCategoryClick(getItem(position))
    }
  }

  class DiffCallback: DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
  }
}