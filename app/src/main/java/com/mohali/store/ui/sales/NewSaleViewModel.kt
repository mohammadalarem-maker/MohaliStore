package com.mohali.store.ui.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.ProductDao
import com.mohali.store.data.local.SaleDao
import com.mohali.store.data.models.*
import com.mohali.store.data.remote.FirebaseRepository
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewSaleViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val saleDao: SaleDao,
    private val firebaseRepository: FirebaseRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<SaleItem>>(emptyList())
    val cartItems: StateFlow<List<SaleItem>> = _cartItems

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _discount = MutableStateFlow(0.0)
    val discount: StateFlow<Double> = _discount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val searchResults: StateFlow<List<Product>> = _searchQuery
        .debounce(300)
        .flatMapLatest { q -> if (q.isEmpty()) flowOf(emptyList()) else productDao.searchProducts(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subtotal: StateFlow<Double> = _cartItems
        .map { items -> items.sumOf { it.total } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val total: StateFlow<Double> = combine(subtotal, _discount) { sub, disc -> (sub - disc).coerceAtLeast(0.0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    fun onSearch(q: String) { _searchQuery.value = q }
    fun setDiscount(amount: Double) { _discount.value = amount }

    fun addToCart(product: Product) {
        val existing = _cartItems.value.find { it.productId == product.id }
        if (existing != null) {
            _cartItems.update { items ->
                items.map { item ->
                    if (item.productId == product.id) {
                        val newQty = item.quantity + 1
                        item.copy(quantity = newQty, total = newQty * item.unitPrice)
                    } else item
                }
            }
        } else {
            val newItem = SaleItem(
                productId = product.id,
                productName = product.name,
                barcode = product.barcode,
                quantity = 1,
                unitPrice = product.sellPrice,
                total = product.sellPrice
            )
            _cartItems.update { it + newItem }
        }
    }

    fun increaseQty(productId: String) {
        _cartItems.update { items ->
            items.map { item ->
                if (item.productId == productId) {
                    val newQty = item.quantity + 1
                    item.copy(quantity = newQty, total = newQty * item.unitPrice)
                } else item
            }
        }
    }

    fun decreaseQty(productId: String) {
        _cartItems.update { items ->
            items.mapNotNull { item ->
                if (item.productId == productId) {
                    if (item.quantity <= 1) null
                    else {
                        val newQty = item.quantity - 1
                        item.copy(quantity = newQty, total = newQty * item.unitPrice)
                    }
                } else item
            }
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.update { it.filter { item -> item.productId != productId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _discount.value = 0.0
        _searchQuery.value = ""
    }

    fun completeSale(
        customerName: String,
        customerPhone: String,
        paidAmount: Double,
        paymentMethod: PaymentMethod,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val user = prefsManager.getUser()
            val invoiceNumber = "INV-${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())}"
            val totalAmount = total.value

            val sale = Sale(
                id = UUID.randomUUID().toString(),
                invoiceNumber = invoiceNumber,
                customerName = customerName,
                customerPhone = customerPhone,
                items = _cartItems.value,
                subtotal = subtotal.value,
                discount = _discount.value,
                total = totalAmount,
                paidAmount = paidAmount,
                changeAmount = (paidAmount - totalAmount).coerceAtLeast(0.0),
                paymentMethod = paymentMethod,
                cashierId = user?.uid ?: "",
                cashierName = user?.username ?: "",
                createdAt = System.currentTimeMillis(),
                status = SaleStatus.COMPLETED
            )

            // Save locally
            saleDao.insertSale(sale)

            // Decrease stock locally
            _cartItems.value.forEach { item ->
                productDao.decreaseStock(item.productId, item.quantity)
            }

            // Sync to Firebase
            firebaseRepository.addSale(sale)

            // Check low stock after sale
            _cartItems.value.forEach { item ->
                val product = productDao.getProductById(item.productId)
                if (product != null && product.quantity <= product.minQuantity) {
                    firebaseRepository.sendLowStockNotification(product)
                }
            }

            _isLoading.value = false
            clearCart()
            onSuccess()
        }
    }
}
