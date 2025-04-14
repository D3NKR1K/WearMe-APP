package com.example.wearme.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wearme.databinding.ActivityMainBinding
import com.example.wearme.domain.model.CategoriesAdapter
import com.example.wearme.domain.model.ProductsAdapter
import com.example.wearme.ui.bio.ProfileActivity

class ProductsActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var productsAdapter: ProductsAdapter
  private lateinit var categoriesAdapter: CategoriesAdapter

  // Data for categories and their corresponding product images
  private val categoryData = mapOf(
    "All" to listOf(
      "http://79.174.82.23:8000/static/images/1.webp",
      "http://79.174.82.23:8000/static/images/2.webp",
      "http://79.174.82.23:8000/static/images/3.webp"
    ),
    "Upper" to listOf(
      "http://79.174.82.23:8000/static/images/1.webp",
      "http://79.174.82.23:8000/static/images/3.webp"
    ),
    "Lower" to listOf("http://79.174.82.23:8000/static/images/2.webp")
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    Log.d("ProductsActivity", "onCreate called")

    setupAdapters()
    setupClickListeners()
    showProducts("All") // Directly show all products on initial load
  }

  private fun setupAdapters() {
    Log.d("ProductsActivity", "setupAdapters called")
    // Products Adapter setup
    productsAdapter = ProductsAdapter(emptyList())
    binding.productsRecyclerView.apply {
      adapter = productsAdapter
      layoutManager = GridLayoutManager(this@ProductsActivity, 2)
    }

    // Categories Adapter setup
    categoriesAdapter = CategoriesAdapter { category ->
      Log.d("ProductsActivity", "Category selected: $category")
      showProducts(category) // Update products when a category is selected
    }
    binding.categoriesRecyclerView.apply {
      adapter = categoriesAdapter
      layoutManager = LinearLayoutManager(this@ProductsActivity)
    }
  }

  private fun setupClickListeners() {
    Log.d("ProductsActivity", "setupClickListeners called")

    // Set click listener for the showcase button
    binding.productsShowcaseButton.setOnClickListener {
      Log.d("ProductsActivity", "Showcase button clicked")
      showProducts("All")
    }

    // Set click listener for the catalog button
    binding.productsCatalogButton.setOnClickListener {
      Log.d("ProductsActivity", "Catalog button clicked")
      showCategories()
    }

    // Set click listener for the profile button
    binding.productsProfileButton.setOnClickListener {
      Log.d("ProductsActivity", "Profile button clicked")
      startActivity(Intent(this, ProfileActivity::class.java))
    }
  }

  private fun showProducts(category: String) {
    Log.d("ProductsActivity", "showProducts called with category: $category")
    productsAdapter.updateData(categoryData[category] ?: emptyList())
    binding.categoriesRecyclerView.visibility = View.GONE
    binding.productsRecyclerView.visibility = View.VISIBLE
  }

  private fun showCategories() {
    Log.d("ProductsActivity", "showCategories called")
    categoriesAdapter.submitList(listOf("Upper", "Lower", "Shoes"))
    binding.categoriesRecyclerView.visibility = View.VISIBLE
    binding.productsRecyclerView.visibility = View.GONE
  }
}
