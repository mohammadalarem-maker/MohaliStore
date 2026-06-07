package com.mohali.store.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.models.User
import com.mohali.store.data.models.UserRole
import com.mohali.store.data.remote.FirebaseRepository
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun login(
        username: String,
        password: String,
        onResult: (success: Boolean, isAdmin: Boolean, error: String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. الدخول كمسؤول محلي
                val result = firebaseRepository.loginAdmin(username, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _currentUser.value = user
                        prefsManager.saveUser(user)
                        onResult(true, user.role == UserRole.ADMIN, null)
                        return@launch
                    }
                }

                // 2. الدخول عبر Firebase
                val email = "$username@mohali.store"
                val fbResult = firebaseRepository.loginWithEmailPassword(email, password)
                
                if (fbResult.isSuccess) {
                    val user = fbResult.getOrNull()
                    if (user != null) {
                        _currentUser.value = user
                        prefsManager.saveUser(user)
                        onResult(true, user.role == UserRole.ADMIN, null)
                    } else {
                        onResult(false, false, "حدث خطأ: بيانات المستخدم فارغة من القاعدة")
                    }
                } else {
                    // إظهار سبب رفض فايربيز الحقيقي بدلاً من رسالة عامة
                    val errorMsg = fbResult.exceptionOrNull()?.message ?: "بيانات غير صحيحة"
                    onResult(false, false, "رفض فايربيز: $errorMsg")
                }
            } catch (e: Throwable) {
                // 🔥 هذه المصيدة ستمنع إغلاق التطبيق وتعرض سبب الكراش في الواجهة!
                val crashReason = e.message ?: e.javaClass.simpleName
                onResult(false, false, "كراش النظام: $crashReason")
            }
        }
    }

    fun changePassword(
        newPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = firebaseRepository.changePassword(newPassword)
                if (result.isSuccess) {
                    prefsManager.savePassword(newPassword)
                    onResult(true, null)
                } else {
                    onResult(false, result.exceptionOrNull()?.message)
                }
            } catch (e: Throwable) {
                onResult(false, "كراش: ${e.message}")
            }
        }
    }

    fun isLoggedIn(): Boolean = try { prefsManager.isLoggedIn() } catch(e: Exception) { false }
    fun getSavedUser(): User? = try { prefsManager.getUser() } catch(e: Exception) { null }
}
