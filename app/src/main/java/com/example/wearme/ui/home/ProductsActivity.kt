package com.example.wearme.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wearme.databinding.ActivityMainBinding
import com.example.wearme.domain.model.CategoriesAdapter
import com.example.wearme.domain.model.ProductsAdapter
import com.example.wearme.ui.bio.ProfileActivity

class ProductsActivity: AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var productsAdapter: ProductsAdapter
  private lateinit var categoriesAdapter: CategoriesAdapter

  private val categoryData = mapOf(
    "All" to listOf(
      "http://79.174.82.23:8000/static/images/1.webp",
      "http://79.174.82.23:8000/static/images/2.webp",
      "http://79.174.82.23:8000/static/images/3.webp"
    ), "Upper" to listOf(
      "http://79.174.82.23:8000/static/images/1.webp",
      "http://79.174.82.23:8000/static/images/3.webp"
    ), "Lower" to listOf("http://79.174.82.23:8000/static/images/2.webp")
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupAdapters()
    setupClickListeners()
    showInitialData()
  }

  private fun setupAdapters() {
    // Products Adapter
    productsAdapter = ProductsAdapter(emptyList())
    binding.productsRecyclerView.apply {
      adapter = productsAdapter
      layoutManager = GridLayoutManager(this@ProductsActivity, 2)
    }

    // Categories Adapter
    categoriesAdapter = CategoriesAdapter { category ->
      productsAdapter.updateData(categoryData[category] ?: emptyList())
      showProducts()
    }
    binding.categoriesRecyclerView.apply {
      adapter = categoriesAdapter
      layoutManager = LinearLayoutManager(this@ProductsActivity)
    }
  }

  private fun setupClickListeners() {
    binding.productsShowcaseButton.setOnClickListener {
      productsAdapter.updateData(categoryData["All"] ?: emptyList())
      showProducts()
    }

    binding.productsCatalogButton.setOnClickListener {
      categoriesAdapter.submitList(listOf("Upper", "Lower", "Shoes"))
      binding.categoriesRecyclerView.visibility = View.VISIBLE
      binding.productsRecyclerView.visibility = View.GONE
    }

    binding.productsProfileButton.setOnClickListener {
      startActivity(Intent(this, ProfileActivity::class.java))
    }
  }

  private fun showInitialData() {
    productsAdapter.updateData(categoryData["All"] ?: emptyList())
    binding.productsRecyclerView.visibility = View.VISIBLE
    binding.categoriesRecyclerView.visibility = View.GONE
  }

  private fun showProducts() {
    binding.categoriesRecyclerView.visibility = View.GONE
    binding.productsRecyclerView.visibility = View.VISIBLE
  }
}