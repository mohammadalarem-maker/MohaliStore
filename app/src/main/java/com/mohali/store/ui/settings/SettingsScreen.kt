package com.mohali.store.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.ui.inventory.mohaliTextFieldColors
import com.mohali.store.ui.theme.MohaliColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val storeName by viewModel.storeName.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val taxRate by viewModel.taxRate.collectAsState()
    val lowStockThreshold by viewModel.lowStockThreshold.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Store Settings
            SettingsCard("إعدادات المحل", Icons.Filled.Store) {
                SettingsTextField("اسم المحل", storeName, { viewModel.setStoreName(it) })
                SettingsTextField("العملة", currency, { viewModel.setCurrency(it) })
                SettingsTextField("نسبة الضريبة (%)", taxRate.toString(), { viewModel.setTaxRate(it.toDoubleOrNull() ?: 0.0) }, KeyboardType.Decimal)
            }

            // Notifications Settings
            SettingsCard("إعدادات الإشعارات", Icons.Filled.Notifications) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("تفعيل الإشعارات", color = Color.White, fontSize = 14.sp)
                        Text("إشعارات المبيعات ونفاد المخزون", color = MohaliColors.Muted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MohaliColors.Accent)
                    )
                }
                SettingsTextField("حد تنبيه نفاد المخزون (قطعة)", lowStockThreshold.toString(), { viewModel.setLowStockThreshold(it.toIntOrNull() ?: 5) }, KeyboardType.Number)
            }

            // About
            SettingsCard("حول التطبيق", Icons.Filled.Info) {
                InfoRow("اسم التطبيق", "محلي ستور")
                InfoRow("الإصدار", "1.0.0")
                InfoRow("المطور", "كلود")
                InfoRow("بواسطة", "محمد الصارم")
                InfoRow("البريد الإلكتروني", "Mohammedalsarem6@gmail.com")
                InfoRow("Firebase", "مفعّل ومتصل")
            }

            // Logout
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showLogoutConfirm = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Error.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, MohaliColors.Error.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Filled.Logout, null, tint = MohaliColors.Error)
                Spacer(Modifier.width(8.dp))
                Text("تسجيل الخروج", color = MohaliColors.Error, fontWeight = FontWeight.SemiBold)
            }

            // Footer
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("تطوير: كلود • بواسطة محمد الصارم", color = MohaliColors.Muted, fontSize = 11.sp)
                Text("جميع الحقوق محفوظة © 2024", color = MohaliColors.Muted, fontSize = 10.sp)
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            containerColor = MohaliColors.Card,
            title = { Text("تسجيل الخروج", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من تسجيل الخروج؟", color = MohaliColors.OnSurfaceVariant) },
            confirmButton = {
                Button(onClick = { viewModel.logout(); onLogout() }, colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Error)) {
                    Text("خروج", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("إلغاء", color = MohaliColors.OnSurfaceVariant) }
            }
        )
    }
}

@Composable
fun SettingsCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MohaliColors.AccentTeal, modifier = Modifier.size(20.dp))
                Text(title, color = MohaliColors.AccentTeal, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            content()
        }
    }
}

@Composable
fun SettingsTextField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = mohaliTextFieldColors(),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MohaliColors.OnSurfaceVariant, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
