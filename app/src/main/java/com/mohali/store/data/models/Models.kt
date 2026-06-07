package com.mohali.store.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

class RoleConverter {
    @TypeConverter
    fun fromRole(role: UserRole): String = role.name
    @TypeConverter
    fun toRole(role: String): UserRole = UserRole.valueOf(role)
}

// ================= USER =================
@Entity(tableName = "users")
@androidx.room.TypeConverters(RoleConverter::class)
data class User(
    @PrimaryKey val uid: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CASHIER,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = 0
)

enum class UserRole { ADMIN, MANAGER, CASHIER, VIEWER }

// ================= PRODUCT =================
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val barcode: String = "",
    val quantity: Int = 0,
    val minQuantity: Int = 0,
    val category: ProductCategory? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

// ================= SALE =================
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey val id: String = "",
    val total: Double = 0.0,
    val status: SaleStatus? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// ================= CUSTOMER =================
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
