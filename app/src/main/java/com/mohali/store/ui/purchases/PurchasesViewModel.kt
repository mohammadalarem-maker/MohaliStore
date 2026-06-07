package com.mohali.store.ui.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.PurchaseDao
import com.mohali.store.data.models.Purchase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(private val purchaseDao: PurchaseDao) : ViewModel() {
    val purchases: StateFlow<List<Purchase>> = purchaseDao.getAllPurchases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
