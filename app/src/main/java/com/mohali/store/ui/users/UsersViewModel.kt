package com.mohali.store.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.models.User
import com.mohali.store.data.models.UserRole
import com.mohali.store.data.remote.FirebaseRepository
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        _currentUser.value = prefsManager.getUser()
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            firebaseRepository.getUsers().collect { _users.value = it }
        }
    }

    fun addUser(username: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            val user = User(username = username, email = email, role = role)
            firebaseRepository.registerUser(user, password)
        }
    }

    fun deactivateUser(uid: String) {
        viewModelScope.launch { firebaseRepository.deactivateUser(uid) }
    }

    fun changePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = firebaseRepository.changePassword(newPassword)
            if (result.isSuccess) {
                prefsManager.savePassword(newPassword)
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message)
            }
        }
    }
}
