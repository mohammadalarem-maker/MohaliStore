package com.mohali.store.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.ui.navigation.Screen
import com.mohali.store.ui.theme.MohaliColors
import java.text.SimpleDateFormat
import java.util.*

data class DashboardCard(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

data class MenuSection(
    val title: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val color: Color,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    val unreadNotifications by viewModel.unreadNotifications.collectAsState()
    val dateFormat = remember { SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")) }
    val today = remember { dateFormat.format(Date()) }

    val dashboardCards = listOf(
        DashboardCard("مبيعات اليوم", "${stats.todaySales} ر", "${stats.todayTransactions} فاتورة", Icons.Filled.TrendingUp, MohaliColors.Accent, Screen.Sales.route),
        DashboardCard("مبيعات الشهر", "${stats.monthSales} ر", "هذا الشهر", Icons.Filled.BarChart, MohaliColors.AccentTeal, Screen.Reports.route),
        DashboardCard("إجمالي المنتجات", "${stats.totalProducts}", "منتج نشط", Icons.Filled.Inventory, MohaliColors.AccentGold, Screen.Inventory.route),
        DashboardCard("تنبيهات مخزون", "$lowStockCount", "منتج منخفض", Icons.Filled.Warning, MohaliColors.Warning, Screen.Inventory.route)
    )

    val menuSections = listOf(
        MenuSection("العمليات اليومية", listOf(
            MenuItem("نقطة البيع", Icons.Filled.ShoppingCart, Screen.NewSale.route, MohaliColors.Accent),
            MenuItem("الفواتير", Icons.Filled.Receipt, Screen.Sales.route, MohaliColors.AccentTeal),
            MenuItem("العملاء", Icons.Filled.People, Screen.Customers.route, MohaliColors.AccentGold),
            MenuItem("مشتريات", Icons.Filled.LocalShipping, Screen.Purchases.route, MohaliColors.Success)
        )),
        MenuSection("إدارة المخزون", listOf(
            MenuItem("المنتجات", Icons.Filled.Inventory2, Screen.Inventory.route, MohaliColors.PhonesColor),
            MenuItem("نفاد المخزون", Icons.Filled.WarningAmber, Screen.Inventory.route, MohaliColors.Warning, if (lowStockCount > 0) "$lowStockCount" else null),
            MenuItem("المصروفات", Icons.Filled.AccountBalance, Screen.Expenses.route, MohaliColors.Error),
            MenuItem("التقارير", Icons.Filled.Analytics, Screen.Reports.route, MohaliColors.Info)
        )),
        MenuSection("الإدارة", listOf(
            MenuItem("المستخدمون", Icons.Filled.ManageAccounts, Screen.Users.route, MohaliColors.BeautyToolsColor),
            MenuItem("الإشعارات", Icons.Filled.Notifications, Screen.Notifications.route, MohaliColors.AccentGold, if (unreadNotifications > 0) "$unreadNotifications" else null),
            MenuItem("الإعدادات", Icons.Filled.Settings, Screen.Settings.route, MohaliColors.Muted),
            MenuItem("التقارير المالية", Icons.Filled.PieChart, Screen.Reports.route, MohaliColors.Success)
        ))
    )

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مرحباً، ${currentUser?.username ?: ""}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = today,
                            style = MaterialTheme.typography.labelSmall,
                            color = MohaliColors.OnSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Notifications bell
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) Badge { Text("$unreadNotifications") }
                        }
                    ) {
                        IconButton(onClick = { onNavigate(Screen.Notifications.route) }) {
                            Icon(Icons.Outlined.Notifications, null, tint = Color.White)
                        }
                    }
                    IconButton(onClick = { onNavigate(Screen.Settings.route) }) {
                        Icon(Icons.Outlined.Settings, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MohaliColors.PrimaryVariant
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Quick Sale FAB banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.NewSale.route) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(MohaliColors.Accent, Color(0xFF8B0030))),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ابدأ عملية بيع جديدة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text("نقطة البيع السريعة", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                            Icon(Icons.Filled.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }

            // Stats cards
            item {
                Text("ملخص اليوم", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(dashboardCards) { card ->
                        DashboardStatCard(card = card, onClick = { onNavigate(card.route) })
                    }
                }
            }

            // Menu sections
            items(menuSections) { section ->
                MenuSectionView(section = section, onNavigate = onNavigate)
            }

            // Footer
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider(color = MohaliColors.CardBorder)
                    Spacer(Modifier.height(12.dp))
                    Text("تطوير: كلود • بواسطة محمد الصارم", style = MaterialTheme.typography.labelSmall, color = MohaliColors.Muted)
                    Text("الإصدار 1.0.0", style = MaterialTheme.typography.labelSmall, color = MohaliColors.Muted)
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(card: DashboardCard, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, card.color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(card.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(card.icon, null, tint = card.color, modifier = Modifier.size(22.dp))
            }
            Text(card.value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(card.title, color = MohaliColors.OnSurfaceVariant, fontSize = 11.sp)
            Text(card.subtitle, color = card.color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun MenuSectionView(section: MenuSection, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(section.title, style = MaterialTheme.typography.titleMedium, color = MohaliColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(section.items) { item ->
                MenuItemCard(item = item, onClick = { onNavigate(item.route) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(item: MenuItem, onClick: () -> Unit) {
    BadgedBox(badge = {
        if (item.badge != null) Badge(containerColor = MohaliColors.Error) { Text(item.badge, fontSize = 10.sp) }
    }) {
        Card(
            modifier = Modifier.width(90.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
            border = BorderStroke(1.dp, MohaliColors.CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(46.dp).background(item.color.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, null, tint = item.color, modifier = Modifier.size(24.dp))
                }
                Text(item.label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}
