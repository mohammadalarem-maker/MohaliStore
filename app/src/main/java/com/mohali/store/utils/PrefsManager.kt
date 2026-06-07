package com.mohali.store.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mohali.store.data.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mohali_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        prefs.edit().putString("current_user", gson.toJson(user)).apply()
        prefs.edit().putBoolean("is_logged_in", true).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString("current_user", null) ?: return null
        return try { gson.fromJson(json, User::class.java) } catch (e: Exception) { null }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun savePassword(password: String) {
        prefs.edit().putString("local_password", password).apply()
    }

    fun getLocalPassword(): String = prefs.getString("local_password", "1234567") ?: "1234567"

    fun setLowStockThreshold(value: Int) {
        prefs.edit().putInt("low_stock_threshold", value).apply()
    }

    fun getLowStockThreshold(): Int = prefs.getInt("low_stock_threshold", 5)

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun isNotificationsEnabled(): Boolean = prefs.getBoolean("notifications_enabled", true)

    fun logout() {
        prefs.edit()
            .remove("current_user")
            .putBoolean("is_logged_in", false)
            .apply()
    }

    fun setStoreName(name: String) {
        prefs.edit().putString("store_name", name).apply()
    }

    fun getStoreName(): String = prefs.getString("store_name", "محلي ستور") ?: "محلي ستور"

    fun setTaxRate(rate: Double) {
        prefs.edit().putFloat("tax_rate", rate.toFloat()).apply()
    }

    fun getTaxRate(): Double = prefs.getFloat("tax_rate", 0f).toDouble()

    fun setCurrency(currency: String) {
        prefs.edit().putString("currency", currency).apply()
    }

    fun getCurrency(): String = prefs.getString("currency", "ريال") ?: "ريال"
}
