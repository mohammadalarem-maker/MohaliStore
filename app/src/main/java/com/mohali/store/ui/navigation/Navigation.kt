package com.mohali.store.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import com.mohali.store.ui.home.HomeScreen
import com.mohali.store.ui.inventory.InventoryScreen
import com.mohali.store.ui.inventory.AddEditProductScreen
import com.mohali.store.ui.login.LoginScreen
import com.mohali.store.ui.login.LoginViewModel
import com.mohali.store.ui.sales.SalesScreen
import com.mohali.store.ui.sales.NewSaleScreen
import com.mohali.store.ui.reports.ReportsScreen
import com.mohali.store.ui.users.UsersScreen
import com.mohali.store.ui.customers.CustomersScreen
import com.mohali.store.ui.purchases.PurchasesScreen
import com.mohali.store.ui.expenses.ExpensesScreen
import com.mohali.store.ui.settings.SettingsScreen
import com.mohali.store.ui.notifications.NotificationsScreen

sealed class Screen(val route: String) {
    object Login         : Screen("login")
    object Home          : Screen("home")
    object Inventory     : Screen("inventory")
    object AddProduct    : Screen("add_product?id={id}") {
        fun createRoute(id: String? = null) = if (id != null) "add_product?id=$id" else "add_product"
    }
    object Sales         : Screen("sales")
    object NewSale       : Screen("new_sale")
    object Reports       : Screen("reports")
    object Users         : Screen("users")
    object Customers     : Screen("customers")
    object Purchases     : Screen("purchases")
    object Expenses      : Screen("expenses")
    object Settings      : Screen("settings")
    object Notifications : Screen("notifications")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    
    // حالة لتخزين الشاشة الأولى بعد التأكد من حالة تسجيل الدخول
    var startDestination by remember { mutableStateOf<String?>(null) }

    // استخدام LaunchedEffect لتأجيل تحديد الوجهة ومنع الكراش عند بدء التشغيل
    LaunchedEffect(Unit) {
        startDestination = if (loginViewModel.isLoggedIn()) Screen.Home.route else Screen.Login.route
    }

    // التحقق من أن الوجهة قد تم تحديدها فعلياً قبل بناء الـ NavHost
    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { isAdmin ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    viewModel = loginViewModel
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Inventory.route) {
                InventoryScreen(
                    onAddProduct = { navController.navigate(Screen.AddProduct.createRoute()) },
                    onEditProduct = { id -> navController.navigate(Screen.AddProduct.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.AddProduct.route,
                arguments = listOf(navArgument("id") { nullable = true; defaultValue = null })
            ) { backStack ->
                val productId = backStack.arguments?.getString("id")
                AddEditProductScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Screen.Sales.route) {
                SalesScreen(
                    onNewSale = { navController.navigate(Screen.NewSale.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NewSale.route) {
                NewSaleScreen(
                    onBack = { navController.popBackStack() },
                    onSaleComplete = { navController.popBackStack() }
                )
            }

            composable(Screen.Reports.route) {
                ReportsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Users.route) {
                UsersScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Customers.route) {
                CustomersScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Purchases.route) {
                PurchasesScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Expenses.route) {
                ExpensesScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }
        }
    } else {
        // شاشة تحميل فارغة تظهر لجزء من الثانية أثناء التحقق من حالة المستخدم لحماية التطبيق من الكراش
        Box(modifier = Modifier.fillMaxSize())
    }
}
