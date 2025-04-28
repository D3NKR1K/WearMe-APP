package com.example.wearme.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wearme.R
import com.example.wearme.data.network.api.GetMeasurementsCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityMainBinding
import com.example.wearme.domain.model.CategoriesAdapter
import com.example.wearme.domain.model.ItemsAdapter
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
            when (category) {
                "Upper" -> {
                    RetrofitInstance.measurementsApi.getUpper().enqueue(
                        GetMeasurementsCallback(
                            activity = this,
                            onSuccess = { response ->
                                handleMeasurementsResult(category, true)
                            },
                            onError = { handleMeasurementsResult(category, false) },
                            validateValues = { body ->
                                body.chest > 0 && body.waist > 0 && body.hips > 0
                            })
                    )
                }

                "Lower" -> {
                    RetrofitInstance.measurementsApi.getUnder().enqueue(
                        GetMeasurementsCallback(
                            activity = this,
                            onSuccess = { response ->
                                handleMeasurementsResult(category, true)
                            },
                            onError = { handleMeasurementsResult(category, false) },
                            validateValues = { body ->
                                body.waist > 0 && body.hips > 0
                            })
                    )
                }

                "Footwear" -> {
                    RetrofitInstance.measurementsApi.getFoot().enqueue(
                        GetMeasurementsCallback(
                            activity = this,
                            onSuccess = { response ->
                                handleMeasurementsResult(category, true)
                            },
                            onError = { handleMeasurementsResult(category, false) },
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

        categoriesAdapter.submitList(listOf("Upper", "Lower", "Footwear"))
        binding.categoriesRecyclerView.visibility = View.VISIBLE
        binding.itemsRecyclerView.visibility = View.GONE
    }


    private fun handleMeasurementsResult(category: String, hasMeasurements: Boolean) {
        runOnUiThread {
            if (hasMeasurements) {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitInstance.clothesApi.getClothes(
                            globalCategoryId = when (category.lowercase()) {
                                "upper" -> 3
                                "lower" -> 1
                                "footwear" -> 2
                                else -> throw IllegalArgumentException("Invalid category")
                            }, subCategoryId = null, color = null
                        )

                        if (response.isSuccessful) {
                            response.body()?.let { clothes ->
                                itemsAdapter.updateList(clothes)
                                showProducts()
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
    }

    private fun showProducts() {
        binding.categoriesRecyclerView.visibility = View.GONE
        binding.itemsRecyclerView.visibility = View.VISIBLE
    }
}
