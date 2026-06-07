package com.mohali.store.ui.inventory

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.Product
import com.mohali.store.data.models.ProductCategory
import com.mohali.store.ui.theme.MohaliColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onAddProduct: () -> Unit,
    onEditProduct: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("إدارة المخزون", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onAddProduct) {
                        Icon(Icons.Filled.Add, null, tint = MohaliColors.Accent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddProduct,
                containerColor = MohaliColors.Accent,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("إضافة منتج") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("بحث بالاسم أو الباركود...") },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = MohaliColors.OnSurfaceVariant) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty())
                        IconButton(onClick = { viewModel.onSearchChange("") }) {
                            Icon(Icons.Filled.Clear, null, tint = MohaliColors.OnSurfaceVariant)
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MohaliColors.Accent,
                    unfocusedBorderColor = MohaliColors.CardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = MohaliColors.Surface,
                    unfocusedContainerColor = MohaliColors.Surface
                ),
                singleLine = true
            )

            // Category filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.onCategoryFilter(null) },
                        label = { Text("الكل") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MohaliColors.Accent,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(ProductCategory.values().toList()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { viewModel.onCategoryFilter(cat) },
                        label = { Text(cat.nameAr) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MohaliColors.Accent,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Stats row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStatCard("إجمالي", "${products.size}", MohaliColors.Info, Modifier.weight(1f))
                MiniStatCard("منخفض", "${products.count { it.quantity <= it.minQuantity && it.quantity > 0 }}", MohaliColors.Warning, Modifier.weight(1f))
                MiniStatCard("نافد", "${products.count { it.quantity == 0 }}", MohaliColors.Error, Modifier.weight(1f))
            }

            // Products list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onEdit = { onEditProduct(product.id) },
                        onDelete = { showDeleteDialog = product }
                    )
                }
                if (products.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Inventory2, null, tint = MohaliColors.Muted, modifier = Modifier.size(64.dp))
                                Spacer(Modifier.height(12.dp))
                                Text("لا توجد منتجات", color = MohaliColors.Muted, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = MohaliColors.Card,
            title = { Text("حذف المنتج", color = Color.White) },
            text = { Text("هل تريد حذف ${product.name}؟", color = MohaliColors.OnSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(product.id)
                    showDeleteDialog = null
                }) {
                    Text("حذف", color = MohaliColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("إلغاء", color = MohaliColors.OnSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun ProductCard(product: Product, onEdit: () -> Unit, onDelete: () -> Unit) {
    val stockColor = when {
        product.quantity == 0 -> MohaliColors.Error
        product.quantity <= product.minQuantity -> MohaliColors.Warning
        else -> MohaliColors.Success
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, stockColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Box(
                modifier = Modifier.size(50.dp).background(
                    MohaliColors.getCategoryColor(product.category).copy(alpha = 0.15f),
                    RoundedCornerShape(14.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory,
                    contentDescription = null,
                    tint = MohaliColors.getCategoryColor(product.category),
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (product.barcode.isNotEmpty())
                    Text(product.barcode, color = MohaliColors.Muted, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ش: ${product.buyPrice} ر", color = MohaliColors.OnSurfaceVariant, fontSize = 11.sp)
                    Text("ب: ${product.sellPrice} ر", color = MohaliColors.Success, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${product.quantity}",
                    color = stockColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text("قطعة", color = MohaliColors.Muted, fontSize = 10.sp)
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Edit, null, tint = MohaliColors.Info, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, null, tint = MohaliColors.Error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MiniStatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(label, color = MohaliColors.OnSurfaceVariant, fontSize = 11.sp)
        }
    }
}

fun MohaliColors.getCategoryColor(category: ProductCategory): Color = when (category) {
    ProductCategory.PHONES -> PhonesColor
    ProductCategory.ACCESSORIES -> AccessoriesColor
    ProductCategory.ELECTRONICS -> ElectronicsColor
    ProductCategory.COSMETICS -> CosmeticsColor
    ProductCategory.BEAUTY_TOOLS -> BeautyToolsColor
    ProductCategory.CHARGERS -> ChargersColor
    else -> AccentTeal
}
