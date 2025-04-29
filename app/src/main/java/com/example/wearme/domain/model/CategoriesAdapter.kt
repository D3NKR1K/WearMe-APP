package com.example.wearme.domain.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wearme.databinding.ItemCategoryBinding
import com.example.wearme.domain.model.api.Category

class CategoriesAdapter(
    private val onCategoryClick: (Category) -> Unit
): ListAdapter<Category, CategoriesAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemCategoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.categoryName.text = category.name
            binding.categoryIcon.setImageResource(category.iconResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
        holder.itemView.setOnClickListener { onCategoryClick(category) }
    }

    class DiffCallback: DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}