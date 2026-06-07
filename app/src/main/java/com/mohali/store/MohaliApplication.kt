package com.mohali.store

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MohaliApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Sales channel
            NotificationChannel(
                CHANNEL_SALES, "مبيعات جديدة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات المبيعات الجديدة"
                enableVibration(true)
                manager.createNotificationChannel(this)
            }

            // Low stock channel
            NotificationChannel(
                CHANNEL_STOCK, "تنبيهات المخزون",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات نفاد المخزون"
                enableVibration(true)
                manager.createNotificationChannel(this)
            }

            // General channel
            NotificationChannel(
                CHANNEL_GENERAL, "عام",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "الإشعارات العامة"
                manager.createNotificationChannel(this)
            }
        }
    }

    companion object {
        const val CHANNEL_SALES = "mohali_sales"
        const val CHANNEL_STOCK = "mohali_stock"
        const val CHANNEL_GENERAL = "mohali_general"
    }
}
