package com.mohali.store.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.ExpenseDao
import com.mohali.store.data.models.Expense
import com.mohali.store.data.models.ExpenseCategory
import com.mohali.store.data.remote.FirebaseRepository
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firebaseRepository: FirebaseRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    val expenses: StateFlow<List<Expense>> = expenseDao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses

    init { loadMonthTotal() }

    private fun loadMonthTotal() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val end = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val start = cal.timeInMillis
            _totalExpenses.value = expenseDao.getTotalExpenses(start, end) ?: 0.0
        }
    }

    fun addExpense(title: String, amount: Double, category: ExpenseCategory, description: String) {
        viewModelScope.launch {
            val expense = Expense(
                id = UUID.randomUUID().toString(),
                title = title, amount = amount,
                category = category, description = description,
                createdById = prefsManager.getUser()?.uid ?: ""
            )
            expenseDao.insertExpense(expense)
            firebaseRepository.addExpense(expense)
            loadMonthTotal()
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch { expenseDao.deleteExpense(id) }
    }
}
