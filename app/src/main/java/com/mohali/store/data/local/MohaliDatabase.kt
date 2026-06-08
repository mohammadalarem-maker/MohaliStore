package com.mohali.store.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mohali.store.data.models.*

@Database(
    entities = [Product::class, Sale::class, Customer::class, Purchase::class, Expense::class], 
    version = 1, 
    exportSchema = false
)
abstract class MohaliDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun customerDao(): CustomerDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun expenseDao(): ExpenseDao
}
