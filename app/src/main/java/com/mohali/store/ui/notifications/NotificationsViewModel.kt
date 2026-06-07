package com.mohali.store.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.models.AppNotification
import com.mohali.store.data.remote.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    init {
        viewModelScope.launch {
            firebaseRepository.getNotifications().collect { _notifications.value = it }
        }
    }

    fun clearAll() { _notifications.value = emptyList() }
}
