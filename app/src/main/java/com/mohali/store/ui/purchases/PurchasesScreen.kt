package com.mohali.store.ui.purchases

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
import com.mohali.store.data.models.Purchase
import com.mohali.store.ui.theme.MohaliColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(onBack: () -> Unit, viewModel: PurchasesViewModel = hiltViewModel()) {
    val purchases by viewModel.purchases.collectAsState()
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("المشتريات", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MohaliColors.Accent) {
                Icon(Icons.Filled.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            if (purchases.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxWidth().padding(50.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.LocalShipping, null, tint = MohaliColors.Muted, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("لا توجد مشتريات", color = MohaliColors.Muted)
                        }
                    }
                }
            }
            items(purchases, key = { it.id }) { purchase ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
                    border = BorderStroke(1.dp, MohaliColors.CardBorder)
                ) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(purchase.supplierName.ifEmpty { "مورد غير محدد" }, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${purchase.items.size} صنف", color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
                            Text(dateFormat.format(Date(purchase.createdAt)), color = MohaliColors.Muted, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${"%.2f".format(purchase.totalCost)} ر", color = MohaliColors.Warning, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (purchase.remainingAmount > 0)
                                Text("متبقي: ${"%.2f".format(purchase.remainingAmount)} ر", color = MohaliColors.Error, fontSize = 11.sp)
                            else
                                Text("مدفوع بالكامل", color = MohaliColors.Success, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
