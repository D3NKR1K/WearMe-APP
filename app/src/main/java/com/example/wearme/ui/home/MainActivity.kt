package com.example.wearme.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wearme.R
import com.example.wearme.data.network.api.GetMeasurementsCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityMainBinding
import com.example.wearme.domain.model.CategoriesAdapter
import com.example.wearme.domain.model.ItemsAdapter
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.bio.MeasurementsActivity
import com.example.wearme.ui.bio.ProfileActivity

class MainActivity: AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var itemsAdapter: ItemsAdapter
  private lateinit var tokenManager: TokenManager
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

    tokenManager = TokenManager(this)

    tokenManager.getToken() ?: run {
      Log.e("[AUTH]", "Token not found")
      return
    }

    setContentView(binding.root)

    setupAdapters()
    setupClickListeners()
    showInitialData()
  }

  private fun setupAdapters() {
    itemsAdapter = ItemsAdapter(emptyList())
    binding.itemsRecyclerView.apply {
      adapter = itemsAdapter
      layoutManager = GridLayoutManager(this@MainActivity, 2)
    }

    // Categories Adapter
    categoriesAdapter = CategoriesAdapter { category ->
      val token = tokenManager.getToken() ?: return@CategoriesAdapter

      when (category) {
        "Upper" -> {
          RetrofitInstance.measurementsApi.getUpper("Bearer $token").enqueue(
            GetMeasurementsCallback(activity = this, onSuccess = { response ->
              handleMeasurementsResult(category, true)
            }, onError = { handleMeasurementsResult(category, false) }, validateValues = { body ->
              body.chest > 0 && body.waist > 0 && body.hips > 0
            })
          )
        }

        "Lower" -> {
          RetrofitInstance.measurementsApi.getUnder("Bearer $token").enqueue(
            GetMeasurementsCallback(activity = this, onSuccess = { response ->
              handleMeasurementsResult(category, true)
            }, onError = { handleMeasurementsResult(category, false) }, validateValues = { body ->
              body.waist > 0 && body.hips > 0
            })
          )
        }

        "Foot" -> {
          RetrofitInstance.measurementsApi.getFoot("Bearer $token")
            .enqueue(GetMeasurementsCallback(activity = this, onSuccess = { response ->
              handleMeasurementsResult(category, true)
            }, onError = { handleMeasurementsResult(category, false) }, validateValues = { body ->
              body.foot > 0
            }))
        }
      }
    }

    binding.categoriesRecyclerView.apply {
      adapter = categoriesAdapter
      layoutManager = LinearLayoutManager(this@MainActivity)
    }
  }

  private fun handleMeasurementsResult(category: String, hasMeasurements: Boolean) {
    runOnUiThread {
      if (hasMeasurements) {
        itemsAdapter.updateData(categoryData[category] ?: emptyList())
        showProducts()
      } else {
        showMeasurementsDialog(category)
      }
    }
  }

  fun showMeasurementsDialog(category: String) {
    AlertDialog.Builder(this, R.style.CustomAlertDialog).setTitle("No Measurements")
      .setMessage("To view $category items, you need to provide your measurements.")
      .setPositiveButton("Enter Measurements") { dialog, _ ->
        // Переход на экран ввода мерок
        startActivity(Intent(this, MeasurementsActivity::class.java))
        dialog.dismiss()
      }.setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }.create().show()
  }

  fun showNetworkErrorDialog() {
    AlertDialog.Builder(this).setTitle("Connection Error")
      .setMessage("Please check your internet connection")
      .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
  }

  private fun setupClickListeners() {
    binding.productsShowcaseButton.setOnClickListener {
      itemsAdapter.updateData(categoryData["All"] ?: emptyList())
      showProducts()
    }

    binding.productsCatalogButton.setOnClickListener {
      categoriesAdapter.submitList(listOf("Upper", "Lower", "Shoes"))
      binding.categoriesRecyclerView.visibility = View.VISIBLE
      binding.itemsRecyclerView.visibility = View.GONE
    }

    binding.productsProfileButton.setOnClickListener {
      startActivity(Intent(this, ProfileActivity::class.java))
    }
  }

  private fun showInitialData() {
    itemsAdapter.updateData(categoryData["All"] ?: emptyList())
    binding.itemsRecyclerView.visibility = View.VISIBLE
    binding.categoriesRecyclerView.visibility = View.GONE
  }

  private fun showProducts() {
    binding.categoriesRecyclerView.visibility = View.GONE
    binding.itemsRecyclerView.visibility = View.VISIBLE
  }
}
