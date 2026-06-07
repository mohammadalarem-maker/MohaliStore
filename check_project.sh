#!/bin/bash
echo -e "\e[1;34m==================================================\e[0m"
echo -e "\e[1;34m    بدء فحص وتشخيص أكواد مشروع MohaliStore        \e[0m"
echo -e "\e[1;34m==================================================\e[0m"

echo -e "\n\e[1;33m[1] الفحص عن كلاس التطبيق الرئيسي (Application Class):\e[0m"
APP_CLASS=$(grep -rn "@HiltAndroidApp" ./app/src/main/)
if [ -z "$APP_CLASS" ]; then
    echo -e "\e[1;31m[-] خطأ: لم يتم العثور على @HiltAndroidApp في أي كلاس! هذا يسبب كراش فوري.\e[0m"
else
    echo -e "\e[1;32m[+] ممتاز: تم العثور على تهيئة Hilt للتطبيق في:\e[0m"
    echo "$APP_CLASS"
fi

echo -e "\n\e[1;33m[2] فحص ملف AndroidManifest.xml وربطه بكلاس التطبيق:\e[0m"
if [ -f "./app/src/main/AndroidManifest.xml" ]; then
    HAS_APP_NAME=$(grep -n "android:name=" ./app/src/main/AndroidManifest.xml)
    if [ -z "$HAS_APP_NAME" ]; then
        echo -e "\e[1;31m[-] خطأ: كلاس التطبيق ليس مسجلاً في Manifest (android:name مفقود)! كراش حتمي.\e[0m"
    else
        echo -e "\e[1;32m[+] ممتاز: تم العثور على تسجيل التطبيق في المانيفست:\e[0m"
        echo "$HAS_APP_NAME"
    fi
else
    echo -e "\e[1;31m[-] خطأ: ملف AndroidManifest.xml غير موجود في المسار الصحيح!\e[0m"
fi

echo -e "\n\e[1;33m[3] فحص ملف MainActivity.kt وتهيئة نقطة الدخول:\e[0m"
MAIN_ACTIVITY=$(find ./app/src/main/ -name "MainActivity.kt")
if [ -z "$MAIN_ACTIVITY" ]; then
    echo -e "\e[1;31m[-] خطأ: لم يتم العثور على ملف MainActivity.kt!\e[0m"
else
    echo "[+] تم العثور على MainActivity.kt. جاري فحص @AndroidEntryPoint..."
    HAS_ENTRY=$(grep -n "@AndroidEntryPoint" "$MAIN_ACTIVITY")
    if [ -z "$HAS_ENTRY" ]; then
        echo -e "\e[1;31m[-] خطأ: الـ MainActivity تفتقد وسم @AndroidEntryPoint! أي ViewModel سيتم استدعاؤه سيسبب كراش فوراً.\e[0m"
    else
        echo -e "\e[1;32m[+] ممتاز: @AndroidEntryPoint موجود في الـ MainActivity.\e[0m"
    fi
fi

echo -e "\n\e[1;33m[4] فحص ملف LoginViewModel.kt وبناء الـ Constructor:\e[0m"
VM_FILE=$(find ./app/src/main/ -name "LoginViewModel.kt")
if [ -z "$VM_FILE" ]; then
    echo -e "\e[1;31m[-] خطأ: لم يتم العثور على ملف LoginViewModel.kt!\e[0m"
else
    echo "[+] تم العثور على LoginViewModel.kt. جاري فحص الحقن الوراثي..."
    HAS_HILT_VM=$(grep -n "@HiltViewModel" "$VM_FILE")
    HAS_INJECT=$(grep -n "@Inject constructor" "$VM_FILE")
    
    if [ -z "$HAS_HILT_VM" ]; then
        echo -e "\e[1;31m[-] تحذير: وسم @HiltViewModel مفقود في LoginViewModel!\e[0m"
    else
        echo -e "\e[1;32m[+] ممتاز: وسم @HiltViewModel موجود.\e[0m"
    fi
    
    if [ -z "$HAS_INJECT" ]; then
        echo -e "\e[1;31m[-] خطأ: @Inject constructor مفقود في كلاس الـ ViewModel! لن يتمكن Hilt من إنشائه وسينهار التطبيق.\e[0m"
    else
        echo -e "\e[1;32m[+] ممتاز: @Inject constructor موجود.\e[0m"
    fi
fi

echo -e "\n\e[1;33m[5] فحص بناء قاعدة بيانات Room والمحاكاة التدميرية:\e[0m"
DB_BUILD=$(grep -rn "databaseBuilder" ./app/src/main/)
if [ -z "$DB_BUILD" ]; then
    echo -e "\e[1;31m[-] لم يتم العثور على أمر databaseBuilder (ربما يتم حقنه عبر Module مستقل).\e[0m"
else
    echo "[+] تم العثور على بناء قاعدة البيانات في السطور التالية:"
    echo "$DB_BUILD"
    HAS_FALLBACK=$(grep -rn "fallbackToDestructiveMigration" ./app/src/main/)
    if [ -z "$HAS_FALLBACK" ]; then
        echo -e "\e[1;31m[-] تحذير: fallbackToDestructiveMigration() مفقود! إذا قمت بتغيير أي جدول سابقاً، سينهار التطبيق فوراً بسبب اختلاف النسخ (Database Version Mismatch).\e[0m"
    else
        echo -e "\e[1;32m[+] ممتاز: حماية الهجرة التدميرية لقاعدة البيانات مفعّلة.\e[0m"
    fi
fi

echo -e "\n\e[1;34m==================================================\e[0m"
echo -e "\e[1;34m            انتهى الفحص والتشخيص                 \e[0m"
echo -e "\e[1;34m==================================================\e[0m"
