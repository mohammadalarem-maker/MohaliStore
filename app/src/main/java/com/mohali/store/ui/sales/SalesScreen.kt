package com.mohali.store.ui.sales

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.*
import com.mohali.store.ui.theme.MohaliColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    onNewSale: () -> Unit,
    onBack: () -> Unit,
    viewModel: SalesViewModel = hiltViewModel()
) {
    val sales by viewModel.sales.collectAsState()
    val totalToday by viewModel.totalToday.collectAsState()
    val totalMonth by viewModel.totalMonth.collectAsState()
    var selectedSale by remember { mutableStateOf<Sale?>(null) }
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("سجل المبيعات", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewSale,
                containerColor = MohaliColors.Accent,
                contentColor = Color.White
            ) { Icon(Icons.Filled.Add, null) }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Summary
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard("اليوم", "${"%.2f".format(totalToday)} ر", MohaliColors.Accent, Modifier.weight(1f))
                    SummaryCard("الشهر", "${"%.2f".format(totalMonth)} ر", MohaliColors.AccentTeal, Modifier.weight(1f))
                }
            }

            item {
                Text("الفواتير الأخيرة", color = MohaliColors.OnSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            if (sales.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Receipt, null, tint = MohaliColors.Muted, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("لا توجد فواتير بعد", color = MohaliColors.Muted)
                        }
                    }
                }
            }

            items(sales, key = { it.id }) { sale ->
                SaleCard(
                    sale = sale,
                    dateFormat = dateFormat,
                    onClick = { selectedSale = sale }
                )
            }
        }
    }

    // Sale Detail Dialog
    selectedSale?.let { sale ->
        SaleDetailDialog(
            sale = sale,
            dateFormat = dateFormat,
            onDismiss = { selectedSale = null }
        )
    }
}

@Composable
fun SummaryCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(label, color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
fun SaleCard(sale: Sale, dateFormat: SimpleDateFormat, onClick: () -> Unit) {
    val statusColor = when (sale.status) {
        SaleStatus.COMPLETED -> MohaliColors.Success
        SaleStatus.CANCELLED -> MohaliColors.Error
        SaleStatus.RETURNED  -> MohaliColors.Warning
        SaleStatus.PENDING   -> MohaliColors.Info
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sale.invoiceNumber, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (sale.customerName.isNotEmpty())
                    Text(sale.customerName, color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
                Text(dateFormat.format(Date(sale.createdAt)), color = MohaliColors.Muted, fontSize = 11.sp)
                Text("${sale.items.size} منتج • ${sale.paymentMethod.nameAr}", color = MohaliColors.OnSurfaceVariant, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${"%.2f".format(sale.total)} ر", color = MohaliColors.Accent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(sale.cashierName, color = MohaliColors.Muted, fontSize = 10.sp)
                Box(
                    modifier = Modifier.background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)
                ) { Text(sale.status.name, color = statusColor, fontSize = 10.sp) }
            }
        }
    }
}

@Composable
fun SaleDetailDialog(sale: Sale, dateFormat: SimpleDateFormat, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MohaliColors.Card,
        title = {
            Text(sale.invoiceNumber, color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(dateFormat.format(Date(sale.createdAt)), color = MohaliColors.Muted, fontSize = 12.sp)
                if (sale.customerName.isNotEmpty()) Text("العميل: ${sale.customerName}", color = MohaliColors.OnSurfaceVariant, fontSize = 13.sp)
                Divider(color = MohaliColors.CardBorder)
                sale.items.forEach { item ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.productName} × ${item.quantity}", color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text("${"%.2f".format(item.total)} ر", color = MohaliColors.Accent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Divider(color = MohaliColors.CardBorder)
                if (sale.discount > 0)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("خصم", color = MohaliColors.AccentGold)
                        Text("-${"%.2f".format(sale.discount)} ر", color = MohaliColors.AccentGold)
                    }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("الإجمالي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("${"%.2f".format(sale.total)} ر", color = MohaliColors.Accent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Text("طريقة الدفع: ${sale.paymentMethod.nameAr}", color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
                Text("الكاشير: ${sale.cashierName}", color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق", color = MohaliColors.Accent) }
        }
    )
}
