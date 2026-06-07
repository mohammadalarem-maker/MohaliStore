package com.mohali.store.ui.inventory

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.ProductCategory
import com.mohali.store.ui.theme.MohaliColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val isEdit = productId != null
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(productId) {
        if (productId != null) viewModel.loadProduct(productId)
    }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEdit) "تعديل المنتج" else "إضافة منتج جديد",
                        color = Color.White, fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error banner
            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MohaliColors.Error.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, MohaliColors.Error)
                ) {
                    Text(
                        error!!, color = MohaliColors.Error,
                        modifier = Modifier.padding(12.dp), fontSize = 13.sp
                    )
                }
            }

            SectionCard("معلومات المنتج الأساسية") {
                MohaliTextField("اسم المنتج *", state.name, { viewModel.onNameChange(it) }, Icons.Filled.Label)
                MohaliTextField("الباركود", state.barcode, { viewModel.onBarcodeChange(it) }, Icons.Filled.QrCode)
                MohaliTextField("الماركة / البراند", state.brand, { viewModel.onBrandChange(it) }, Icons.Filled.Business)
                MohaliTextField("الموديل", state.model, { viewModel.onModelChange(it) }, Icons.Filled.PhoneAndroid)
                MohaliTextField("اللون", state.color, { viewModel.onColorChange(it) }, Icons.Filled.Palette)
                MohaliTextField("السعة / التخزين", state.storage, { viewModel.onStorageChange(it) }, Icons.Filled.Storage)
            }

            SectionCard("الفئة") {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = state.category.nameAr,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("فئة المنتج") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        colors = mohaliTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MohaliColors.Card)
                    ) {
                        ProductCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.nameAr, color = Color.White) },
                                onClick = { viewModel.onCategoryChange(cat); expanded = false }
                            )
                        }
                    }
                }
            }

            SectionCard("الأسعار والمخزون") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MohaliTextField(
                        "سعر الشراء *", state.buyPrice,
                        { viewModel.onBuyPriceChange(it) },
                        Icons.Filled.AttachMoney,
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    MohaliTextField(
                        "سعر البيع *", state.sellPrice,
                        { viewModel.onSellPriceChange(it) },
                        Icons.Filled.Sell,
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MohaliTextField(
                        "الكمية *", state.quantity,
                        { viewModel.onQuantityChange(it) },
                        Icons.Filled.Inventory,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    MohaliTextField(
                        "حد التنبيه", state.minQuantity,
                        { viewModel.onMinQuantityChange(it) },
                        Icons.Filled.NotificationImportant,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            SectionCard("معلومات إضافية") {
                MohaliTextField("موقع في المحل", state.location, { viewModel.onLocationChange(it) }, Icons.Filled.LocationOn)
                MohaliTextField(
                    "ضمان (أشهر)", state.warrantyMonths,
                    { viewModel.onWarrantyChange(it) },
                    Icons.Filled.VerifiedUser,
                    keyboardType = KeyboardType.Number
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = { Text("الوصف / الملاحظات") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = mohaliTextFieldColors(),
                    minLines = 3, maxLines = 5
                )
            }

            // Profit indicator
            val buy = state.buyPrice.toDoubleOrNull() ?: 0.0
            val sell = state.sellPrice.toDoubleOrNull() ?: 0.0
            if (buy > 0 && sell > 0) {
                val profit = sell - buy
                val profitPct = (profit / buy) * 100
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (profit > 0) MohaliColors.Success.copy(0.1f) else MohaliColors.Error.copy(0.1f)
                    ),
                    border = BorderStroke(1.dp, if (profit > 0) MohaliColors.Success else MohaliColors.Error),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("هامش الربح", color = MohaliColors.OnSurfaceVariant, fontSize = 13.sp)
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "%.2f ر".format(profit),
                                color = if (profit > 0) MohaliColors.Success else MohaliColors.Error,
                                fontWeight = FontWeight.Bold, fontSize = 16.sp
                            )
                            Text("%.1f%%".format(profitPct), color = MohaliColors.Muted, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    viewModel.saveProduct {
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Accent),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Save, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isEdit) "حفظ التعديلات" else "إضافة المنتج", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = MohaliColors.AccentTeal, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            content()
        }
    }
}

@Composable
fun MohaliTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MohaliColors.OnSurfaceVariant, modifier = Modifier.size(20.dp)) },
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = mohaliTextFieldColors(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun mohaliTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MohaliColors.Accent,
    unfocusedBorderColor = MohaliColors.CardBorder,
    focusedLabelColor = MohaliColors.Accent,
    unfocusedLabelColor = MohaliColors.OnSurfaceVariant,
    cursorColor = MohaliColors.Accent,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = MohaliColors.SurfaceVariant,
    unfocusedContainerColor = MohaliColors.Surface
)
