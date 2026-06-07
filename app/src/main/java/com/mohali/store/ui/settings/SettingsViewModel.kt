package com.mohali.store.ui.settings

import androidx.lifecycle.ViewModel
import com.mohali.store.data.remote.FirebaseRepository
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _storeName = MutableStateFlow(prefsManager.getStoreName())
    val storeName: StateFlow<String> = _storeName

    private val _currency = MutableStateFlow(prefsManager.getCurrency())
    val currency: StateFlow<String> = _currency

    private val _taxRate = MutableStateFlow(prefsManager.getTaxRate())
    val taxRate: StateFlow<Double> = _taxRate

    private val _lowStockThreshold = MutableStateFlow(prefsManager.getLowStockThreshold())
    val lowStockThreshold: StateFlow<Int> = _lowStockThreshold

    private val _notificationsEnabled = MutableStateFlow(prefsManager.isNotificationsEnabled())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    fun setStoreName(name: String) { _storeName.value = name; prefsManager.setStoreName(name) }
    fun setCurrency(currency: String) { _currency.value = currency; prefsManager.setCurrency(currency) }
    fun setTaxRate(rate: Double) { _taxRate.value = rate; prefsManager.setTaxRate(rate) }
    fun setLowStockThreshold(threshold: Int) { _lowStockThreshold.value = threshold; prefsManager.setLowStockThreshold(threshold) }
    fun setNotificationsEnabled(enabled: Boolean) { _notificationsEnabled.value = enabled; prefsManager.setNotificationsEnabled(enabled) }
    fun logout() { firebaseRepository.logout(); prefsManager.logout() }
}
