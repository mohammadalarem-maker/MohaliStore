package com.mohali.store.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.ProductDao
import com.mohali.store.data.models.Product
import com.mohali.store.data.models.ProductCategory
import com.mohali.store.data.remote.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: StateFlow<ProductCategory?> = _selectedCategory

    val products: StateFlow<List<Product>> = combine(
        productDao.getAllProducts(),
        _searchQuery,
        _selectedCategory
    ) { products, query, category ->
        products.filter { product ->
            val matchesQuery = query.isEmpty() ||
                product.name.contains(query, ignoreCase = true) ||
                product.barcode.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true)
            val matchesCategory = category == null || product.category == category
            matchesQuery && matchesCategory
        }
    }
    .flowOn(Dispatchers.IO)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchChange(query: String) { _searchQuery.value = query }
    fun onCategoryFilter(category: ProductCategory?) { _selectedCategory.value = category }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                productDao.deleteProduct(productId)
                firebaseRepository.deleteProduct(productId)
            }
        }
    }
}
