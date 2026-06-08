package com.mohali.store.di

import android.content.Context
import androidx.room.Room
import com.mohali.store.data.local.MohaliDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MohaliDatabase {
        return Room.databaseBuilder(
            context,
            MohaliDatabase::class.java,
            "mohali_store_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideProductDao(db: MohaliDatabase) = db.productDao()

    @Provides
    @Singleton
    fun provideSaleDao(db: MohaliDatabase) = db.saleDao()

    @Provides
    @Singleton
    fun provideCustomerDao(db: MohaliDatabase) = db.customerDao()

    @Provides
    @Singleton
    fun providePurchaseDao(db: MohaliDatabase) = db.purchaseDao()

    @Provides
    @Singleton
    fun provideExpenseDao(db: MohaliDatabase) = db.expenseDao()
}
