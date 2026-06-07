package com.mohali.store.ui.sales

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.*
import com.mohali.store.ui.theme.MohaliColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleScreen(
    onBack: () -> Unit,
    onSaleComplete: () -> Unit,
    viewModel: NewSaleViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val total by viewModel.total.collectAsState()
    val discount by viewModel.discount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showCheckout by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var paidAmount by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.CASH) }
    var discountText by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        SaleSuccessDialog(
            total = total,
            change = (paidAmount.toDoubleOrNull() ?: 0.0) - total,
            onDismiss = { onSaleComplete() }
        )
    }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("نقطة البيع", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(Icons.Filled.DeleteSweep, null, tint = MohaliColors.Error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Search products
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearch,
                placeholder = { Text("ابحث عن منتج أو امسح الباركود...") },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = MohaliColors.OnSurfaceVariant) },
                trailingIcon = {
                    Icon(Icons.Outlined.QrCodeScanner, null, tint = MohaliColors.AccentTeal,
                        modifier = Modifier.clickable { /* barcode scanner */ })
                },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MohaliColors.Accent,
                    unfocusedBorderColor = MohaliColors.CardBorder,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = MohaliColors.Surface, unfocusedContainerColor = MohaliColors.Surface
                ),
                singleLine = true
            )

            // Search results
            AnimatedVisibility(visible = products.isNotEmpty() && searchQuery.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).heightIn(max = 200.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
                    border = BorderStroke(1.dp, MohaliColors.CardBorder)
                ) {
                    LazyColumn {
                        items(products) { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.addToCart(product); viewModel.onSearch("") }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text("${product.sellPrice} ر • متبقي: ${product.quantity}", color = MohaliColors.OnSurfaceVariant, fontSize = 11.sp)
                                }
                                Icon(Icons.Filled.AddCircle, null, tint = MohaliColors.Success, modifier = Modifier.size(28.dp))
                            }
                            Divider(color = MohaliColors.CardBorder, thickness = 0.5.dp)
                        }
                    }
                }
            }

            // Cart
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                if (cartItems.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.ShoppingCart, null, tint = MohaliColors.Muted, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("السلة فارغة", color = MohaliColors.Muted, style = MaterialTheme.typography.bodyLarge)
                            Text("ابحث عن منتج لإضافته", color = MohaliColors.Muted, fontSize = 12.sp)
                        }
                    }
                } else {
                    Text("المنتجات المضافة (${cartItems.size})", color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(vertical = 6.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(cartItems, key = { it.productId }) { item ->
                            CartItemRow(
                                item = item,
                                onIncrease = { viewModel.increaseQty(item.productId) },
                                onDecrease = { viewModel.decreaseQty(item.productId) },
                                onRemove = { viewModel.removeFromCart(item.productId) }
                            )
                        }
                    }
                }
            }

            // Totals & Checkout
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Discount row
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = discountText,
                                onValueChange = { discountText = it; viewModel.setDiscount(it.toDoubleOrNull() ?: 0.0) },
                                label = { Text("خصم (ريال)", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MohaliColors.AccentGold,
                                    unfocusedBorderColor = MohaliColors.CardBorder,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedContainerColor = MohaliColors.Surface, unfocusedContainerColor = MohaliColors.Surface
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                TotalRow("المجموع", "${"%.2f".format(subtotal)} ر", MohaliColors.OnSurfaceVariant)
                                if (discount > 0) TotalRow("الخصم", "-${"%.2f".format(discount)} ر", MohaliColors.AccentGold)
                                TotalRow("الإجمالي", "${"%.2f".format(total)} ر", MohaliColors.Accent, bold = true, size = 18.sp)
                            }
                        }

                        // Payment methods
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PaymentMethod.values().forEach { method ->
                                FilterChip(
                                    selected = selectedPayment == method,
                                    onClick = { selectedPayment = method },
                                    label = { Text(method.nameAr, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MohaliColors.Accent,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Checkout button
                        Button(
                            onClick = { showCheckout = true },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Success)
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("إتمام البيع • ${"%.2f".format(total)} ر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }

    // Checkout dialog
    if (showCheckout) {
        AlertDialog(
            onDismissRequest = { showCheckout = false },
            containerColor = MohaliColors.Card,
            title = { Text("إتمام عملية البيع", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = customerName, onValueChange = { customerName = it },
                        label = { Text("اسم العميل (اختياري)") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MohaliColors.Accent, unfocusedBorderColor = MohaliColors.CardBorder,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedContainerColor = MohaliColors.Surface, unfocusedContainerColor = MohaliColors.Surface
                        )
                    )
                    OutlinedTextField(
                        value = paidAmount, onValueChange = { paidAmount = it },
                        label = { Text("المبلغ المدفوع") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MohaliColors.Accent, unfocusedBorderColor = MohaliColors.CardBorder,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedContainerColor = MohaliColors.Surface, unfocusedContainerColor = MohaliColors.Surface
                        )
                    )
                    val change = (paidAmount.toDoubleOrNull() ?: 0.0) - total
                    if (change >= 0) {
                        Text("الباقي: ${"%.2f".format(change)} ر", color = MohaliColors.Success, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    } else if (paidAmount.isNotEmpty()) {
                        Text("المبلغ غير كافٍ!", color = MohaliColors.Error, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val paid = paidAmount.toDoubleOrNull() ?: total
                        viewModel.completeSale(
                            customerName = customerName,
                            customerPhone = customerPhone,
                            paidAmount = paid,
                            paymentMethod = selectedPayment
                        ) {
                            showCheckout = false
                            showSuccess = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Success),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("تأكيد البيع", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckout = false }) { Text("إلغاء", color = MohaliColors.OnSurfaceVariant) }
            }
        )
    }
}

@Composable
fun CartItemRow(item: SaleItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Surface),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("${item.unitPrice} ر × ${item.quantity}", color = MohaliColors.OnSurfaceVariant, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("${"%.2f".format(item.total)} ر", color = MohaliColors.Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Filled.RemoveCircle, null, tint = MohaliColors.Warning, modifier = Modifier.size(20.dp))
                }
                Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IconButton(onClick = onIncrease, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Filled.AddCircle, null, tint = MohaliColors.Success, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Filled.Close, null, tint = MohaliColors.Error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun TotalRow(label: String, value: String, color: Color, bold: Boolean = false, size: TextUnit = 13.sp) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = MohaliColors.OnSurfaceVariant, fontSize = size)
        Text(value, color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, fontSize = size)
    }
}

@Composable
fun SaleSuccessDialog(total: Double, change: Double, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MohaliColors.Card,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.CheckCircle, null, tint = MohaliColors.Success, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(8.dp))
                Text("تمت عملية البيع!", color = MohaliColors.Success, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("الإجمالي: ${"%.2f".format(total)} ر", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                if (change > 0) Text("الباقي: ${"%.2f".format(change)} ر", color = MohaliColors.AccentGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Accent),
                modifier = Modifier.fillMaxWidth()
            ) { Text("حسناً", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    )
}
