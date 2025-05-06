package com.example.wearme.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wearme.R
import com.example.wearme.data.network.api.GetMeasurementsCallback
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.databinding.ActivityMainBinding
import com.example.wearme.domain.model.CategoriesAdapter
import com.example.wearme.domain.model.ItemsAdapter
import com.example.wearme.domain.model.api.Category
import com.example.wearme.domain.model.api.Cloth
import com.example.wearme.ui.bio.MeasurementsActivity
import com.example.wearme.ui.bio.ProfileActivity
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupAdapters()
        setupClickListeners()
    }

    private fun setupAdapters() {
        itemsAdapter = ItemsAdapter(emptyList()) { cloth ->
            openClothDetails(cloth)
        }
        binding.itemsRecyclerView.apply {
            adapter = itemsAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 1)
        }

        // Categories Adapter
        categoriesAdapter = CategoriesAdapter { category ->
            when (category.name) {
                "Upper" -> {
                    RetrofitInstance.measurementsApiService.getUpper().enqueue(
                        GetMeasurementsCallback(
                            activity = this,
                            onSuccess = { response ->
                                handleMeasurementsResult(category.name, true)
                            },
                            onError = { handleMeasurementsResult(category.name, false) },
                            validateValues = { body ->
                                body.chest > 0 && body.waist > 0 && body.hips > 0
                            })
                    )
                }

                "Lower" -> {
                    RetrofitInstance.measurementsApiService.getUnder().enqueue(
                        GetMeasurementsCallback(
                            activity = this,
                            onSuccess = { response ->
                                handleMeasurementsResult(category.name, true)
                            },
                            onError = { handleMeasurementsResult(category.name, false) },
                            validateValues = { body ->
                                body.waist > 0 && body.hips > 0
                            })
                    )
                }

                "Footwear" -> {
                    RetrofitInstance.measurementsApiService.getFoot().enqueue(
                        GetMeasurementsCallback(
                            activity = this,
                            onSuccess = { response ->
                                handleMeasurementsResult(category.name, true)
                            },
                            onError = { handleMeasurementsResult(category.name, false) },
                            validateValues = { body ->
                                body.foot > 0
                            })
                    )
                }
            }
        }

        binding.categoriesRecyclerView.apply {
            adapter = categoriesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        val categories = listOf(
            Category("Upper", R.drawable.ic_upper),
            Category("Lower", R.drawable.ic_lower),
            Category("Footwear", R.drawable.ic_footwear)
        )

        categoriesAdapter.submitList(categories)
        binding.categoriesRecyclerView.visibility = View.VISIBLE
        binding.itemsRecyclerView.visibility = View.GONE
    }


    private fun handleMeasurementsResult(category: String, hasMeasurements: Boolean) {
        runOnUiThread {
            if (hasMeasurements) {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitInstance.clothesApiService.getClothes(
                            globalCategoryId = when (category.lowercase()) {
                                "upper" -> 1
                                "lower" -> 2
                                "footwear" -> 3
                                else -> throw IllegalArgumentException("Invalid category")
                            }
                        )

                        if (response.isSuccessful) {
                            response.body()?.let { clothes ->
                                itemsAdapter.updateList(clothes)
                                showProducts(category)
                            }
                        } else {
                            showNetworkErrorDialog()
                        }

                    } catch (e: Exception) {
                        Log.e("Network", "Error fetching clothes", e)
                        showNetworkErrorDialog()
                    }
                }
            } else {
                showMeasurementsDialog(category)
            }
        }
    }

    private fun openClothDetails(cloth: Cloth) {
        val intent = Intent(this, ClothDetailActivity::class.java).apply {
            putExtra("CLOTH_DATA", cloth)
        }
        startActivity(intent)
    }

    fun showMeasurementsDialog(category: String) {
        AlertDialog.Builder(this, R.style.CustomAlertDialog).setTitle("No Measurements")
            .setMessage("To view $category items, you need to provide your measurements.")
            .setPositiveButton("Enter Measurements") { dialog, _ ->
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
        binding.productsProfileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Initially set button state based on visibility
        updateCatalogButtonState()

        // Set up click listener only when it's not disabled
        binding.productsCatalogButton.setOnClickListener {
            if (binding.productsCatalogButton.isEnabled) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showProducts(categoryName: String) {
        binding.categoriesRecyclerView.visibility = View.GONE
        binding.itemsRecyclerView.visibility = View.VISIBLE
        binding.toolbarTitle.text = "Clothes from $categoryName category"

        // After hiding categoriesRecyclerView, update button state
        updateCatalogButtonState()
    }

    // Update the state of the catalog button based on the visibility of categoriesRecyclerView
    private fun updateCatalogButtonState() {
        if (binding.categoriesRecyclerView.isVisible) {
            binding.productsCatalogButton.isEnabled = false
            binding.productsCatalogButton.alpha = 0.5f
        } else {
            binding.productsCatalogButton.isEnabled = true
            binding.productsCatalogButton.alpha = 1.0f
        }
    }
}
