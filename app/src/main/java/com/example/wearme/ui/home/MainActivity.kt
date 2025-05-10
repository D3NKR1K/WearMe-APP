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
import com.example.wearme.ui.bio.MeasurementsEditActivity
import com.example.wearme.ui.bio.ProfileActivity
import kotlinx.coroutines.launch
import retrofit2.Call

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    data class MeasurementCategory<T>(
        val apiCall: () -> Call<T>, val categoryId: Int
    )

    private val categoryMap = mapOf(
        "Upper" to MeasurementCategory(
            apiCall = { RetrofitInstance.measurementsApiService.getUpper() }, categoryId = 1
        ), "Lower" to MeasurementCategory(
            apiCall = { RetrofitInstance.measurementsApiService.getUnder() }, categoryId = 2
        ), "Footwear" to MeasurementCategory(
            apiCall = { RetrofitInstance.measurementsApiService.getFoot() }, categoryId = 3
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupClickListeners()
    }

    private fun setupAdapters() {
        itemsAdapter = ItemsAdapter(emptyList()) { openClothDetails(it) }
        binding.itemsRecyclerView.apply {
            adapter = itemsAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 1)
        }

        categoriesAdapter = CategoriesAdapter { category ->
            categoryMap[category.name]?.let { measurementCategory ->
                handleCategoryClick(category, measurementCategory)
            }
        }

        binding.categoriesRecyclerView.apply {
            adapter = categoriesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            visibility = View.VISIBLE
        }

        categoriesAdapter.submitList(
            listOf(
                Category("Upper", R.drawable.ic_upper),
                Category("Lower", R.drawable.ic_lower),
                Category("Footwear", R.drawable.ic_footwear)
            )
        )

        binding.itemsRecyclerView.visibility = View.GONE
    }

    private fun <T> handleCategoryClick(
        category: Category, measurementCategory: MeasurementCategory<T>
    ) {
        measurementCategory.apiCall().enqueue(
            GetMeasurementsCallback(activity = this, onSuccess = {
                handleMeasurementsResult(
                    category.name, measurementCategory.categoryId, true
                )
            }, onError = {
                handleMeasurementsResult(
                    category.name, measurementCategory.categoryId, false
                )
            })
        )
    }


    private fun handleMeasurementsResult(
        category: String, categoryId: Int, hasMeasurements: Boolean
    ) {
        if (hasMeasurements) {
            lifecycleScope.launch {
                try {
                    val response =
                        RetrofitInstance.clothesApiService.getClothes(globalCategoryId = categoryId)

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
                startActivity(Intent(this, MeasurementsEditActivity::class.java))
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

        updateCatalogButtonState()

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

        updateCatalogButtonState()
    }

    private fun updateCatalogButtonState() {
        val isListVisible = binding.categoriesRecyclerView.isVisible
        binding.productsCatalogButton.apply {
            isEnabled = !isListVisible
            alpha = if (isEnabled) 1.0f else 0.5f
        }
    }
}
