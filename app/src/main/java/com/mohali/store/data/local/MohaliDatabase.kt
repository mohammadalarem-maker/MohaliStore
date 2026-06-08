package com.mohali.store.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// ملاحظة: تأكد من مراجعة كلاسات الـ Entities لديك إذا كانت مساراتها مختلفة
@Database(
    entities = [], 
    version = 1, 
    exportSchema = false
)
abstract class MohaliDatabase : RoomDatabase() {
    // إذا واجهت خطأ هنا، تأكد أن كلاسات الـ Dao ممتدة من الـ Interface الصحيح
}
