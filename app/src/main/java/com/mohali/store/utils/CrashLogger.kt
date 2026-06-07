package com.mohali.store.utils

import android.content.Context
import android.util.Log

object CrashLogger {
    fun log(context: Context, exception: Throwable) {
        Log.e("MOHALI_CRASH", "لقد حدث كراش: ", exception)
        try {
            // حفظ نص الخطأ بالكامل داخل ذاكرة التطبيق فوراً
            val sharedPref = context.getSharedPreferences("mohali_debug", Context.MODE_PRIVATE)
            sharedPref.edit().putString("last_crash", Log.getStackTraceString(exception)).commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
