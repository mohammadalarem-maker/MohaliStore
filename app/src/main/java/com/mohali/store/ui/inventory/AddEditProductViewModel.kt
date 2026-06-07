package com.mohali.store.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.ProductDao
import com.mohali.store.data.models.Product
import com.mohali.store.data.models.ProductCategory
import com.mohali.store.data.remote.FirebaseRepository
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ProductFormState(
    val name: String = "",
    val barcode: String = "",
    val brand: String = "",
    val model: String = "",
    val color: String = "",
    val storage: String = "",
    val category: ProductCategory = ProductCategory.PHONES,
    val buyPrice: String = "",
    val sellPrice: String = "",
    val quantity: String = "",
    val minQuantity: String = "5",
    val location: String = "",
    val warrantyMonths: String = "0",
    val description: String = ""
)

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val firebaseRepository: FirebaseRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProductFormState())
    val state: StateFlow<ProductFormState> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var editingProductId: String? = null

    fun loadProduct(id: String) {
        viewModelScope.launch {
            editingProductId = id
            val product = productDao.getProductById(id) ?: return@launch
            _state.value = ProductFormState(
                name = product.name,
                barcode = product.barcode,
                brand = product.brand,
                model = product.model,
                color = product.color,
                storage = product.storage,
                category = product.category,
                buyPrice = product.buyPrice.toString(),
                sellPrice = product.sellPrice.toString(),
                quantity = product.quantity.toString(),
                minQuantity = product.minQuantity.toString(),
                location = product.location,
                warrantyMonths = product.warrantyMonths.toString(),
                description = product.description
            )
        }
    }

    fun onNameChange(v: String)         { _state.update { it.copy(name = v) } }
    fun onBarcodeChange(v: String)      { _state.update { it.copy(barcode = v) } }
    fun onBrandChange(v: String)        { _state.update { it.copy(brand = v) } }
    fun onModelChange(v: String)        { _state.update { it.copy(model = v) } }
    fun onColorChange(v: String)        { _state.update { it.copy(color = v) } }
    fun onStorageChange(v: String)      { _state.update { it.copy(storage = v) } }
    fun onCategoryChange(v: ProductCategory) { _state.update { it.copy(category = v) } }
    fun onBuyPriceChange(v: String)     { _state.update { it.copy(buyPrice = v) } }
    fun onSellPriceChange(v: String)    { _state.update { it.copy(sellPrice = v) } }
    fun onQuantityChange(v: String)     { _state.update { it.copy(quantity = v) } }
    fun onMinQuantityChange(v: String)  { _state.update { it.copy(minQuantity = v) } }
    fun onLocationChange(v: String)     { _state.update { it.copy(location = v) } }
    fun onWarrantyChange(v: String)     { _state.update { it.copy(warrantyMonths = v) } }
    fun onDescriptionChange(v: String)  { _state.update { it.copy(description = v) } }

    fun saveProduct(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.name.isBlank()) { _error.value = "يرجى إدخال اسم المنتج"; return }
        if (s.sellPrice.isBlank()) { _error.value = "يرجى إدخال سعر البيع"; return }
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            val product = Product(
                id = editingProductId ?: UUID.randomUUID().toString(),
                name = s.name.trim(),
                barcode = s.barcode.trim(),
                brand = s.brand.trim(),
                model = s.model.trim(),
                color = s.color.trim(),
                storage = s.storage.trim(),
                category = s.category,
                buyPrice = s.buyPrice.toDoubleOrNull() ?: 0.0,
                sellPrice = s.sellPrice.toDoubleOrNull() ?: 0.0,
                quantity = s.quantity.toIntOrNull() ?: 0,
                minQuantity = s.minQuantity.toIntOrNull() ?: 5,
                location = s.location.trim(),
                warrantyMonths = s.warrantyMonths.toIntOrNull() ?: 0,
                description = s.description.trim(),
                updatedAt = System.currentTimeMillis()
            )
            productDao.insertProduct(product)
            firebaseRepository.addProduct(product)

            // Check low stock after save
            if (product.quantity <= product.minQuantity) {
                firebaseRepository.sendLowStockNotification(product)
            }
            _isLoading.value = false
            onSuccess()
        }
    }
}
