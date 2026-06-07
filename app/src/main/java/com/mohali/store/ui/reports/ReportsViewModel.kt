package com.mohali.store.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.ExpenseDao
import com.mohali.store.data.local.PurchaseDao
import com.mohali.store.data.local.SaleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class ReportStats(
    val sales: Double = 0.0,
    val purchases: Double = 0.0,
    val expenses: Double = 0.0,
    val cogs: Double = 0.0,
    val grossProfit: Double = 0.0,
    val netProfit: Double = 0.0
)

data class CategoryStat(val category: String, val amount: Double, val percentage: Float)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val saleDao: SaleDao,
    private val purchaseDao: PurchaseDao,
    private val expenseDao: ExpenseDao
) : ViewModel() {

    private val _stats = MutableStateFlow(ReportStats())
    val stats: StateFlow<ReportStats> = _stats

    private val _weeklySales = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val weeklySales: StateFlow<List<Pair<String, Double>>> = _weeklySales

    private val _categoryStats = MutableStateFlow<List<Triple<String, Double, Float>>>(emptyList())
    val categoryStats: StateFlow<List<Triple<String, Double, Float>>> = _categoryStats

    init { loadPeriod(1) }

    fun loadPeriod(period: Int) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val end = cal.timeInMillis
            val start = when (period) {
                0 -> { cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.timeInMillis }
                1 -> { cal.add(Calendar.DAY_OF_YEAR, -7); cal.timeInMillis }
                2 -> { cal.set(Calendar.DAY_OF_MONTH, 1); cal.timeInMillis }
                3 -> { cal.set(Calendar.DAY_OF_YEAR, 1); cal.timeInMillis }
                else -> { cal.add(Calendar.DAY_OF_YEAR, -7); cal.timeInMillis }
            }

            val sales = saleDao.getTotalSalesAmount(start, end) ?: 0.0
            val expenses = expenseDao.getTotalExpenses(start, end) ?: 0.0
            val grossProfit = sales * 0.3 // estimated 30% margin
            val netProfit = grossProfit - expenses

            _stats.value = ReportStats(
                sales = sales,
                purchases = 0.0,
                expenses = expenses,
                cogs = sales * 0.7,
                grossProfit = grossProfit,
                netProfit = netProfit
            )

            loadWeeklyData()
        }
    }

    private fun loadWeeklyData() {
        viewModelScope.launch {
            val dayFormat = SimpleDateFormat("EEE", Locale("ar"))
            val result = mutableListOf<Pair<String, Double>>()
            val cal = Calendar.getInstance()

            for (i in 6 downTo 0) {
                val dayCal = Calendar.getInstance()
                dayCal.add(Calendar.DAY_OF_YEAR, -i)
                dayCal.set(Calendar.HOUR_OF_DAY, 0); dayCal.set(Calendar.MINUTE, 0); dayCal.set(Calendar.SECOND, 0)
                val dayStart = dayCal.timeInMillis
                dayCal.set(Calendar.HOUR_OF_DAY, 23); dayCal.set(Calendar.MINUTE, 59)
                val dayEnd = dayCal.timeInMillis
                val amount = saleDao.getTotalSalesAmount(dayStart, dayEnd) ?: 0.0
                result.add(Pair(dayFormat.format(Date(dayStart)), amount))
            }
            _weeklySales.value = result
        }
    }

    fun exportReport() {
        // PDF export implementation
    }
}
