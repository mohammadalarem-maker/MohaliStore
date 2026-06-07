package com.mohali.store.data.remote                                        

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mohali.store.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    // Collections
    private val usersCollection = firestore.collection("users")
    private val productsCollection = firestore.collection("products")
    private val salesCollection = firestore.collection("sales")
    private val customersCollection = firestore.collection("customers")
    private val purchasesCollection = firestore.collection("purchases")
    private val expensesCollection = firestore.collection("expenses")
    private val notificationsCollection = firestore.collection("notifications")
    private val settingsCollection = firestore.collection("settings")       

    // ============================
    // AUTH
    // ============================
    suspend fun loginWithEmailPassword(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {                                                                    
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Login failed")               
            val userDoc = usersCollection.document(uid).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found")
            Result.success(user)                                                    
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginAdmin(username: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        if (username == "Mohali" && password == "1234567") {
            val adminUser = User(
                uid = "admin_local",
                username = "Mohali",
                email = "Mohammedalsarem6@gmail.com",
                role = UserRole.ADMIN,
                isActive = true
            )
            return@withContext Result.success(adminUser)
        }
        return@withContext Result.failure(Exception("Invalid credentials"))
    }

    suspend fun registerUser(user: User, password: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = result.user?.uid ?: throw Exception("Registration failed")
            val newUser = user.copy(uid = uid)                                          
            usersCollection.document(uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            auth.currentUser?.updatePassword(newPassword)?.await() ?: throw Exception("Not logged in")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()

    // ============================
    // USERS
    // ============================
    fun getUsers(): Flow<List<User>> = callbackFlow {
        val listener = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }             
val users = snapshot?.documents?.mapNotNull { doc ->
    try {
        User(
            uid = doc.getString("uid") ?: "",
            username = doc.getString("username") ?: "",
            email = doc.getString("email") ?: "",
            role = doc.getString("role") ?: "CASHIER",
            isActive = doc.getBoolean("isActive") ?: true,
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
            lastLogin = doc.getLong("lastLogin"),
            profileImage = doc.getString("profileImage") ?: ""
        )
    } catch (e: Exception) { null }
} ?: emptyList()
            trySend(users)
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deactivateUser(uid: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            usersCollection.document(uid).update("isActive", false).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================
    // PRODUCTS
    // ============================
    fun getProducts(): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
                trySend(products)                                                       
            }
        awaitClose { listener.remove() }
    }

    suspend fun addProduct(product: Product): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val docRef = productsCollection.document()
            val newProduct = product.copy(id = docRef.id)
            docRef.set(newProduct).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            productsCollection.document(product.id).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            productsCollection.document(productId).update("isActive", false).await()
            Result.success(Unit)                                                    
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncProducts(products: List<Product>) = withContext(Dispatchers.IO) {
        try {
            val batch = firestore.batch()
            products.forEach { product ->
                batch.set(productsCollection.document(product.id), product)
            }
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ============================
    // SALES
    // ============================
    suspend fun addSale(sale: Sale): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val docRef = salesCollection.document()
            val newSale = sale.copy(id = docRef.id, isSynced = true)
            docRef.set(newSale).await()

            // Send notification to admin silently
            sendSaleNotification(newSale)

            // Update product quantities safely
            sale.items.forEach { item ->
                try {
                    productsCollection.document(item.productId)
                        .update("quantity", com.google.firebase.firestore.FieldValue.increment(-item.quantity.toLong()))
                        .await()                                                            
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<Sale>> = callbackFlow {
        val listener = salesCollection                                                  
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .whereLessThanOrEqualTo("createdAt", endDate)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val sales = snapshot?.toObjects(Sale::class.java) ?: emptyList()
                trySend(sales)                                                          
            }
        awaitClose { listener.remove() }
    }

    // ============================
    // NOTIFICATIONS
    // ============================
    private suspend fun sendSaleNotification(sale: Sale) = withContext(Dispatchers.IO) {
        try {
            val notification = AppNotification(
                id = notificationsCollection.document().id,
                title = "🛒 عملية بيع جديدة",
                body = "تم بيع ${sale.items.size} منتج بمبلغ ${sale.total} ريال",
                type = NotificationType.SALE,                                               
                data = mapOf(
                    "saleId" to sale.id,
                    "total" to sale.total.toString(),
                    "cashier" to sale.cashierName,
                    "itemsCount" to sale.items.size.toString()
                )
            )
            notificationsCollection.document(notification.id).set(notification).await()
        } catch (e: Exception) {                                                        
            e.printStackTrace()
        }
    }

    suspend fun sendLowStockNotification(product: Product) = withContext(Dispatchers.IO) {
        try {
            val notification = AppNotification(
                id = notificationsCollection.document().id,
                title = "⚠️ تحذير: مخزون منخفض",
                body = "المنتج ${product.name} وصل إلى ${product.quantity} قطعة فقط",
                type = NotificationType.LOW_STOCK,                                          
                data = mapOf(
                    "productId" to product.id,
                    "productName" to product.name,
                    "quantity" to product.quantity.toString(),
                    "minQuantity" to product.minQuantity.toString()
                )
            )
            notificationsCollection.document(notification.id).set(notification).await()
        } catch (e: Exception) {                                                        
            e.printStackTrace()
        }
    }

    fun getNotifications(): Flow<List<AppNotification>> = callbackFlow {
        val listener = notificationsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val notifications = snapshot?.toObjects(AppNotification::class.java) ?: emptyList()
                trySend(notifications)                                                  
            }
        awaitClose { listener.remove() }
    }

    // ============================
    // CUSTOMERS
    // ============================
    suspend fun addCustomer(customer: Customer): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val docRef = customersCollection.document()
            val newCustomer = customer.copy(id = docRef.id)
            docRef.set(newCustomer).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCustomers(): Flow<List<Customer>> = callbackFlow {
        val listener = customersCollection
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val customers = snapshot?.toObjects(Customer::class.java) ?: emptyList()
                trySend(customers)                                                      
            }
        awaitClose { listener.remove() }                                        
    }

    // ============================
    // EXPENSES                                                                 
    // ============================
    suspend fun addExpense(expense: Expense): Result<String> = withContext(Dispatchers.IO) {                      
        return@withContext try {
            val docRef = expensesCollection.document()                                  
            val newExpense = expense.copy(id = docRef.id)                               
            docRef.set(newExpense).await()                                              
            Result.success(docRef.id)                                               
        } catch (e: Exception) {                                                        
            Result.failure(e)
        }                                                                       
    }
                                                                                
    fun getExpenses(): Flow<List<Expense>> = callbackFlow {
        val listener = expensesCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)                           
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val expenses = snapshot?.toObjects(Expense::class.java) ?: emptyList()                                                                                  
                trySend(expenses)
            }                                                                       
        awaitClose { listener.remove() }
    }

    // ============================
    // SETTINGS
    // ============================
    suspend fun getSettings(): Map<String, Any> = withContext(Dispatchers.IO) {                                   
        return@withContext try {                                                                    
            settingsCollection.document("app_settings").get().await().data ?: emptyMap()                                                                        
        } catch (e: Exception) {
            emptyMap()
        }
    }                                                                       
    
    suspend fun updateSettings(settings: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {          
        return@withContext try {
            settingsCollection.document("app_settings").set(settings).await()
            Result.success(Unit)                                                    
        } catch (e: Exception) {
            Result.failure(e)                                                       
        }                                                                       
    }                                                                       
}
