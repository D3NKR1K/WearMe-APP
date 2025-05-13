package com.example.wearme.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wearme.R
import com.example.wearme.data.network.api.GetMeasurementsCallback
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.data.remote.IdsRequest
import com.example.wearme.databinding.ActivityMainBinding
import com.example.wearme.domain.model.CategoriesAdapter
import com.example.wearme.domain.model.ItemsAdapter
import com.example.wearme.domain.model.api.Category
import com.example.wearme.domain.model.api.Cloth
import com.example.wearme.ui.bio.MeasurementsEditActivity
import com.example.wearme.ui.bio.ProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import retrofit2.Call

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    private val categoryIds = mutableSetOf<Int>()
    private val colorIds = mutableSetOf<Int>()

    private val categoryNames = mutableSetOf<String>()
    private var colorNames = mutableSetOf<String>()

    private val categoryDict = mutableMapOf<Int, String>()
    private val colorDict = mutableMapOf<Int, String>()

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

        binding.openFiltersButton.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.filter_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)
        val colorSpinner: Spinner = view.findViewById(R.id.colorSpinner)
        val applyFiltersButton: Button = view.findViewById(R.id.applyFiltersButton)

        // Заполняем спиннеры данными
        categorySpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categoryNames.toList()
        )

        colorSpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, colorNames.toList()
        )

        // Обработка нажатия на кнопку "Применить фильтры"
        applyFiltersButton.setOnClickListener {
            val selectedCategoryId =
                getIdFromName(categorySpinner.selectedItem.toString(), categoryDict)
            val selectedColorId = getIdFromName(colorSpinner.selectedItem.toString(), colorDict)

            applyFilters(selectedCategoryId, selectedColorId)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun getIdFromName(name: String, dict: Map<Int, String>): Int {
        return dict.entries.find { it.value == name }?.key ?: -1
    }

    private fun applyFilters(categoryId: Int, colorId: Int) {
        lifecycleScope.launch {
            try {
                itemsAdapter.applyFilters(categoryId, colorId)
            } catch (e: Exception) {
                Log.e("Filters", "Error applying filters", e)
            }
        }
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

        val defaultCategories = listOf(
            Category("Upper", R.drawable.ic_upper, getString(R.string.upper)),
            Category("Lower", R.drawable.ic_lower, getString(R.string.lower)),
            Category("Footwear", R.drawable.ic_footwear, getString(R.string.footwear))
        )
        categoriesAdapter.submitList(defaultCategories)

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

    private suspend fun updateDictionaries(clothes: List<Cloth>) {
        categoryIds.clear()
        colorIds.clear()

        clothes.forEach { cloth ->
            cloth.category?.let { categoryIds.add(it) }
            cloth.color?.let { colorIds.add(it) }
        }

        val dataResponse = RetrofitInstance.clothesApiService.getClothesData(
            IdsRequest(categoryIds.toList(), colorIds.toList())
        )

        categoryNames.clear()
        colorNames.clear()
        dataResponse.body()?.categories?.let { categoryNames.addAll(it) }
        dataResponse.body()?.colors?.let { colorNames.addAll(it) }

        colorNames = colorNames.filterNotNull().toMutableSet()

        categoryDict.clear()
        colorDict.clear()
        categoryDict.putAll(categoryIds.zip(categoryNames).toMap())
        colorDict.putAll(colorIds.zip(colorNames).toMap())
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
                            updateDictionaries(clothes)
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
        binding.openFiltersButton.apply {
            isEnabled = !isListVisible
            alpha = if (isEnabled) 1.0f else 0.5f
        }
    }
}
