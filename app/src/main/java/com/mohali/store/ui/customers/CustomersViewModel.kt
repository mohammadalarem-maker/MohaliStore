package com.mohali.store.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.CustomerDao
import com.mohali.store.data.models.Customer
import com.mohali.store.data.remote.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val customers: StateFlow<List<Customer>> = _searchQuery
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isEmpty()) customerDao.getAllCustomers()
            else customerDao.searchCustomers(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearch(q: String) { _searchQuery.value = q }

    fun addCustomer(name: String, phone: String, email: String) {
        viewModelScope.launch {
            val customer = Customer(id = UUID.randomUUID().toString(), name = name, phone = phone, email = email)
            customerDao.insertCustomer(customer)
            firebaseRepository.addCustomer(customer)
        }
    }
}
