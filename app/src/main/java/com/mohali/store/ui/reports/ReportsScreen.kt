package com.mohali.store.ui.reports

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.ui.theme.MohaliColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val weeklySales by viewModel.weeklySales.collectAsState()
    val categoryStats by viewModel.categoryStats.collectAsState()
    var selectedPeriod by remember { mutableStateOf(0) }
    val periods = listOf("اليوم", "الأسبوع", "الشهر", "السنة")

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("التقارير المالية", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportReport() }) {
                        Icon(Icons.Filled.FileDownload, null, tint = MohaliColors.AccentGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // Period selector
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    periods.forEachIndexed { i, period ->
                        FilterChip(
                            selected = selectedPeriod == i,
                            onClick = { selectedPeriod = i; viewModel.loadPeriod(i) },
                            label = { Text(period, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MohaliColors.Accent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // KPI Cards
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { KpiCard("المبيعات", "${"%.2f".format(stats.sales)} ر", MohaliColors.Accent, Icons.Filled.TrendingUp) }
                    item { KpiCard("المشتريات", "${"%.2f".format(stats.purchases)} ر", MohaliColors.Warning, Icons.Filled.LocalShipping) }
                    item { KpiCard("المصروفات", "${"%.2f".format(stats.expenses)} ر", MohaliColors.Error, Icons.Filled.AccountBalance) }
                    item { KpiCard("صافي الربح", "${"%.2f".format(stats.netProfit)} ر", if (stats.netProfit >= 0) MohaliColors.Success else MohaliColors.Error, Icons.Filled.AttachMoney) }
                }
            }

            // Weekly chart
            item {
                ReportCard("مبيعات الأسبوع") {
                    if (weeklySales.isNotEmpty()) {
                        SimpleBarChart(data = weeklySales)
                    } else {
                        Text("لا توجد بيانات كافية", color = MohaliColors.Muted, modifier = Modifier.padding(16.dp))
                    }
                }
            }

            // Category breakdown
            item {
                ReportCard("مبيعات حسب الفئة") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(4.dp)) {
                        categoryStats.forEach { (category, amount, percentage) ->
                            CategoryBar(label = category, amount = amount, percentage = percentage)
                        }
                        if (categoryStats.isEmpty())
                            Text("لا توجد بيانات", color = MohaliColors.Muted)
                    }
                }
            }

            // Financial Summary
            item {
                ReportCard("الملخص المالي") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(4.dp)) {
                        FinancialRow("إجمالي المبيعات", "${"%.2f".format(stats.sales)} ر", MohaliColors.Success)
                        FinancialRow("تكلفة البضاعة", "${"%.2f".format(stats.cogs)} ر", MohaliColors.Warning)
                        FinancialRow("إجمالي الربح", "${"%.2f".format(stats.grossProfit)} ر", MohaliColors.AccentTeal)
                        Divider(color = MohaliColors.CardBorder)
                        FinancialRow("المصروفات", "${"%.2f".format(stats.expenses)} ر", MohaliColors.Error)
                        FinancialRow("صافي الربح", "${"%.2f".format(stats.netProfit)} ر",
                            if (stats.netProfit >= 0) MohaliColors.Success else MohaliColors.Error, bold = true)
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(label: String, value: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(label, color = MohaliColors.OnSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
fun ReportCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = MohaliColors.AccentTeal, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SimpleBarChart(data: List<Pair<String, Double>>) {
    val maxVal = data.maxOfOrNull { it.second } ?: 1.0
    Row(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { i, (label, value) ->
            val fraction = if (maxVal > 0) (value / maxVal).toFloat() else 0f
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                if (value > 0) {
                    Text("${"%.0f".format(value)}", color = MohaliColors.Accent, fontSize = 9.sp)
                    Spacer(Modifier.height(2.dp))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(fraction.coerceAtLeast(0.03f))
                        .background(
                            Brush.verticalGradient(listOf(MohaliColors.AccentTeal, MohaliColors.Accent)),
                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(label, color = MohaliColors.Muted, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun CategoryBar(label: String, amount: Double, percentage: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, fontSize = 13.sp)
            Text("${"%.2f".format(amount)} ر (${"%.1f".format(percentage)}%)", color = MohaliColors.Accent, fontSize = 12.sp)
        }
        LinearProgressIndicator(
            progress = (percentage / 100f).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = MohaliColors.Accent,
            trackColor = MohaliColors.CardBorder
        )
    }
}

@Composable
fun FinancialRow(label: String, value: String, color: Color, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = MohaliColors.OnSurfaceVariant, fontSize = 13.sp)
        Text(value, color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium, fontSize = if (bold) 15.sp else 13.sp)
    }
}
