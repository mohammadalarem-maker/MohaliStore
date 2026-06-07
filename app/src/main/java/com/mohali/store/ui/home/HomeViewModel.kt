package com.mohali.store.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.ProductDao
import com.mohali.store.data.local.SaleDao
import com.mohali.store.data.models.DashboardStats
import com.mohali.store.data.models.User
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val saleDao: SaleDao,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats                           
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser                        
    
    private val _lowStockCount = MutableStateFlow(0)
    val lowStockCount: StateFlow<Int> = _lowStockCount

    private val _unreadNotifications = MutableStateFlow(0)
    val unreadNotifications: StateFlow<Int> = _unreadNotifications

    init {
        try {
            _currentUser.value = prefsManager.getUser()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        loadStats()
        observeLowStock()
    }

    private fun loadStats() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val cal = Calendar.getInstance()
                    val endOfDay = cal.timeInMillis
                    cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
                    
                    val calStart = Calendar.getInstance()
                    calStart.set(Calendar.HOUR_OF_DAY, 0); calStart.set(Calendar.MINUTE, 0); calStart.set(Calendar.SECOND, 0)
                    val startOfDay = calStart.timeInMillis
                    
                    calStart.set(Calendar.DAY_OF_MONTH, 1)
                    val startOfMonth = calStart.timeInMillis

                    val todaySales = saleDao.getTotalSalesAmount(startOfDay, endOfDay) ?: 0.0
                    val todayTx = saleDao.getSalesCount(startOfDay, endOfDay)
                    val monthSales = saleDao.getTotalSalesAmount(startOfMonth, endOfDay) ?: 0.0
                    val totalProducts = productDao.getTotalProductsCount()

                    withContext(Dispatchers.Main) {
                        _stats.value = DashboardStats(
                            todaySales = todaySales,
                            todayTransactions = todayTx,
                            monthSales = monthSales,
                            totalProducts = totalProducts
                        )
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun observeLowStock() {                                                 
        viewModelScope.launch {
            try {
                productDao.getLowStockProducts()
                    .flowOn(Dispatchers.IO) // جلب البيانات في الخلفية
                    .catch { e -> e.printStackTrace() }
                    .collect { products ->
                        withContext(Dispatchers.Main) { // التحديث الحتمي على الواجهة الرئيسية
                            _lowStockCount.value = products.size
                        }
                    }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
