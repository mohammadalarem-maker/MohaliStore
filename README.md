# 🏪 محلي ستور - Mohali Store
### نظام إدارة محل متكامل | Phones, Accessories, Beauty & Electronics

> تطوير: **كلود** • بواسطة **محمد الصارم**

---

## 📱 وصف التطبيق
تطبيق أندرويد احترافي لإدارة محل تلفونات وإكسسوارات ومستحضرات تجميل وإلكترونيات، يشمل:
- نقطة بيع (POS) متكاملة مع الباركود
- إدارة مخزون شاملة بالتنبيهات
- تقارير مالية وإحصاءات بيانية
- إشعارات فورية لكل عملية بيع
- مزامنة Firebase للبيانات السحابية
- نظام مستخدمين بصلاحيات متعددة

---

## 🔐 بيانات الدخول الافتراضية
| الحقل | القيمة |
|-------|--------|
| اسم المستخدم | `Mohali` |
| كلمة المرور | `1234567` |
| الصلاحية | مدير (Admin) |

---

## ⚙️ متطلبات الإعداد

### 1. إعداد Firebase
1. اذهب إلى [Firebase Console](https://console.firebase.google.com)
2. أنشئ مشروعاً جديداً باسم `MohaliStore`
3. أضف تطبيق Android بـ:
   - **Package Name:** `com.mohali.store`
4. حمّل ملف `google-services.json` وضعه في مجلد `app/`
5. فعّل الخدمات التالية:
   - ✅ **Authentication** → Email/Password
   - ✅ **Firestore Database** → ابدأ في وضع الاختبار
   - ✅ **Cloud Messaging (FCM)**
   - ✅ **Storage** (للصور)

### 2. إعداد Firestore Rules
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 3. إضافة المسؤول في Firebase
- افتح **Authentication** في Firebase Console
- أضف مستخدماً بالبريد: `Mohammedalsarem6@gmail.com`

---

## 🚀 رفع المشروع على GitHub

```bash
# 1. في مجلد المشروع
git init
git add .
git commit -m "Initial commit - Mohali Store v1.0.0"

# 2. أنشئ مستودعاً على GitHub ثم:
git remote add origin https://github.com/YOUR_USERNAME/MohaliStore.git
git branch -M main
git push -u origin main
```

---

## 🏗️ بنية المشروع
```
MohaliStore/
├── app/
│   ├── src/main/
│   │   ├── java/com/mohali/store/
│   │   │   ├── data/
│   │   │   │   ├── models/      # نماذج البيانات
│   │   │   │   ├── local/       # Room Database
│   │   │   │   └── remote/      # Firebase Repository
│   │   │   ├── ui/
│   │   │   │   ├── login/       # شاشة الدخول
│   │   │   │   ├── home/        # الواجهة الرئيسية
│   │   │   │   ├── inventory/   # إدارة المخزون
│   │   │   │   ├── sales/       # المبيعات & POS
│   │   │   │   ├── reports/     # التقارير
│   │   │   │   ├── users/       # إدارة المستخدمين
│   │   │   │   ├── customers/   # العملاء
│   │   │   │   ├── purchases/   # المشتريات
│   │   │   │   ├── expenses/    # المصروفات
│   │   │   │   ├── settings/    # الإعدادات
│   │   │   │   └── notifications/ # الإشعارات
│   │   │   ├── di/              # Hilt DI
│   │   │   ├── notifications/   # FCM Service
│   │   │   └── utils/           # أدوات مساعدة
│   │   └── res/                 # الموارد
│   └── google-services.json     # ← ضع ملفك الحقيقي هنا
└── README.md
```

---

## 🎨 الألوان والتصميم
| العنصر | اللون |
|--------|-------|
| الخلفية الرئيسية | `#1A1A2E` |
| الثانوية | `#16213E` |
| التمييز (Accent) | `#E94560` |
| الفيروزي | `#00B4D8` |
| الذهبي | `#FFD700` |
| النجاح | `#00C896` |

---

## 📦 التقنيات المستخدمة
- **Kotlin** + **Jetpack Compose** (واجهة المستخدم)
- **Firebase** (Auth + Firestore + FCM)
- **Room Database** (تخزين محلي)
- **Hilt** (حقن التبعيات)
- **Coroutines + Flow** (البرمجة التفاعلية)
- **Navigation Compose** (التنقل)
- **Material 3** (تصميم)

---

## 🔔 نظام الإشعارات
- إشعار فوري عند كل عملية بيع يحتوي على: الصنف، السعر، المبلغ، الوقت، التاريخ
- تنبيه عند نفاد المخزون عند الحد المحدد
- يمكن تعديل حد التنبيه من الإعدادات

---

## 👥 صلاحيات المستخدمين
| الصلاحية | المهام |
|----------|--------|
| **مدير (Admin)** | كامل الصلاحيات |
| **مشرف (Manager)** | إدارة المنتجات والتقارير |
| **كاشير (Cashier)** | البيع والفواتير فقط |
| **مشاهد (Viewer)** | عرض البيانات فقط |

---

## 📊 الفئات المدعومة
- 📱 هواتف
- 🎧 اكسسوارات
- 💻 إلكترونيات
- 💄 مستحضرات تجميل
- 💅 أدوات تجميل
- 🔋 شواحن
- 📱 جرابات وأغطية
- 📺 شاشات وزجاج
- ⌚ ساعات ذكية

---

*تطوير: كلود • بواسطة محمد الصارم © 2024*

 
