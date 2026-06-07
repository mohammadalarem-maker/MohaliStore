package com.mohali.store.data.local

import androidx.room.*
import com.mohali.store.data.models.*

@Database(
    entities = [User::class, Product::class, Sale::class, Customer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoleConverter::class)
abstract class MohaliDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun customerDao(): CustomerDao
}
