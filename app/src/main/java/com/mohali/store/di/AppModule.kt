package com.mohali.store.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.mohali.store.data.local.MohaliDatabase
import com.mohali.store.data.local.ProductDao
import com.mohali.store.data.local.SaleDao
import com.mohali.store.data.local.CustomerDao
import com.mohali.store.data.local.PurchaseDao
import com.mohali.store.data.local.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // دالة أمان لضمان تهيئة فايربيز إجبارياً قبل طلب أي كلاس تابع له
    private fun ensureFirebaseInitialized(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        ensureFirebaseInitialized(context)
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(@ApplicationContext context: Context): FirebaseFirestore {
        ensureFirebaseInitialized(context)
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseMessaging(@ApplicationContext context: Context): FirebaseMessaging {
        ensureFirebaseInitialized(context)
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MohaliDatabase =
        Room.databaseBuilder(context, MohaliDatabase::class.java, MohaliDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProductDao(db: MohaliDatabase): ProductDao = db.productDao()

    @Provides
    fun provideSaleDao(db: MohaliDatabase): SaleDao = db.saleDao()

    @Provides
    fun provideCustomerDao(db: MohaliDatabase): CustomerDao = db.customerDao()

    @Provides
    fun providePurchaseDao(db: MohaliDatabase): PurchaseDao = db.purchaseDao()

    @Provides
    fun provideExpenseDao(db: MohaliDatabase): ExpenseDao = db.expenseDao()
}
